package net.samagames.werewolves.classes;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.WinType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
public class Salvator extends WWClass {

    protected Salvator()
    {
        super("salvator", "Le", "Salvateur", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new String[]{"Une fois par nuit, protégez quelqu'un", "de l'attaque des loups-garous."}, null);
    }

    @Override
    public boolean canPlayAtNight()
    {
        return true;
    }

    @Override
    public WinType getWinType()
    {
        return WinType.INNOCENTS;
    }

    @Override
    public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target)
    {
        if (!target.isOnline() || target.isModerator() || target.isSpectator())
            return ;
        Player p1 = source.getPlayerIfOnline();
        if (p1 != null)
            p1.sendMessage(plugin.getGame().getCoherenceMachine().getGameTag() + ChatColor.WHITE + " Vous avez protégé : " + ChatColor.YELLOW + target.getDisplayName());
        target.setProtected(true);
        plugin.getGame().nextNightEvent();
    }
}
