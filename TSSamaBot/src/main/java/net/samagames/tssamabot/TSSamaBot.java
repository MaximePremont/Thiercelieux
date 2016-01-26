package net.samagames.tssamabot;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.samagames.tssamabot.utils.BotLogger;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TSSamaBot
{	
	public static void main(String[] args)
	{
		new TSSamaBot().run();
	}
	
	private TS3Api api;
	private PacketListener listener;
	private List<WolfChannel> channels;
	private List<WolfChannel> toDestroy;
	
	private void run()
	{
		final TS3Config config = new TS3Config();
		getConfig(config);
		config.setDebugLevel(Level.ALL);
		config.setFloodRate(FloodRate.UNLIMITED);
		
		final TS3Query query = new TS3Query(config);
		query.connect();
		
		api = query.getApi();
		api.selectVirtualServerById(1);
		api.setNickname("SamaWolfBot");
		api.addTS3Listeners(new BotListener(this));
		
		BotLogger.log(api, "SamaWolfBot connect� !");
		listener = new PacketListener(this);
		listener.start();
		
		channels = new ArrayList<WolfChannel>();
		toDestroy = new ArrayList<WolfChannel>();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run()
			{
				BotLogger.log(api, "Stopping SamaWolfBot");
				removeAllChannels();
				listener.stopListener();
				query.exit();
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		try {
			while (true)
			{
				Thread.sleep(100);
				channels.removeAll(toDestroy);
				toDestroy.clear();
			}
		} catch (InterruptedException e) {}
		System.exit(0);
	}
	
	private void getConfig(TS3Config config)
	{
		try {
			File file = new File("config.json");
			JsonObject object = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
			
			config.setHost(object.get("host").getAsString());//"rigner.ovh");
			config.setLoginCredentials(object.get("username").getAsString(), object.get("password").getAsString());//"tssamabot", "PLs1id1C");
		} catch (Throwable t) {
			t.printStackTrace();
			BotLogger.log(null, "Error : Bad config. Aborting.");
			System.exit(1);
		}
	}
	
	public TS3Api getApi()
	{
		return api;
	}
	
	public boolean createChannel(WolfChannel channel)
	{
		if (channel.create(api))
		{
			channels.add(channel);
			return true;
		}
		return false;
	}
	
	public boolean removeChannel(WolfChannel channel)
	{
		if (!channels.contains(channel))
			return false;
		if (channel.destroy(api))
		{
			toDestroy.add(channel);
			return true;
		}
		return false;
	}
	
	public boolean removeChannel(String name)
	{
		WolfChannel chan = null;
		for (WolfChannel tmp : channels)
			if (tmp.getName().equals(name))
				chan = tmp;
		if (chan == null)
			return false;
		return removeChannel(chan);
	}
	
	public void removeAllChannels()
	{
		for (WolfChannel channel : channels)
			removeChannel(channel);
		channels.removeAll(toDestroy);
		toDestroy.clear();
	}
	
	public WolfChannel getChannel(int channelid)
	{
		for (WolfChannel tmp : channels)
			if (tmp.getID() == channelid && !toDestroy.contains(tmp))
				return tmp;
		return null;
	}
}
