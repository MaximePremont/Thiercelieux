package net.samagames.werewolves.classes;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.WinType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Salvator extends WWClass {

	protected Salvator()
	{
		super("salvator", "Le", "Salvateur", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new String[]{"Une fois par nuit, protégez quelqu'un", "de l'attaque des loups-garous."}, null);
	}

	@Override
	public boolean canPlayAtNight()
	{
		return true;
	}

	@Override
	public WinType getWinType()
	{
		return WinType.INNOCENTS;
	}

	@Override
	public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target)
	{
		if (!target.isOnline() || target.isModerator() || target.isSpectator())
			return ;
		Player p1 = source.getPlayerIfOnline();
		if (p1 != null)
			p1.sendMessage(plugin.getGame().getCoherenceMachine().getGameTag() + ChatColor.WHITE + " Vous avez protégé : " + ChatColor.YELLOW + target.getOfflinePlayer().getName());
		target.setProtected(true);
		plugin.getGame().nextNightEvent();
	}
}
