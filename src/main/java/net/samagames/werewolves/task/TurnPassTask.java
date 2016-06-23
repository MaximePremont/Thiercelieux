package net.samagames.werewolves.task;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.PacketUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TurnPassTask implements Runnable
{
    private WWPlugin plugin;
    private WWClass clazz;
    private boolean night;
    private int time;

    public TurnPassTask(WWPlugin plugin, WWClass clazz, boolean night)
    {
        this.plugin = plugin;
        this.clazz = clazz;
        this.night = night;
        this.time = clazz.getMaximumDelay();
    }

    public TurnPassTask(WWPlugin plugin, int time, boolean night)
    {
        this.plugin = plugin;
        this.time = time;
        this.night = night;
        this.clazz = null;
    }

    @Override
    public void run()
    {
        this.time--;
        broadcastActionBarMessage(ChatColor.RED + "Temps restant : " + (this.time >= 600 ? "" : "0") + (this.time / 60) + ":" + (this.time % 60 < 10 ? "0" : "") + (this.time % 60));
        if (this.time <= 0 && (this.clazz == null || this.plugin.getGame().isCurrentlyPlayed(clazz)))
        {
            this.plugin.getGame().broadcastMessage(ChatColor.RED + "Temps écoulé !");
            broadcastActionBarMessage(ChatColor.RED + "Temps écoulé !");
            if (this.night)
                this.plugin.getGame().nextNightEvent();
            else
                this.plugin.getGame().nextDayEvent();
        }
    }

    private void broadcastActionBarMessage(String msg)
    {
        for (WWPlayer player : this.plugin.getGame().getRegisteredGamePlayers().values())
        {
            Player p = player.getPlayerIfOnline();
            if (p != null)
                PacketUtils.sendActionBarMessage(p, msg);
        }
    }
}
