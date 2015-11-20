package net.samagames.werewolves.game;

import net.samagames.api.games.GamePlayer;
import net.samagames.werewolves.classes.WWClass;

import org.bukkit.entity.Player;

public class WWPlayer extends GamePlayer
{
	private WWClass clazz;
	private WWHouse house;
	
	public WWPlayer(Player player)
	{
		super(player);
		clazz = null;
		house = null;
	}
	
	public WWClass getPlayedClass()
	{
		return clazz;
	}
	
	public void setPlayedClass(WWClass newClass)
	{
		clazz = newClass;
	}

	public void setHouse(WWHouse wwHouse)
	{
		house = wwHouse;
	}
	
	public WWHouse getHouse()
	{
		return house;
	}
}
