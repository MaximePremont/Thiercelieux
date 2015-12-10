package net.samagames.werewolves.game;

import net.samagames.api.games.GamePlayer;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.util.PacketUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class WWPlayer extends GamePlayer
{
	private WWClass clazz;
	private WWHouse house;
	private boolean secondturn;
	private WWPlayer couple;
	private boolean isProtected;
	
	public WWPlayer(Player player)
	{
		super(player);
		clazz = null;
		house = null;
		secondturn = false;
		couple = null;
		isProtected = false;
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
	
	public boolean isInSecondTurn()
	{
		return secondturn;
	}
	
	public void setSecondTurn(boolean s)
	{
		secondturn = s;
	}
	
	public void setCouple(WWPlayer other)
	{
		couple = other;
	}
	
	public WWPlayer getCouple()
	{
		return couple;
	}
	
	public boolean isInCouple()
	{
		return (couple != null);
	}
	
	@Override
	public void setSpectator()
	{
		this.spectator = true;
		Player p = this.getPlayerIfOnline();
		if (p == null)
			return ;
		p.removePotionEffect(PotionEffectType.BLINDNESS);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.setFlying(true);
		for (Player player : Bukkit.getOnlinePlayers())
            player.hidePlayer(p);
		if (house != null)
			house.setDeadName(p.getName());
		if (!moderator)
			PacketUtils.sendWorldBorder(p);
	}
	
	
	public boolean isProtected()
	{
		return isProtected;
	}
	
	public void setProtected(boolean p)
	{
		isProtected = p;
	}
}
