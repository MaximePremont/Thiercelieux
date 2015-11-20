package net.samagames.werewolves.util;

import java.util.UUID;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.samagames.tools.Reflection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class PacketUtils
{
	private PacketUtils(){}
	
	public static void broadcastDestroyPacket(int id)
	{
		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(id);
		for (Player p : Bukkit.getOnlinePlayers())
			if (p instanceof CraftPlayer)
				((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
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
			e.printStackTrace();
		}
		for (Player p : Bukkit.getOnlinePlayers())
			if (p instanceof CraftPlayer)
				((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	}
	
	private static DataWatcher getDataWatcher(String msg)
	{
		DataWatcher watcher = new DataWatcher(null);
		if (msg != null)
			watcher.a(2, msg);
		return watcher;
	}

	public static void broadcastSpawnPlayerPacket(Player player, UUID uuid)
	{
		if (!(player instanceof CraftPlayer))
			return ;
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(((CraftPlayer)player).getHandle());
		try {
			Reflection.setValue(packet, "b", uuid);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		for (Player p : Bukkit.getOnlinePlayers())
			if (p instanceof CraftPlayer && !p.equals(player))
				((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	}
	
	public static void sendActionBarMessage(Player player, String msg)
	{
		if (!(player instanceof CraftPlayer))
			return ;
		String json = "{text:\"" + msg + "\"}";
		PacketPlayOutChat packet = new PacketPlayOutChat(ChatSerializer.a(json), (byte)2);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
	}
}
