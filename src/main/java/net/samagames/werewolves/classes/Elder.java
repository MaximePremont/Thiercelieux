package net.samagames.werewolves.classes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.WinType;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
public class Elder extends WWClass
{
    private Set<UUID> protectedPlayers;

    protected Elder()
    {
        super("elder", "L'", "&eAncien", new ItemStack(Material.LOG), new String[]{}, null);
        this.protectedPlayers = new HashSet<>();
    }

    @Override
    public boolean canPlayAtNight()
    {
        return false;
    }

    @Override
    public WinType getWinType()
    {
        return WinType.INNOCENTS;
    }

    @Override
    public void handleNightTurnStart(WWPlugin plugin, Set<WWPlayer> players)
    {
        players.stream().filter(wwp -> !this.protectedPlayers.contains(wwp.getUUID())).forEach(wwp -> this.protectedPlayers.add(wwp.getUUID()));
    }

    @Override
    public boolean canBeKilled(WWPlayer player, WWClass by)
    {
        if (!(by instanceof WereWolf))
            return true;
        if (!this.protectedPlayers.contains(player.getUUID()))
            return true;
        this.protectedPlayers.remove(player.getUUID());
        return false;
    }

    @Override
    public boolean handleDeath(WWPlugin plugin, WWPlayer player, WWClass by)
    {
        if (by == null || by.getWinType() == WinType.INNOCENTS)
        {
            for (WWPlayer wwp : plugin.getGame().getInGamePlayers().values())
            {
                if (!wwp.isOnline() || wwp.isModerator() || wwp.isSpectator())
                    continue ;
                if (wwp.getPlayedClass() != null && wwp.getPlayedClass().getWinType() == WinType.INNOCENTS)
                    wwp.getPlayedClass().setDisabled(true);
            }
            plugin.getGame().broadcastMessage(plugin.getGame().getCoherenceMachine().getGameTag() + " L'ancien " + player.getDisplayName() + " a été tué par des villageois, ceux-ci perdent donc tous leurs pouvoirs !");
        }
        return true;
    }
}
