package net.samagames.tssamabot.actions;

import net.samagames.tssamabot.TSSamaBot;
import net.samagames.tssamabot.WolfChannel;
import net.samagames.tssamabot.utils.BotLogger;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class MutePlayer extends BotAction
{
	@Override
	public String run(TSSamaBot bot, CommandSender sender, String[] args)
	{
		StringBuilder result = new StringBuilder("OK");
		for (String str : args)
		{
			Client client = bot.getApi().getClientByNameExact(str, true);
			if (client == null)
				continue ;
			WolfChannel channel = bot.getChannel(client.getChannelId());
			if (channel == null)
				continue ;
			if (bot.getApi().addChannelClientPermission(channel.getID(), client.getDatabaseId(), "i_client_talk_power", 0))
				result.append(":" + str.toLowerCase());
		}
		String str = result.toString();
		if (str.equals("OK"))
			return "NOK";
		BotLogger.log(bot.getApi(), "Muted players " + str.substring(3, str.length()).replace(":", ", "));
		return str;
	}
}
