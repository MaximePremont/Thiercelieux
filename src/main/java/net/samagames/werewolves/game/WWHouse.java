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
public class WWHouse
{
    private Location display;
    private Location bed;
    private ArmorStand armorstand;

    public WWHouse(Location display, Location bed)
    {
        this.display = display;
        this.bed = bed;
        this.armorstand = null;
    }

    public void safeReset()
    {
        this.display.getWorld().loadChunk(this.display.getBlockX() / 16, this.display.getBlockZ() / 16);
        this.display.getWorld().getEntities().stream().filter(e -> e instanceof ArmorStand).forEach(Entity::remove);
    }

    public void displayName(String name)
    {
        if (this.armorstand != null)
            return ;
        this.armorstand = (ArmorStand)this.display.getWorld().spawnEntity(this.display, EntityType.ARMOR_STAND);
        this.armorstand.setCustomNameVisible(true);
        this.armorstand.setCustomName(ChatColor.GOLD + "Maison de " + name);
        this.armorstand.setVisible(false);
        this.armorstand.setGravity(false);
    }

    public void teleportToBed(Player player)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        player.setSleepingIgnored(true);
        player.teleport(this.bed);
        player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(Integer.MAX_VALUE, 10));
        try {
            EnumBedResult result = ((CraftPlayer)player).getHandle().a(new BlockPosition(this.bed.getBlockX(), this.bed.getBlockY(), this.bed.getBlockZ()));
            if (result != EnumBedResult.OK)
                Bukkit.getLogger().severe("[WereWolves] Error : can't set player " + player.getName() + " in bed (" + this.bed.getBlockX() + ", " + this.bed.getBlockY() + ", " + this.bed.getBlockZ() + ", result=" + result + ").");
        } catch (IllegalArgumentException ex) {
            Bukkit.getLogger().severe("[WereWolves] Invalid bed location ! (" + this.bed.getBlockX() + ", " + this.bed.getBlockY() + ", " + this.bed.getBlockZ() + ").");
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
        if (this.armorstand == null)
            return ;
        this.armorstand.setCustomName(ChatColor.RED + "✞ Maison de " + name + ChatColor.RED + " ✞");
    }
}
