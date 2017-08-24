package net.samagames.werewolves.util;

import net.minecraft.server.v1_9_R2.EnumParticle;
import net.minecraft.server.v1_9_R2.PacketPlayOutWorldParticles;

import org.bukkit.Location;
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
public class ParticlesUtil
{
    private ParticlesUtil(){}

    public static void sendParticle(Player player, EnumParticle particle, float data, Location loc)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                particle,
                true,
                (float)loc.getX(),
                (float)loc.getY(),
                (float)loc.getZ(),
                (float)0,
                (float)0,
                (float)0,
                data,
                1);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
}
