package net.samagames.werewolves.util;

import net.samagames.tools.RulesBook;
import net.samagames.werewolves.WWPlugin;

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
public class RulesUtil
{
    private static ItemStack itemStack;

    private RulesUtil() {}

    public static ItemStack getRulesBook()
    {
        return RulesUtil.itemStack;
    }

    static
    {
        RulesUtil.itemStack = new RulesBook(WWPlugin.NAME_BICOLOR).addOwner("Rigner")
                .addPage("Comment jouer ?", "")
                .addPage("Objectifs", "")
                .addPage("Classes", "").toItemStack();
    }
}
