package net.samagames.tssamabot;

import java.util.HashMap;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;

public class WolfChannel
{
	private final HashMap<ChannelProperty, String> properties;
	private final String name;
	private int id;
	private String password;
	
	public WolfChannel(String name, String password)
	{
		properties = new HashMap<ChannelProperty, String>();
		properties.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
		if (password != null)
			properties.put(ChannelProperty.CHANNEL_PASSWORD, password);
		properties.put(ChannelProperty.CHANNEL_NEEDED_TALK_POWER, "100");
		this.name = name;
		this.password = password;
		id = -1;
	}
	
	public boolean create(TS3Api api)
	{
		if (id == -1)
			id = api.createChannel(name, properties);
		return (id != -1);
	}
	
	public boolean destroy(TS3Api api)
	{
		if (id != -1)
			return api.deleteChannel(id, true);
		return false;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setPassword(String pass)
	{
		password = pass;
	}
	
	public String getPassword()
	{
		return password;
	}
}
