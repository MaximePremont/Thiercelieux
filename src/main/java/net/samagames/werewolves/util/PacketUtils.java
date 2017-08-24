package net.samagames.werewolves.util;

import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.server.v1_9_R2.*;
import net.samagames.tools.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
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
@SuppressWarnings("deprecation")
public class PacketUtils
{
    private PacketUtils(){}

    public static void broadcastDestroyPacket(int id)
    {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(id);
        Bukkit.getOnlinePlayers().stream().filter(p -> p instanceof CraftPlayer).forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
    }

    public static void broadcastSpawnEntityPacket(int id, EntityType type, Location loc, String name)
    {
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
        try {
            Reflection.setValue(packet, "a", id);
            Reflection.setValue(packet, "b", type.getTypeId());
            Reflection.setValue(packet, "c", (int)(loc.getX() * 32D));
            Reflection.setValue(packet, "d", (int)(loc.getY() * 32D));
            Reflection.setValue(packet, "e", (int)(loc.getZ() * 32D));
            Reflection.setValue(packet, "f", 0);
            Reflection.setValue(packet, "g", 0);
            Reflection.setValue(packet, "h", 0);
            Reflection.setValue(packet, "i", (byte)MathHelper.d(loc.getPitch() * 256.0F / 360.0F));
            Reflection.setValue(packet, "j", (byte)MathHelper.d(loc.getYaw() * 256.0F / 360.0F));
            Reflection.setValue(packet, "k", (byte)0);
            Reflection.setValue(packet, "l", getDataWatcher(name));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        Bukkit.getOnlinePlayers().stream().filter(p -> p instanceof CraftPlayer).forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
    }

    @SuppressWarnings("unchecked")
    private static DataWatcher getDataWatcher(String msg)
    {
        DataWatcher watcher = new DataWatcher(null);
        if (msg != null)
            try
            {
                watcher.set((DataWatcherObject<String>) Reflection.getValue(null, Entity.class, true, "aA"), msg);
            } catch (Exception ignored) {}
        return watcher;
    }

    public static void broadcastSpawnPlayerPacket(Player player, UUID uuid)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
        if (player.getUniqueId().equals(uuid))
            Bukkit.getOnlinePlayers().stream().filter(p -> p instanceof CraftPlayer && !p.equals(player)).forEach(p -> ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet));
    }

    public static void sendActionBarMessage(Player player, String msg)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        String json = "{text:\"" + msg + "\"}";
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(json), (byte)2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendWorldBorder(Player player)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        WorldBorder wb = new WorldBorder();
        wb.setCenter(1000000D, 1000000D);
        wb.setSize(1D);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
    }
}
