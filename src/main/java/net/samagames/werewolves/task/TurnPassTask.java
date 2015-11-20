package net.samagames.werewolves.task;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.PacketUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TurnPassTask implements Runnable
{
	private WWPlugin plugin;
	private WWClass clazz;
	private boolean night;
	private int time;
	
	public TurnPassTask(WWPlugin plugin, WWClass clazz, boolean night)
	{
		this.plugin = plugin;
		this.clazz = clazz;
		this.night = night;
		time = clazz.getMaximumDelay();
	}
	
	@Override
	public void run()
	{
		time--;
		broadcastActionBarMessage(ChatColor.RED + "Temps restant : " + (time >= 600 ? "" : "0") + (time / 60) + ":" + (time % 60 < 10 ? "0" : "") + (time % 60));
		if (time <= 0 && plugin.getGame().isCurrentlyPlayed(clazz))
		{
			plugin.getGame().broadcastMessage(ChatColor.RED + "Temps écoulé !");
			broadcastActionBarMessage(ChatColor.RED + "Temps écoulé !");
			if (night)
				plugin.getGame().nextNightEvent();
			//else
			//	plugin.getGame().nextDayEvent();
		}
	}
	
	private void broadcastActionBarMessage(String msg)
	{
		for (WWPlayer player : plugin.getGame().getInGamePlayers().values())
		{
			Player p = player.getPlayerIfOnline();
			PacketUtils.sendActionBarMessage(p, msg);
		}
	}
}
