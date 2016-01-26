package net.samagames.tssamabot.actions;

import java.util.ArrayList;
import java.util.List;

import net.samagames.tssamabot.TSSamaBot;
import net.samagames.tssamabot.WolfChannel;
import net.samagames.tssamabot.utils.BotLogger;
import net.samagames.tssamabot.utils.StringUtils;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class CreateChannel extends BotAction
{
	@Override
	public String run(TSSamaBot bot, CommandSender sender, String[] args)
	{
		if (args.length < 2)
			return NOK;
		String name = args[0];
		StringBuilder sb = new StringBuilder("OK");
		List<Client> toMove = new ArrayList<Client>();
		for (int i = 1; i < args.length; i++)
		{
			Client ci = bot.getApi().getClientByNameExact(args[i], true);
			if (ci == null)
				continue ;
			toMove.add(ci);
		}
		String password = StringUtils.randomString();
		WolfChannel channel = new WolfChannel("Loup Garou #" + name, password);
		if (toMove.isEmpty() || !bot.createChannel(channel))
			return NOK;
		int id = channel.getID();
		for (Client ci : toMove)
		{
			if (bot.getApi().moveClient(ci.getId(), id)
					&& bot.getApi().addChannelClientPermission(id, ci.getDatabaseId(), "i_client_talk_power", 100))
			{
				sb.append(':');
				sb.append(ci.getNickname().toLowerCase());
			}
		}
		bot.getApi().sendChannelMessage(id, "Mot de passe du channel : " + password);
		String result = sb.toString();
		BotLogger.log(bot.getApi(), "Created channel " + name + " with players " + result.substring(3, result.length()).replace(":", ", "));
		return result;
	}
}
