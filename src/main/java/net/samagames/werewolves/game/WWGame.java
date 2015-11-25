package net.samagames.werewolves.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import net.samagames.api.games.Game;
import net.samagames.tools.Titles;
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
	protected List<WWPlayer> deaths;
	protected BukkitTask passtask;
	protected Map<UUID, UUID> votes;
	
	protected WWGame(WWPlugin plugin)
	{
		super("werewolves", "Loups Garous", "Inspiré du vrai jeu de cartes", WWPlayer.class);
		this.plugin = plugin;
		this.state = GameState.WAITING;
		world = plugin.getServer().getWorlds().get(0);
		deaths = new ArrayList<WWPlayer>();
		passtask = null;
		votes = new HashMap<UUID, UUID>();
	}

	public void giveWaitingInventory(Player p)
	{
		Inventory inv = p.getInventory();
		inv.clear();
		inv.setItem(4, ItemsUtil.setItemMeta(Material.NETHER_STAR, 1, (short)0, ChatColor.AQUA + "" + ChatColor.BOLD + "Sélecteur", null));
		inv.setItem(7, RulesUtil.getRulesBook());
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
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
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
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
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
	}
	
	public void giveSpectatingInventory(Player p)
	{
		Inventory inv = p.getInventory();
		inv.clear();
		inv.setItem(7, RulesUtil.getRulesBook());
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
	}
	
	public void giveVotingInventory(Player p)
	{
		Inventory inv = p.getInventory();
		inv.clear();
		inv.setItem(0, ItemsUtil.SELECTOR);
		inv.setItem(7, RulesUtil.getRulesBook());
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
	}
	
	@Override
	public void startGame()
	{
		if (this.isGameStarted())
			return ;
		super.startGame();
		this.state = GameState.PREPARE;
		selectRoles();
		Bukkit.getScheduler().runTaskLater(plugin, () -> startNight(), 80);
	}
	
	public void nextNightEvent()
	{
		this.cancelPassTask();
		WWClass[] classes = WWClass.NIGHT_ORDER;
		if (currentevent >= 0)
		{
			Set<WWPlayer> oldplayers = this.getPlayersByClass(classes[currentevent]);
			WWDisguise olddisguise = classes[currentevent].getDisguise();
			for (WWPlayer player : oldplayers)
			{
				Player p = player.getPlayerIfOnline();
				player.getHouse().teleportToBed(p);
				if (olddisguise != null)
					olddisguise.undisguisePlayer(p);
				giveSleepingInventory(player);
			}
			classes[currentevent].handleNightTurnEnd(plugin, oldplayers);
		}
		currentevent++;
		if (currentevent >= classes.length)
		{
			showDeads();
			startDay();
			return ;
		}
		Set<WWPlayer> players = this.getPlayersByClass(classes[currentevent]);
		if (players.isEmpty())
		{
			nextNightEvent();
			return ;
		}
		broadcastMessage(this.getCoherenceMachine().getGameTag() + ChatColor.WHITE + " " + classes[currentevent].getPrefix() + " " + classes[currentevent].getName() + ChatColor.WHITE + " se réveille" + (classes[currentevent].getPrefix().equals("Les") ? "nt" : "") + " !");
		String n = classes[currentevent].getTextAtNight();
		WWDisguise disguise = classes[currentevent].getDisguise();
		for (WWPlayer player : players)
		{
			Player p = player.getPlayerIfOnline();
			player.getHouse().removeFromBed(p);
			p.teleport(plugin.getRandomSpawn());
			if (n != null)
				Titles.sendTitle(p, 5, 50, 5, "", ChatColor.GOLD + n);
			if (disguise != null)
				disguise.disguisePlayer(p);
			givePlayingInventory(player);
		}
		classes[currentevent].handleNightTurnStart(plugin, players);
		passtask = plugin.getServer().getScheduler().runTaskTimer(plugin, new TurnPassTask(plugin, classes[currentevent], true), 20L, 20L);
	}
	
	public void nextDayEvent()
	{
		this.cancelPassTask();
		currentevent++;
		if (currentevent > 0)
		{
			List<UUID> tops = getTopVotes(votes);
			if (currentevent == 2 || tops.size() == 1)
			{
				if (tops.size() == 1)
				{
					WWPlayer player = this.getPlayer(tops.get(0));
					if (player != null && player.isOnline() && !player.isSpectator() && !player.isModerator())
						deaths.add(player);
				}
				showDeads();
				startNight();
				return ;
			}
			broadcastMessage(coherenceMachine.getGameTag() + ChatColor.WHITE + " Aucun choix de fait, un deuxième vote sera nécessaire !");
			for (UUID uuid : tops)
			{
				WWPlayer player = getPlayer(uuid);
				if (player != null && player.isOnline() && !player.isSpectator() && !player.isModerator())
					player.setSecondTurn(true);
			}
		}
		votes.clear();
		for (WWPlayer player : this.getInGamePlayers().values())
		{
			if (!(player.isModerator() || !player.isOnline() || player.isSpectator()))
				votes.put(player.getUUID(), null);
		}
		passtask = plugin.getServer().getScheduler().runTaskTimer(plugin, new TurnPassTask(plugin, 90, false), 20L, 20L);
	}
	
	public void handleDayVote(WWPlayer source, WWPlayer target)
	{
		if (getGameState() != GameState.DAY_1 && getGameState() != GameState.DAY_2)
			return ;
		if (getGameState() == GameState.DAY_2 && !target.isInSecondTurn())
			return ;
		if (votes.containsKey(source.getUUID()))
		{
			votes.put(source.getUUID(), target.getUUID());
			broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.WHITE + " " + ChatColor.BOLD + source.getOfflinePlayer().getName() + ChatColor.WHITE + " a voté pour " + ChatColor.BOLD + target.getOfflinePlayer().getName());
		}
	}
	
	private void showDeads()
	{
		String day = this.getGameState() == GameState.NIGHT ? "cette nuit" : "aujourd'hui";
		if (deaths.isEmpty())
		{
			broadcastMessage(this.coherenceMachine.getGameTag() + " Personne n'est mort " + day + ".");
			return ;
		}
		StringBuilder sb = new StringBuilder(this.coherenceMachine.getGameTag() + " Victime" + (deaths.size() == 1 ? "" : "s") + (state == GameState.NIGHT ? " de " + day : " d'" + day) + " : ");
		int i = 0;
		for (WWPlayer player : deaths)
		{
			player.setSpectator();
			Player p = player.getPlayerIfOnline();
			if (p != null)
				p.getWorld().strikeLightningEffect(p.getLocation());
			if (i > 0)
				sb.append(ChatColor.WHITE + ", ");
			sb.append(ChatColor.YELLOW + player.getOfflinePlayer().getName());
			i++;
		}
		broadcastMessage(sb.toString());
		deaths.clear();
	}

	public void selectRoles()
	{
		Map<WWClass, Integer> list = plugin.getRoles();
		List<WWHouse> houses = plugin.getHouses();
		Random r = new Random();
		for (WWPlayer player : this.getInGamePlayers().values())
		{
			if (player.isSpectator() || player.isModerator() || !player.isOnline())
				continue ;
			WWClass newclass = null;
			int n = r.nextInt(list.size());
			int i = 0;
			for (WWClass clazz : list.keySet())
			{
				if (n == i)
				{
					newclass = clazz;
					break ;
				}
				i++;
			}
			player.setPlayedClass(newclass);
			Titles.sendTitle(player.getPlayerIfOnline(), 5, 70, 5, "", ChatColor.GOLD + "Vous êtes : " + newclass.getName());
			n = list.get(newclass);
			if (n > 1)
				list.put(newclass, n - 1);
			else
				list.remove(newclass);
			giveSleepingInventory(player);
			n = r.nextInt(houses.size());
			player.setHouse(houses.get(n));
			player.getHouse().displayName(player.getOfflinePlayer().getName());
			houses.remove(n);
		}
	}
	
	public Set<WWPlayer> getPlayersByClass(WWClass... clazz)
	{
		Set<WWPlayer> set = new HashSet<WWPlayer>();
		for (WWPlayer player : this.getInGamePlayers().values())
		{
			if (player.isSpectator() || player.isModerator() || !player.isOnline())
				continue ;
			for (WWClass tmp : clazz)
				if (player.getPlayedClass() != null && player.getPlayedClass().equals(tmp))
				{
					set.add(player);
					break ;
				}
		}
		return set;
	}
	
	public Set<WWPlayer> getPlayersByWinType(WinType... types)
	{
		Set<WWPlayer> set = new HashSet<WWPlayer>();
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
		world.setTime(15000L);
		currentevent = -1;
		for (WWPlayer player : this.getInGamePlayers().values())
		{
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
		world.setTime(3000L);
		currentevent = -1;
		broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.WHITE + " Le jour vient de se lever !");
		broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.WHITE + " Il est temps de voter pour savoir qui vous allez tuer aujourd'hui.");
		nextDayEvent();
	}
	
	private boolean checkEnd()
	{
		Map<WWClass, Integer> roles = new HashMap<WWClass, Integer>();
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
		int result = 0; // Check innocent & wolves
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
		if (total == 2)
		{
			//check if couple
		}
		if ((result & 1) == 1 && (result & 2) == 0 && (result & 4) == 0)
		{
			//innocent win
		}
		if ((result & 1) == 0 && (result & 2) == 1 && (result & 4) == 0)
		{
			//wolves win
		}
		if (total == 1 && (result & 4) == 1)
		{
			//alone win
		}
		return false;
	}
	
	@Override
	public void handleLogin(Player player)
	{
		super.handleLogin(player);
		giveWaitingInventory(player);
		player.teleport(plugin.getRandomSpawn());
	}
	
	@Override
	public void handleLogout(Player player)
	{
		super.handleLogout(player);
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
		for (Player player : plugin.getServer().getOnlinePlayers())
			player.sendMessage(msg);
		plugin.getServer().getConsoleSender().sendMessage(msg);
	}
	
	public boolean isCurrentlyPlayed(WWClass clazz)
	{
		if (getGameState() != GameState.NIGHT)
			return false;
		return WWClass.NIGHT_ORDER[currentevent].equals(clazz);
	}
	
	public List<WWPlayer> getDeadPlayers()
	{
		return deaths;
	}
	
	public void cancelPassTask()
	{
		if (passtask == null)
			return ;
		passtask.cancel();
		passtask = null;
	}
	
	public List<UUID> getTopVotes(Map<UUID, UUID> list)
	{
		Map<UUID, Integer> counts = new HashMap<UUID, Integer>();
		for (Iterator<Entry<UUID, UUID>> it = list.entrySet().iterator(); it.hasNext();)
		{
			Entry<UUID, UUID> entry = it.next();
			Integer i = counts.get(entry.getValue());
			if (i == null)
				i = 0;
			i++;
			counts.put(entry.getValue(), i);
		}
		List<UUID> tops = new ArrayList<UUID>();
		int top = 0;
		for (Iterator<Entry<UUID, Integer>> it = counts.entrySet().iterator(); it.hasNext();)
		{
			Entry<UUID, Integer> entry = it.next();
			if (entry.getValue() > top)
			{
				tops.clear();
				top = entry.getValue();
			}
			if (entry.getValue() == top)
				tops.add(entry.getKey());
		}
		return tops;
	}
	
	public abstract void handleChatMessage(WWPlayer player, String message);
}
