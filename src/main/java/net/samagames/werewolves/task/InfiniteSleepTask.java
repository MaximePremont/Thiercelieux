package net.samagames.werewolves.task;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.util.GameState;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
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
