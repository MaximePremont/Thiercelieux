package net.samagames.werewolves.task;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.util.GameState;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
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
public class InfiniteSleepTask implements Runnable
{
    private WWPlugin plugin;

    public InfiniteSleepTask(WWPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void run()
    {
        if (this.plugin.getGame().getGameState() != GameState.NIGHT)
            return ;
        this.plugin.getGame().getInGamePlayers().values().stream().filter(wwp -> wwp.isOnline() && !wwp.isModerator() && !wwp.isSpectator()).forEach(wwp ->
        {
            Player p = wwp.getPlayerIfOnline();
            if (p.isSleeping() && p instanceof CraftPlayer)
                ((CraftPlayer) p).getHandle().sleepTicks = 0;
        });
    }

}
