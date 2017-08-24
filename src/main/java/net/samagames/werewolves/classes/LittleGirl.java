package net.samagames.werewolves.classes;

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
public class LittleGirl extends WWClass
{
    public LittleGirl()
    {
        super("littlegirl", "La", "&9Petite-Fille", new ItemStack(Material.RED_ROSE), new String[]{"Durant la nuit, espionnez les loups-garous"}, null);
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
    public String getTextAtNight()
    {
        return "Ecoutez les loups-garous durant la nuit";
    }
}
