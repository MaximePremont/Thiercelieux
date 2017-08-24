package net.samagames.werewolves.classes;

import net.samagames.tools.Titles;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.WinType;

import org.bukkit.ChatColor;
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
public class Seer extends WWClass
{
    private boolean locked;

    protected Seer()
    {
        super("seer", "La", "&5Voyante", new ItemStack(Material.EYE_OF_ENDER), new String[]{"Une fois par nuit, regardez le", "rôle d'un autre joueur"}, null);
        this.locked = false;
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
    public String getTextAtNight()
    {
        return "Choisissez le joueur donc vous voulez voir la carte";
    }

    @Override
    public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target)
    {
        if (this.locked || target.isSpectator() || target.isModerator() || !target.isOnline())
            return ;
        this.locked = true;
        Titles.sendTitle(source.getPlayerIfOnline(), 5, 50, 5, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Rôle de " + target.getDisplayName(), target.getPlayedClass().getName());
        plugin.getGame().cancelPassTask();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getGame().nextNightEvent(), 60);
    }
}
