package net.samagames.tssamabot.utils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

public class BotLogger
{
	private static final boolean DEBUG = true;
	
	public static void log(TS3Api api, String msg)
	{
		System.out.println(msg);
		if (!DEBUG || api == null)
			return ;
		ClientInfo rigner = api.getClientByUId("Ygmj9jsCuS8KxoC65vrkiNVZkKQ=");
		if (rigner != null)
			api.sendPrivateMessage(rigner.getId(), msg);
	}
}
