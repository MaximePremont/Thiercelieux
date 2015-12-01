package net.samagames.werewolves.game;

import net.samagames.werewolves.WWPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameCommand implements CommandExecutor
{
	private WWPlugin plugin;
	
	public GameCommand(WWPlugin pl)
	{
		plugin = pl;
	}
	
	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3)
	{
		if (arg3.length == 1 && arg3[0].equalsIgnoreCase("nextevent") && arg0.hasPermission("thiercelieux.nextevent"))
			switch (plugin.getGame().getGameState())
			{
			case DAY_1:
			case DAY_2:
				plugin.getGame().nextDayEvent();
				break ;
			case NIGHT:
				plugin.getGame().nextNightEvent();
			default:
			}
		return true;
	}

}
