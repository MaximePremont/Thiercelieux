package net.samagames.werewolves.classes;

import net.samagames.tools.Titles;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Seer extends WWClass
{
	private boolean locked;
	
	protected Seer()
	{
		super("seer", "La", "&5Voyante", new ItemStack(Material.EYE_OF_ENDER), new String[]{"Une fois par nuit, regardez le", "rôle d'un autre joueur"}, null);
		locked = false;
	}

	@Override
	public boolean canPlayAtNight()
	{
		return true;
	}

	@Override
	public String getTextAtNight()
	{
		return "Choisissez le joueur donc vous voulez voir la carte";
	}
	
	@Override
	public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target)
	{
		if (locked || target.isSpectator() || target.isModerator() || !target.isOnline())
			return ;
		locked = true;
		Titles.sendTitle(source.getPlayerIfOnline(), 5, 50, 5, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Rôle de " + target.getOfflinePlayer().getName(), target.getPlayedClass().getName());
		plugin.getGame().cancelPassTask();
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getGame().nextNightEvent(), 60);
	}
}
