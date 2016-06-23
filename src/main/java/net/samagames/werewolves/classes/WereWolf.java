package net.samagames.werewolves.classes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.samagames.api.games.GamePlayer;
import net.samagames.tools.Titles;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.entities.SkinDisguise;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.WinType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WereWolf extends WWClass
{
    private Map<UUID, UUID> choices;
    private Set<WWPlayer> players;

    public WereWolf()
    {
        super("werewolf", "Les", "&8&lLoup-Garou", new ItemStack(Material.ROTTEN_FLESH), new String[]{"La nuit, décidez d'une victime à dévorer. Miam"}, new SkinDisguise("da508ecc-dbd9-46c5-8095-47b91aa4ff5f"));
        this.choices = new HashMap<>();
    }

    @Override
    public boolean canPlayAtNight()
    {
        return true;
    }

    @Override
    public WinType getWinType()
    {
        return WinType.WOLVES;
    }

    @Override
    public String getTextAtNight()
    {
        return "Choisissez la personne que vous voulez dévorer !";
    }

    @Override
    public void handleNightTurnStart(WWPlugin plugin, Set<WWPlayer> players)
    {
        this.players = players;
        this.choices.clear();
        for (WWPlayer player : players)
            this.choices.put(player.getUUID(), null);
        Set<WWPlayer> receivers = plugin.getGame().getPlayersByClass(WWClass.LITTLE_GIRL);
        receivers.stream().filter(GamePlayer::isOnline).forEach(wwp -> Titles.sendTitle(wwp.getPlayerIfOnline(), 5, 50, 5, "", WWClass.LITTLE_GIRL.getTextAtNight()));
    }

    @Override
    public void handleNightTurnEnd(WWPlugin plugin, Set<WWPlayer> players)
    {
        this.players.clear();
        List<UUID> tops = plugin.getGame().getTopVotes(this.choices);
        String msg;
        if (tops.size() != 1)
            msg = ChatColor.RED + "Aucun choix de fait, il n'y aura pas de victime des loups-garous ce soir.";
        else
        {
            WWPlayer player = plugin.getGame().getPlayer(tops.get(0));
            if (player == null || !player.isOnline())
                msg = ChatColor.RED + "Le joueur choisi s'est déconnecté, il n'y aura pas de victime des loups-garous ce soir.";
            else
            {
                if (!player.isProtected())
                    plugin.getGame().diePlayer(player, this);
                msg = plugin.getGame().getCoherenceMachine().getGameTag() + ChatColor.RED + " La victime de ce soir des loups-garous est : " + player.getDisplayName() + " !";
            }
        }
        players.stream().filter(GamePlayer::isOnline).forEach(wwp -> wwp.getPlayerIfOnline().sendMessage(msg));
        this.choices.clear();
    }

    @Override
    public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target)
    {
        if (target.isSpectator() || target.isModerator() || !target.isOnline())
        {
            source.getPlayerIfOnline().sendMessage(ChatColor.RED + "Ce joueur est déconnecté.");
            return ;
        }
        if (this.choices.containsKey(source.getUUID()))
        {
            this.choices.put(source.getUUID(), target.getUUID());
            for (WWPlayer wwp : this.players)
            {
                String msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + source.getDisplayName() + " a voté pour " + target.getDisplayName();
                Player player = wwp.getPlayerIfOnline();
                if (player != null)
                    player.sendMessage(msg);
            }
        }
    }
}
