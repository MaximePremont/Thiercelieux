package net.samagames.werewolves.classes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.samagames.tools.Titles;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.entities.WereWolfDisguise;
import net.samagames.werewolves.game.WWPlayer;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
		super("werewolf", "Les", "&8&lLoup-Garou", new ItemStack(Material.ROTTEN_FLESH), new String[]{"La nuit, décidez d'une victime à dévorer !"}, new WereWolfDisguise());
		choices = new HashMap<UUID, UUID>();
	}

	@Override
	public boolean canPlayAtNight()
	{
		return true;
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
		choices.clear();
		for (WWPlayer player : players)
			choices.put(player.getUUID(), null);
		Set<WWPlayer> receivers = plugin.getGame().getPlayersByClass(WWClass.LITTLE_GIRL);
		for (WWPlayer wwp : receivers)
			if (wwp.isOnline())
				Titles.sendTitle(wwp.getPlayerIfOnline(), 5, 50, 5, "", WWClass.LITTLE_GIRL.getTextAtNight());
	}
	
	@Override
	public void handleNightTurnEnd(WWPlugin plugin, Set<WWPlayer> players)
	{
		this.players.clear();
		Pair<UUID, Integer> max = new MutablePair<UUID, Integer>(null, null);
		Collection<UUID> values = choices.values();
		for (UUID tmp : values)
		{
			if (tmp == null)
				continue ;
			int n = 0;
			for (UUID tmp2 : values)
				if (tmp.equals(tmp2))
					n++;
			if (max.getLeft() != null && max.getRight() != null && n == max.getRight() && !max.getLeft().equals(tmp))
			{
				max = new MutablePair<UUID, Integer>(null, null);
				break ;
			}
			if (max.getLeft() == null || max.getRight() == null || n > max.getRight())
				max = new MutablePair<UUID, Integer>(tmp, n);
		}
		String msg;
		if (max.getLeft() == null || max.getRight() == null)
			msg = ChatColor.RED + "Aucun choix de fait, il n'y aura pas de victime des loups-garous ce soir.";
		else
		{
			WWPlayer player = plugin.getGame().getPlayer(max.getLeft());
			if (player == null || !player.isOnline())
				msg = ChatColor.RED + "Le joueur choisi s'est déconnecté, il n'y aura pas de victime des loups-garous ce soir.";
			else
			{
				plugin.getGame().getDeadPlayers().add(player);
				msg = plugin.getGame().getCoherenceMachine().getGameTag() + ChatColor.RED + " La victime de ce soir des loups-garous est : " + player.getOfflinePlayer().getName() + " !";
			}
		}
		for (WWPlayer wwp : players)
			if (wwp.isOnline())
				wwp.getPlayerIfOnline().sendMessage(msg);
		choices.clear();
	}
	
	@Override
	public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target)
	{
		if (target.isSpectator() || target.isModerator() || !target.isOnline())
		{
			source.getPlayerIfOnline().sendMessage(ChatColor.RED + "Ce joueur est déconnecté !");
			return ;
		}
		if (choices.containsKey(source.getUUID()))
		{
			choices.put(source.getUUID(), target.getUUID());
			for (WWPlayer wwp : players)
			{
				String msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + source.getOfflinePlayer().getName() + " a voté pour " + target.getOfflinePlayer().getName();
				Player player = wwp.getPlayerIfOnline();
				if (player != null)
					player.sendMessage(msg);
			}
		}
	}
}
