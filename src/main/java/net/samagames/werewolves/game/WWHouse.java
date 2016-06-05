package net.samagames.werewolves.game;

import java.util.logging.Level;

import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.EntityHuman.EnumBedResult;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class WWHouse
{
    private Location display;
    private Location bed;
    private ArmorStand armorstand;

    public WWHouse(Location display, Location bed)
    {
        this.display = display;
        this.bed = bed;
        armorstand = null;
    }

    public void safeReset()
    {
        display.getWorld().loadChunk(display.getBlockX() / 16, display.getBlockZ() / 16);
        for (Entity e : display.getWorld().getEntities())
            if (e instanceof ArmorStand)
                e.remove();
    }

    public void displayName(String name)
    {
        if (armorstand != null)
            return ;
        armorstand = (ArmorStand)display.getWorld().spawnEntity(display, EntityType.ARMOR_STAND);
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
            EnumBedResult result = ((CraftPlayer)player).getHandle().a(new BlockPosition(bed.getBlockX(), bed.getBlockY(), bed.getBlockZ()));
            if (result != EnumBedResult.OK)
                Bukkit.getLogger().severe("[WereWolves] Error : can't set player " + player.getName() + " in bed (" + bed.getBlockX() + ", " + bed.getBlockY() + ", " + bed.getBlockZ() + ", result=" + result + ").");
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().severe("[WereWolves] Invalid bed location ! (" + bed.getBlockX() + ", " + bed.getBlockY() + ", " + bed.getBlockZ() + ").");
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void removeFromBed(Player player)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        ((CraftPlayer)player).getHandle().a(true, false, false);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
    }

    public void setDeadName(String name)
    {
        if (armorstand == null)
            return ;
        armorstand.setCustomName(ChatColor.RED + "✞ Maison de " + name + ChatColor.RED + " ✞");
    }
}
