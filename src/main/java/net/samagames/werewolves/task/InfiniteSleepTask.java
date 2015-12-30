package net.samagames.werewolves.task;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.GameState;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
        if (plugin.getGame().getGameState() != GameState.NIGHT)
            return ;
        for (WWPlayer wwp : plugin.getGame().getInGamePlayers().values())
        {
            if (wwp.isOnline() && !wwp.isModerator() && !wwp.isSpectator())
            {
                Player p = wwp.getPlayerIfOnline();
                if (p.isSleeping() && p instanceof CraftPlayer)
                    ((CraftPlayer)p).getHandle().sleepTicks = 0;
            }
        }
    }

}
