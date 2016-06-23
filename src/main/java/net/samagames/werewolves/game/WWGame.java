package net.samagames.werewolves.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.samagames.api.games.Game;
import net.samagames.api.games.Status;
import net.samagames.api.games.themachine.messages.Message;
import net.samagames.tools.Titles;
import net.samagames.tools.chat.ChatUtils;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.entities.WWDisguise;
import net.samagames.werewolves.task.TurnPassTask;
import net.samagames.werewolves.util.GameState;
import net.samagames.werewolves.util.ItemsUtil;
import net.samagames.werewolves.util.RulesUtil;
import net.samagames.werewolves.util.WinType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

public abstract class WWGame extends Game<WWPlayer>
{
    protected WWPlugin plugin;
    protected GameState state;
    protected World world;
    protected int currentevent;
    protected Map<WWPlayer, WWClass> deaths;
    protected BukkitTask passtask;
    protected Map<UUID, UUID> votes;

    protected WWGame(WWPlugin plugin)
    {
        super("werewolves", "Loups Garous", "Inspiré du vrai jeu de cartes", WWPlayer.class);
        this.plugin = plugin;
        this.state = GameState.WAITING;
        world = plugin.getServer().getWorlds().get(0);
        deaths = new HashMap<>();
        passtask = null;
        votes = new HashMap<>();
    }

    public void giveWaitingInventory(Player p)
    {
        Inventory inv = p.getInventory();
        inv.clear();
        inv.setItem(4, ItemsUtil.setItemMeta(Material.NETHER_STAR, 1, (short)0, ChatColor.AQUA + "" + ChatColor.BOLD + "Sélecteur", null));
        inv.setItem(7, RulesUtil.getRulesBook());
        inv.setItem(8, this.plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
    }

    public void givePlayingInventory(WWPlayer wwp)
    {
        Player p = wwp.getPlayerIfOnline();
        if (p == null)
            return ;
        Inventory inv = p.getInventory();
        inv.clear();
        if (wwp.getPlayedClass() != null)
            inv.setItem(4, wwp.getPlayedClass().getItem());
        if (wwp.getPlayedClass() != null && wwp.getPlayedClass().hasSelector())
            inv.setItem(0, ItemsUtil.SELECTOR);
        inv.setItem(7, RulesUtil.getRulesBook());
        inv.setItem(8, this.plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
    }

    public void giveSleepingInventory(WWPlayer wwp)
    {
        Player p = wwp.getPlayerIfOnline();
        if (p == null)
            return ;
        Inventory inv = p.getInventory();
        inv.clear();
        if (wwp.getPlayedClass() != null)
            inv.setItem(4, wwp.getPlayedClass().getItem());
        inv.setItem(7, RulesUtil.getRulesBook());
        inv.setItem(8, this.plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
    }

    public void giveVotingInventory(Player p)
    {
        Inventory inv = p.getInventory();
        inv.clear();
        inv.setItem(0, ItemsUtil.SELECTOR);
        inv.setItem(7, RulesUtil.getRulesBook());
        inv.setItem(8, this.plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
    }

    @Override
    public void startGame()
    {
        if (this.isGameStarted())
            return ;
        super.startGame();
        this.state = GameState.PREPARE;
        selectRoles();
        Bukkit.getScheduler().runTaskLater(this.plugin, this::startNight, 80);
    }

    public void nextNightEvent()
    {
        this.cancelPassTask();
        WWClass[] classes = WWClass.getNightOrder();
        if (this.currentevent >= 0 && this.currentevent < classes.length)
        {
            Set<WWPlayer> oldPlayers = this.getPlayersByClass(classes[this.currentevent]);
            if (!oldPlayers.isEmpty() && !classes[this.currentevent].isDisabled() && classes[this.currentevent].canPlayAtNight())
            {
                WWDisguise oldDisguise = classes[this.currentevent].getDisguise();
                for (WWPlayer player : oldPlayers)
                {
                    Player p = player.getPlayerIfOnline();
                    player.getHouse().teleportToBed(p);
                    if (oldDisguise != null)
                        oldDisguise.undisguisePlayer(p);
                    giveSleepingInventory(player);
                }
                classes[this.currentevent].handleNightTurnEnd(plugin, oldPlayers);
            }
        }
        this.currentevent++;
        if (this.currentevent >= classes.length)
        {
            if (showDeads())
                startDay();
            return ;
        }
        Set<WWPlayer> players = this.getPlayersByClass(classes[this.currentevent]);
        if (players.isEmpty() || classes[this.currentevent].isDisabled() || !classes[this.currentevent].canPlayAtNight())
        {
            nextNightEvent();
            return ;
        }
        broadcastMessage(this.getCoherenceMachine().getGameTag() + ChatColor.WHITE + " " + classes[this.currentevent].getPrefix() + " " + classes[this.currentevent].getName() + ChatColor.WHITE + " se réveille" + ("Les".equals(classes[this.currentevent].getPrefix()) ? "nt" : "") + " !");
        String n = classes[this.currentevent].getTextAtNight();
        WWDisguise disguise = classes[this.currentevent].getDisguise();
        for (WWPlayer player : players)
        {
            Player p = player.getPlayerIfOnline();
            player.getHouse().removeFromBed(p);
            p.teleport(this.plugin.getRandomSpawn());
            if (n != null)
                Titles.sendTitle(p, 5, 50, 5, "", ChatColor.GOLD + n);
            if (disguise != null)
                disguise.disguisePlayer(p);
            givePlayingInventory(player);
        }
        classes[this.currentevent].handleNightTurnStart(this.plugin, players);
        this.passtask = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new TurnPassTask(this.plugin, classes[this.currentevent], true), 20L, 20L);
    }

    public void nextDayEvent()
    {
        this.cancelPassTask();
        this.currentevent++;
        if (this.currentevent > 0)
        {
            List<UUID> tops = getTopVotes(votes);
            if (this.currentevent == 2 || tops.size() == 1)
            {
                if (tops.size() == 1)
                {
                    WWPlayer player = this.getPlayer(tops.get(0));
                    if (player != null && player.isOnline() && !player.isSpectator() && !player.isModerator())
                        diePlayer(player, null);
                }
                if (showDeads())
                    startNight();
                return ;
            }
            if (tops.isEmpty())
                broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.WHITE + " Aucun choix de fait, un deuxième vote sera nécessaire !");
            else
                broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.WHITE + " Egalité dans les voix, un deuxième vote sera nécessaire !");
            for (UUID uuid : tops)
            {
                WWPlayer player = getPlayer(uuid);
                if (player != null && player.isOnline() && !player.isSpectator() && !player.isModerator())
                    player.setSecondTurn(true);
            }
        }
        this.votes.clear();
        this.getInGamePlayers().values().stream().filter(player -> !(player.isModerator() || !player.isOnline() || player.isSpectator())).forEach(player -> this.votes.put(player.getUUID(), null));
        this.passtask = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new TurnPassTask(this.plugin, 90, false), 20L, 20L);
    }

    public void handleDayVote(WWPlayer source, WWPlayer target)
    {
        if (getGameState() != GameState.DAY_1 && getGameState() != GameState.DAY_2)
            return ;
        if (getGameState() == GameState.DAY_2 && !target.isInSecondTurn())
            return ;
        if (this.votes.containsKey(source.getUUID()))
        {
            this.votes.put(source.getUUID(), target.getUUID());
            broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.WHITE + " " + ChatColor.BOLD + source.getDisplayName() + ChatColor.WHITE + " a voté pour " + ChatColor.BOLD + target.getDisplayName());
        }
    }

    private boolean showDeads()
    {
        String day = this.getGameState() == GameState.NIGHT ? "cette nuit" : "aujourd'hui";
        if (this.deaths.isEmpty())
        {
            broadcastMessage(this.coherenceMachine.getGameTag() + " Personne n'est mort " + day + ".");
            return true;
        }
        List<WWPlayer> lovers = new ArrayList<>();
        StringBuilder sb = new StringBuilder(this.coherenceMachine.getGameTag() + " Victime" + (this.deaths.size() == 1 ? "" : "s") + (state == GameState.NIGHT ? " de " + day : " d'" + day) + " : ");
        int i = 0;
        for (WWPlayer player : this.deaths.keySet())
        {
            player.setSpectator();
            if (player.isInCouple() && !this.deaths.containsKey(player.getCouple()))
                lovers.add(player.getCouple());
            Player p = player.getPlayerIfOnline();
            if (p != null)
                p.getWorld().strikeLightningEffect(p.getLocation());
            if (i > 0)
                sb.append(ChatColor.WHITE).append(", ");
            sb.append(ChatColor.YELLOW).append(player.getDisplayName());
            i++;
        }
        broadcastMessage(sb.toString());
        boolean ok = true;
        for (Entry<WWPlayer, WWClass> entry : this.deaths.entrySet())
            if (entry.getKey().getPlayedClass() != null && !entry.getKey().getPlayedClass().handleDeath(this.plugin, entry.getKey(), entry.getValue()))
                ok = false;
        for (WWPlayer player : lovers)
        {
            player.setSpectator();
            Player p = player.getPlayerIfOnline();
            if (p != null)
                p.getWorld().strikeLightningEffect(p.getLocation());
            broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.YELLOW + " " + player.getDisplayName() + ChatColor.WHITE + " était amoureux de " + ChatColor.YELLOW + player.getCouple().getDisplayName() + ChatColor.WHITE + " et se suicide donc par amour.");
        }
        this.deaths.clear();
        return ok;
    }

    public void selectRoles()
    {
        Map<WWClass, Integer> list = this.plugin.getRoles();
        List<WWHouse> houses = this.plugin.getHouses();
        Random r = new Random();
        for (WWPlayer player : this.getInGamePlayers().values())
        {
            if (player.isSpectator() || player.isModerator() || !player.isOnline())
                continue ;
            WWClass newClass = null;
            int n = r.nextInt(list.size());
            int i = 0;
            for (WWClass clazz : list.keySet())
            {
                if (n == i)
                {
                    newClass = clazz;
                    break ;
                }
                i++;
            }
            player.setPlayedClass(newClass);
            Player p = player.getPlayerIfOnline();
            if (newClass != null)
            {
                Titles.sendTitle(p, 5, 70, 5, "", ChatColor.GOLD + "Vous êtes : " + newClass.getName());
                p.sendMessage(this.coherenceMachine.getGameTag() + ChatColor.GOLD + " Vous êtes : " + newClass.getName());
            }
            p.setExp(0);
            p.setLevel(0);
            n = list.get(newClass);
            if (n > 1)
                list.put(newClass, n - 1);
            else
                list.remove(newClass);
            giveSleepingInventory(player);
            n = r.nextInt(houses.size());
            player.setHouse(houses.get(n));
            player.getHouse().displayName(player.getOfflinePlayer().getName());
            houses.remove(n);
        }
    }

    public Set<WWPlayer> getPlayersByClass(WWClass... clazz)
    {
        Set<WWPlayer> set = new HashSet<>();
        for (WWPlayer player : this.getInGamePlayers().values())
        {
            if (player.isSpectator() || player.isModerator() || !player.isOnline())
                continue ;
            for (WWClass tmp : clazz)
                if (player.getPlayedClass() != null && tmp.getClass().isAssignableFrom(player.getPlayedClass().getClass()))
                {
                    set.add(player);
                    break ;
                }
        }
        return set;
    }

    public Set<WWPlayer> getPlayersByWinType(WinType... types)
    {
        Set<WWPlayer> set = new HashSet<>();
        for (WWPlayer player : this.getInGamePlayers().values())
        {
            if (player.isSpectator() || player.isModerator() || !player.isOnline())
                continue ;
            for (WinType tmp : types)
                if (player.getPlayedClass() != null && player.getPlayedClass().getWinType().equals(tmp))
                {
                    set.add(player);
                    break ;
                }
        }
        return set;
    }

    public void startNight()
    {
        if (checkEnd())
            return ;
        this.state = GameState.NIGHT;
        this.world.setTime(15000L);
        this.currentevent = -1;
        for (WWPlayer player : this.getInGamePlayers().values())
        {
            player.setProtected(false);
            if (player.isSpectator() || player.isModerator() || !player.isOnline() || player.getHouse() == null)
                continue ;
            player.setSecondTurn(false);
            Player p = player.getPlayerIfOnline();
            if (p == null)
                continue ;
            player.getHouse().teleportToBed(p);
        }
        broadcastMessage(this.getCoherenceMachine().getGameTag() + ChatColor.WHITE + " La nuit tombe sur SamaVille...");
        nextNightEvent();
    }

    public void startDay()
    {
        if (checkEnd())
            return ;
        this.state = GameState.DAY_1;
        for (WWPlayer player : this.getInGamePlayers().values())
        {
            if (player.isSpectator() || player.isModerator() || !player.isOnline() || player.getHouse() == null)
                continue ;
            Player p = player.getPlayerIfOnline();
            player.getHouse().removeFromBed(p);
            p.teleport(plugin.getRandomSpawn());
            giveVotingInventory(p);
        }
        this.world.setTime(3000L);
        this.currentevent = -1;
        broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.WHITE + " Le jour vient de se lever !");
        broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.WHITE + " Il est temps de voter pour savoir qui vous allez tuer aujourd'hui.");
        nextDayEvent();
    }

    public boolean checkEnd()
    {
        if (this.gamePlayers.size() == 1) //Just for debug
            return false;
        Map<WWClass, Integer> roles = new HashMap<>();
        for (WWPlayer player : this.getInGamePlayers().values())
        {
            if (player.isSpectator() || player.isModerator() || !player.isOnline() || player.getPlayedClass() == null)
                continue ;
            Integer i = roles.get(player.getPlayedClass());
            if (i == null)
                i = 0;
            i++;
            roles.put(player.getPlayedClass(), i);
        }
        Set<WWClass> classes = roles.keySet();
        byte result = 0;
        int total = 0;
        for (WWClass clazz : classes)
        {
            total += roles.get(clazz);
            if (clazz.getWinType() == WinType.INNOCENTS)
                result |= 1;
            else if (clazz.getWinType() == WinType.WOLVES)
                result |= 2;
            else if (clazz.getWinType() == WinType.ALONE)
                result |= 4;
        }
        if (total == 0)
        {
            ArrayList<String> list = new ArrayList<>();
            list.add("Tout le monde a perdu, il n'y a plus personne en vie dans le village...");
            list.add("Les bâtiments resteront abandonnées et tomberont en ruine bientôt.");
            this.coherenceMachine.getTemplateManager().getBasicMessageTemplate().execute(list);
            finishGame();
            return true;
        }
        if (total == 2)
        {
            WWPlayer[] players = new WWPlayer[2];
            int i = 0;
            for (WWClass clazz : classes)
            {
                Set<WWPlayer> tmp = this.getPlayersByClass(clazz);
                for (WWPlayer wwp : tmp)
                {
                    players[i] = wwp;
                    i++;
                }
            }
            if (players[0].isInCouple() && players[1].isInCouple() && players[0].getCouple().equals(players[1]))
            {
                ArrayList<String> list = new ArrayList<>();
                list.add(ChatUtils.getCenteredText("Le couple (" + players[0].getDisplayName() + ChatColor.WHITE + " & " + players[1].getDisplayName() + ChatColor.WHITE + ") a gagné !"));
                list.add(ChatUtils.getCenteredText("Ils vivront heureux et auront beaucoup d'enfants <3"));
                this.coherenceMachine.getTemplateManager().getBasicMessageTemplate().execute(list);
                for (WWPlayer p : players)
                    p.win();
                finishGame();
                return true;
            }
        }
        if (result == 1)
        {
            ArrayList<String> list = new ArrayList<>();
            list.add(ChatUtils.getCenteredText("Les villageois ont gagnés !"));
            list.add(ChatUtils.getCenteredText("Le village est sauvé !"));
            this.coherenceMachine.getTemplateManager().getBasicMessageTemplate().execute(list);
            Set<WWPlayer> players = new HashSet<>();
            for (WWClass clazz : WWClass.getValues())
                if (clazz.getWinType() == WinType.INNOCENTS)
                {
                    Set<WWPlayer> tmp = this.getPlayersByClass(clazz);
                    players.addAll(tmp);
                }
            players.forEach(WWPlayer::win);
            finishGame();
            return true;
        }
        if (result == 2)
        {
            ArrayList<String> list = new ArrayList<>();
            list.add(ChatUtils.getCenteredText(ChatColor.YELLOW + "Les loups ont gagné !"));
            list.add(ChatUtils.getCenteredText(ChatColor.YELLOW + "Tout le village a été dévoré !"));
            this.coherenceMachine.getTemplateManager().getBasicMessageTemplate().execute(list);
            Set<WWPlayer> players = new HashSet<>();
            for (WWClass clazz : WWClass.getValues())
                if (clazz.getWinType() == WinType.WOLVES)
                {
                    Set<WWPlayer> tmp = this.getPlayersByClass(clazz);
                    players.addAll(tmp);
                }
            players.forEach(WWPlayer::win);
            finishGame();
            return true;
        }
        if (total == 1 && result == 4)
        {
            WWPlayer player = null;
            for (WWClass clazz : WWClass.getValues())
                if (clazz.getWinType() == WinType.ALONE)
                    player = player == null ? this.getPlayersByClass(clazz).stream().findFirst().orElse(null) : player;
            if (player == null)
                return false;
            ArrayList<String> list = new ArrayList<>();
            list.add(ChatUtils.getCenteredText(ChatColor.YELLOW + player.getPlayedClass().getPrefix() + player.getPlayedClass().getName() + ChatColor.WHITE + "(" + player.getDisplayName() + ChatColor.WHITE + ") a gagné !"));
            list.add(ChatUtils.getCenteredText(ChatColor.YELLOW + "Il / Elle est le dernier survivant en vie."));
            this.coherenceMachine.getTemplateManager().getBasicMessageTemplate().execute(list);
            player.win();
            finishGame();
            return true;
        }
        return false;
    }

    public void finishGame()
    {
        this.state = GameState.END;
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, this::handleGameEnd, 30);
    }

    @Override
    public void handleLogin(Player player)
    {
        super.handleLogin(player);
        giveWaitingInventory(player);
        player.teleport(this.plugin.getRandomSpawn());
        player.setExp(0);
    }

    @Override
    public void handleLogout(Player player)
    {
        WWPlayer wwp = this.getPlayer(player.getUniqueId());
        if (getStatus() != Status.IN_GAME || wwp == null || wwp.isSpectator() || wwp.isModerator() || wwp.getPlayedClass() == null)
        {
            super.handleLogout(player);
            return ;
        }
        if(this.gamePlayers.containsKey(player.getUniqueId()))
            this.gamePlayers.remove(player.getUniqueId()); //Remove disconnect message
        super.handleLogout(player);
        new Message(ChatColor.WHITE + player.getDisplayName() + " s'est déconnecté du jeu. Son rôle était : " + wwp.getPlayedClass().getName(), this.coherenceMachine.getGameTag()).displayToAll();
        checkEnd();
    }

    public GameState getGameState()
    {
        return state;
    }

    public void setGameState(GameState state)
    {
        this.state = state;
    }

    public void broadcastMessage(String msg)
    {
        for (Player player : this.plugin.getServer().getOnlinePlayers())
            player.sendMessage(msg);
        this.plugin.getServer().getConsoleSender().sendMessage(msg);
    }

    public boolean isCurrentlyPlayed(WWClass clazz)
    {
        return getGameState() == GameState.NIGHT && WWClass.getNightOrder()[this.currentevent].equals(clazz);
    }

    public Set<WWPlayer> getDeadPlayers()
    {
        return this.deaths.keySet();
    }

    public void cancelPassTask()
    {
        if (this.passtask == null)
            return ;
        this.passtask.cancel();
        this.passtask = null;
    }

    public List<UUID> getTopVotes(Map<UUID, UUID> list)
    {
        Map<UUID, Integer> counts = new HashMap<>();
        for (Entry<UUID, UUID> entry : list.entrySet())
        {
            if (entry.getValue() == null)
                continue;
            Integer i = counts.get(entry.getValue());
            if (i == null)
                i = 0;
            i++;
            counts.put(entry.getValue(), i);
        }
        List<UUID> tops = new ArrayList<>();
        int top = 0;
        for (Entry<UUID, Integer> entry : counts.entrySet())
        {
            if (entry.getValue() > top) {
                tops.clear();
                top = entry.getValue();
            }
            if (entry.getValue() == top)
                tops.add(entry.getKey());
        }
        return tops;
    }

    public void diePlayer(WWPlayer player, WWClass killer)
    {
        if (killer == null)
            this.deaths.put(player, null);
        else if (player.getPlayedClass() != null && player.getPlayedClass().canBeKilled(player, killer))
            this.deaths.put(player, killer);
    }

    public abstract void handleChatMessage(WWPlayer player, String message);
}
