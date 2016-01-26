package net.samagames.tssamabot.actions;

import net.samagames.tssamabot.TSSamaBot;

public class KeepAlive extends BotAction {

	@Override
	public String run(TSSamaBot bot, CommandSender sender, String[] args)
	{
		return OK;
	}

}
