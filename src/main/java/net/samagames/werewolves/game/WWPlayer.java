package net.samagames.werewolves.game;

import net.samagames.api.games.GamePlayer;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.util.PacketUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class WWPlayer extends GamePlayer
{
    private WWClass clazz;
    private WWHouse house;
    private boolean secondTurn;
    private WWPlayer couple;
    private boolean isProtected;

    public WWPlayer(Player player)
    {
        super(player);
        this.clazz = null;
        this.house = null;
        this.secondTurn = false;
        this.couple = null;
        this.isProtected = false;
    }

    public WWClass getPlayedClass()
    {
        return this.clazz;
    }

    public void setPlayedClass(WWClass newClass)
    {
        this.clazz = newClass;
    }

    public void setHouse(WWHouse wwHouse)
    {
        this.house = wwHouse;
    }

    public WWHouse getHouse()
    {
        return this.house;
    }

    public boolean isInSecondTurn()
    {
        return this.secondTurn;
    }

    public void setSecondTurn(boolean s)
    {
        this.secondTurn = s;
    }

    public void setCouple(WWPlayer other)
    {
        this.couple = other;
    }

    public WWPlayer getCouple()
    {
        return this.couple;
    }

    public boolean isInCouple()
    {
        return this.couple != null;
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
        if (this.house != null)
            this.house.setDeadName(p.getName());
        if (!this.moderator)
            PacketUtils.sendWorldBorder(p);
    }


    public boolean isProtected()
    {
        return this.isProtected;
    }

    public void setProtected(boolean p)
    {
        this.isProtected = p;
    }

    public void win()
    {
        addCoins(10, "Victoire !");
        addStars(1, "Victoire !");
    }

    public String getDisplayName()
    {
        OfflinePlayer off = Bukkit.getOfflinePlayer(this.uuid);
        String name;
        if (off.isOnline())
            name = off.getPlayer().getDisplayName();
        else
            name = off.getName();
        return name;
    }
}
