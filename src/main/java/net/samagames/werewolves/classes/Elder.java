package net.samagames.werewolves.classes;

import java.util.Set;
import java.util.UUID;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.WinType;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Elder extends WWClass
{
	private Set<UUID> protectedPlayers;
	
	protected Elder()
	{
		super("elder", "L'", "&eAncien", new ItemStack(Material.LOG), new String[]{}, null);
	}

	@Override
	public boolean canPlayAtNight()
	{
		return false;
	}

	@Override
	public WinType getWinType()
	{
		return WinType.INNOCENTS;
	}

	@Override
	public void handleNightTurnStart(WWPlugin plugin, Set<WWPlayer> players)
	{
		for (WWPlayer wwp : players)
			if (!protectedPlayers.contains(wwp.getUUID()))
				protectedPlayers.add(wwp.getUUID());
	}
	
	@Override
	public boolean canBeKilled(WWPlayer player, WWClass by)
	{
		if (!(by instanceof WereWolf))
			return true;
		if (!protectedPlayers.contains(player.getUUID()))
			return true;
		protectedPlayers.remove(player.getUUID());
		return false;
	}
	
	@Override
	public boolean handleDeath(WWPlugin plugin, WWPlayer player, WWClass by)
	{
		if (by == null || by.getWinType() == WinType.INNOCENTS)
		{
			for (WWPlayer wwp : plugin.getGame().getInGamePlayers().values())
			{
				if (!wwp.isOnline() || wwp.isModerator() || wwp.isSpectator())
					continue ;
				if (wwp.getPlayedClass() != null && wwp.getPlayedClass().getWinType() == WinType.INNOCENTS)
					wwp.getPlayedClass().setDisabled(true);
			}
			plugin.getGame().broadcastMessage(plugin.getGame().getCoherenceMachine().getGameTag() + " L'ancien " + player.getOfflinePlayer().getName() + " a été tué par des villageois, ceux-ci perdent donc tous leurs pouvoirs !");
		}
		return true;
	}
}
