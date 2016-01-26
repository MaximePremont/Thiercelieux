package net.samagames.tssamabot.actions;

import net.samagames.tssamabot.TSSamaBot;

public abstract class BotAction
{
	protected static final String OK = "OK";
	protected static final String NOK = "NOK";
	
	public abstract String run(TSSamaBot bot, CommandSender sender, String[] args);
}
