package net.samagames.werewolves.game;

import java.util.Set;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.classes.WereWolf;
import net.samagames.werewolves.util.GameState;

import org.bukkit.ChatColor;

public class TextGame extends WWGame
{
	public TextGame(WWPlugin plugin)
	{
		super(plugin);
	}
	
	@Override
	public void handleChatMessage(WWPlayer player, String message)
	{
		if (player.isModerator())
		{
			broadcastMessage(ChatColor.GRAY + player.getPlayerIfOnline().getDisplayName() + ChatColor.WHITE + ": " + message);
			return ;
		}
		if (player.isSpectator())
		{
			String msg = ChatColor.GRAY + "[SPEC] " + player.getPlayerIfOnline().getDisplayName() + ChatColor.WHITE + ": " + message;
			for (WWPlayer wwp : this.getSpectatorPlayers().values())
				if (wwp.isOnline())
					wwp.getPlayerIfOnline().sendMessage(msg);
			return ;
		}
		if (getGameState() != GameState.NIGHT)
		{
			broadcastMessage(ChatColor.GRAY + player.getPlayerIfOnline().getDisplayName() + ChatColor.WHITE + ": " + message);
			return ;
		}
		if (WWClass.NIGHT_ORDER[currentevent] == WWClass.WEREWOLF && player.getPlayedClass() instanceof WereWolf)
		{
			Set<WWPlayer> receivers = this.getPlayersByClass(WWClass.WEREWOLF);
			String msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + player.getPlayerIfOnline().getDisplayName() + ChatColor.WHITE + ": " + message;
			for (WWPlayer wwp : receivers)
				wwp.getPlayerIfOnline().sendMessage(msg);
			receivers = this.getPlayersByClass(WWClass.LITTLE_GIRL);
			msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + "Loup" + ChatColor.WHITE + ": " + message;
			for (WWPlayer wwp : receivers)
				wwp.getPlayerIfOnline().sendMessage(msg);
			plugin.getServer().getConsoleSender().sendMessage(msg);
		}
			
	}
}
