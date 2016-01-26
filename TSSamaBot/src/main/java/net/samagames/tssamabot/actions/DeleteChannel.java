package net.samagames.tssamabot.actions;

import net.samagames.tssamabot.TSSamaBot;

public class DeleteChannel extends BotAction {

	@Override
	public String run(TSSamaBot bot, CommandSender sender, String[] args)
	{
		if (args.length < 1)
			return NOK;
		if (bot.removeChannel("Loup Garou #" + args[0]))
			return OK;
		return NOK;
	}

}
