package net.samagames.werewolves.game;

import java.util.Set;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;

import org.bukkit.ChatColor;

public class VocalGame extends WWGame
{
	public VocalGame(WWPlugin plugin)
	{
		super(plugin);
	}

	@Override
	public void handleChatMessage(WWPlayer player, String message)
	{
		if (player.isModerator())
		{
			broadcastMessage(player.getPlayerIfOnline().getDisplayName() + ChatColor.WHITE + ":" + message);
			return ;
		}
		if (WWClass.NIGHT_ORDER[currentevent] == WWClass.WEREWOLF && player.getPlayedClass() == WWClass.WEREWOLF)
		{
			Set<WWPlayer> receivers = this.getPlayersByClass(WWClass.WEREWOLF);
			String msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + player.getPlayerIfOnline().getDisplayName() + ChatColor.WHITE + ": " + message;
			for (WWPlayer wwp : receivers)
				wwp.getPlayerIfOnline().sendMessage(msg);
			msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + "Loup" + ChatColor.WHITE + ": " + message;
			receivers = this.getPlayersByClass(WWClass.LITTLE_GIRL);
			for (WWPlayer wwp : receivers)
				wwp.getPlayerIfOnline().sendMessage(msg);
			plugin.getServer().getConsoleSender().sendMessage(msg);
			return ;
		}
		player.getPlayerIfOnline().sendMessage(ChatColor.RED + "Le chat est désactivé en mode vocal. Merci de vous exprimer sur TeamSpeak.");
	}
	
	@Override
	public void startGame()
	{
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			//String[] players = SamaBOTConnector.createChannel(plugin.getApi().getServerName().split("_")[1], null);
			//TODO
			super.startGame();
		});
	}
}
