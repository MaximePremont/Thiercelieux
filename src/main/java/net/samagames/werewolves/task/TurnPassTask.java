package net.samagames.werewolves.task;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.PacketUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/*
 * This file is part of Thiercelieux.
 *
 * Thiercelieux is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Thiercelieux is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thiercelieux.  If not, see <http://www.gnu.org/licenses/>.
 */
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
