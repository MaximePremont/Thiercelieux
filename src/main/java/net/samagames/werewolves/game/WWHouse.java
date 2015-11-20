package net.samagames.werewolves.game;

import net.minecraft.server.v1_8_R3.BlockPosition;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class WWHouse
{
	private Location display;
	private Location bed;
	
	public WWHouse(Location display, Location bed)
	{
		this.display = display;
		this.bed = bed;
	}
	
	public WWHouse safeReset()
	{
		display.getWorld().loadChunk(display.getBlockX() / 16, display.getBlockZ() / 16);
		for (Entity e : display.getWorld().getEntities())
			if (e instanceof ArmorStand)
				e.remove();
		return this;
	}
	
	public void displayName(String name)
	{
		ArmorStand armorstand = (ArmorStand)display.getWorld().spawnEntity(display, EntityType.ARMOR_STAND);
		armorstand.setCustomNameVisible(true);
		armorstand.setCustomName(ChatColor.GOLD + "Maison de " + name);
		armorstand.setVisible(false);
		armorstand.setGravity(false);
	}
	
	public void teleportToBed(Player player)
	{
		if (!(player instanceof CraftPlayer))
			return ;
		player.setSleepingIgnored(true);
		player.teleport(bed);
		player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(Integer.MAX_VALUE, 10));
		try {
			((CraftPlayer)player).getHandle().a(new BlockPosition(bed.getBlockX(), bed.getBlockY(), bed.getBlockZ()));
		} catch (IllegalArgumentException ex) {
			Bukkit.getLogger().severe("[WereWolves] Invalid bed location ! (" + bed.getBlockX() + ", " + bed.getBlockY() + ", " + bed.getBlockZ() + ").");
		}
	}
	
	public void removeFromBed(Player player)
	{
		if (!(player instanceof CraftPlayer))
			return ;
		((CraftPlayer)player).getHandle().a(true, false, false);
		player.removePotionEffect(PotionEffectType.BLINDNESS);
	}
}
