package net.samagames.werewolves.entities;

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
public class SkinDisguise extends WWDisguise
{
    private String uuid;
    
    public SkinDisguise(String uuid)
    {
        super(null);
        this.uuid = uuid;
    }

    @Override
    public void disguisePlayer(Player player)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        this.uuid.getClass();
        //TODO
    }

    //WereWolf username = SM_Werewolf
}
