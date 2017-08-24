package net.samagames.werewolves.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.entities.SkinDisguise;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.ItemsUtil;
import net.samagames.werewolves.util.WinType;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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
public class Witch extends WWClass
{
    private static final String POTION_LIFE = "Potion de vie";
    private static final String POTION_DEATH = "Potion de mort";
    
    private Location house;
    private Location stand;
    private ArmorStand[] armorStands;
    private Map<UUID, Boolean[]> potions;

    protected Witch()
    {
        super("witch", "La", "&5Sorcière", new ItemStack(Material.POTION), new String[]{"Vous avez deux potions, une pour rendre la vie", "et une pour la prendre.", "Utilisez les à bon escient !"}, new SkinDisguise("Witch_Aphmau"));
        this.house = null;
        this.stand = null;
        this.armorStands = null;
        this.potions = new HashMap<>();
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
    public void handleNightTurnStart(WWPlugin plugin, Set<WWPlayer> players)
    {
        for (WWPlayer wwp : players)
        {
            Player p = wwp.getPlayerIfOnline();
            p.setGameMode(GameMode.SURVIVAL);
            p.teleport(this.house);
            if (!this.potions.containsKey(wwp.getUUID()))
                this.potions.put(wwp.getUUID(), new Boolean[]{true, true});
        }
        displayText("Cliquez sur l'alambic", "pour utiliser", "vos potions !");
    }

    private void displayText(String... text)
    {
        if (this.armorStands != null)
            return ;
        this.armorStands = new ArmorStand[text.length];
        Location loc = stand.clone();
        for (int i = text.length - 1; i >= 0; i--)
        {
            this.armorStands[i] = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            this.armorStands[i].setCustomNameVisible(true);
            this.armorStands[i].setCustomName(text[i]);
            this.armorStands[i].setVisible(false);
            this.armorStands[i].setGravity(false);
            this.armorStands[i].setSmall(true);
            loc = loc.add(0, 0.23, 0);
        }
    }

    private void removeText()
    {
        if (this.armorStands == null)
            return ;
        for (ArmorStand as : this.armorStands)
            as.remove();
        this.armorStands = null;
    }

    @Override
    public void handleNightTurnEnd(WWPlugin plugin, Set<WWPlayer> players)
    {
        removeText();
        for (WWPlayer wwp : players)
        {
            Player p = wwp.getPlayerIfOnline();
            p.setGameMode(GameMode.ADVENTURE);
            p.closeInventory();
        }
    }

    @Override
    public void handlePlayerBlockClick(WWPlugin plugin, WWPlayer player, Block block)
    {
        if (block.getLocation().distanceSquared(this.stand) > 1 || block.getType() != Material.BREWING_STAND)
            return ;
        openStand(plugin, player);
    }

    private void openStand(WWPlugin plugin, WWPlayer player)
    {
        Boolean[] pot = this.potions.get(player.getUUID());
        if (pot == null)
            return ;
        Inventory inv = plugin.getServer().createInventory(null, InventoryType.BREWING, ChatColor.DARK_PURPLE + "Alambic de Sorcière");
        Set<WWPlayer> dead = plugin.getGame().getDeadPlayers();
        ItemStack potion1 = ItemsUtil.setItemMeta(Material.POTION, 1, (short)8193, ChatColor.LIGHT_PURPLE + POTION_LIFE, dead.isEmpty() ? new String[]{ChatColor.RED + "Aucun mort à sauver"} : null);
        ItemStack potion2 = ItemsUtil.setItemMeta(Material.POTION, 1, (short)8268, ChatColor.DARK_PURPLE + POTION_DEATH, null);
        ItemStack emptyPotion1 = ItemsUtil.setItemMeta(Material.GLASS_BOTTLE, 1, (short)0, ChatColor.LIGHT_PURPLE + POTION_LIFE, new String[]{ChatColor.RED + "Potion déjà utilisée"});
        ItemStack emptyPotion2 = ItemsUtil.setItemMeta(Material.GLASS_BOTTLE, 1, (short)0, ChatColor.LIGHT_PURPLE + POTION_DEATH, new String[]{ChatColor.RED + "Potion déjà utilisée"});
        ItemStack quit = ItemsUtil.setItemMeta(Material.BARRIER, 1, (short)0, "Passer votre tour", null);
        inv.setItem(0, pot[0] ? potion1 : emptyPotion1);
        inv.setItem(2, pot[1] ? potion2 : emptyPotion2);
        inv.setItem(1, quit);
        player.getPlayerIfOnline().openInventory(inv);
    }

    @Override
    public boolean overrideInventoryClick(WWPlugin plugin, WWPlayer source, Inventory i, ItemStack item)
    {
        if (item == null)
            return false;
        if (i.getName().equals(ChatColor.DARK_PURPLE + "Alambic de Sorcière"))
            return this.onStandInventoryClick(plugin, source, item);
        if (i.getName().equals(ChatColor.LIGHT_PURPLE + POTION_LIFE))
            return this.onLifePotionInventoryClick(plugin, item);
        if (i.getName().equals(ChatColor.DARK_PURPLE + POTION_DEATH))
            return this.onDeathPotionInventoryClick(plugin, item);
        return false;
    }
    
    private boolean onDeathPotionInventoryClick(WWPlugin plugin, ItemStack item)
    {
        if (item.getType() != Material.SKULL_ITEM || item.getDurability() != 3)
            return true;
        String owner = ((SkullMeta)item.getItemMeta()).getOwner();
        if (owner == null)
            return true;
        Player p = plugin.getServer().getPlayerExact(owner);
        if (p == null)
            return true;
        WWPlayer wwp = plugin.getGame().getPlayer(p.getUniqueId());
        if (wwp == null)
            return true;
        if (plugin.getGame().getDeadPlayers().contains(wwp))
            return true;
        Boolean[] pot = this.potions.get(wwp.getUUID());
        if (pot == null || !pot[1])
            return true;
        pot[1] = false;
        plugin.getGame().diePlayer(wwp, this);
        openStand(plugin, wwp);
        return true;
    }
    
    private boolean onLifePotionInventoryClick(WWPlugin plugin, ItemStack item)
    {
        if (item.getType() != Material.SKULL_ITEM || item.getDurability() != 3)
            return true;
        String owner = ((SkullMeta)item.getItemMeta()).getOwner();
        if (owner == null)
            return true;
        Player p = plugin.getServer().getPlayerExact(owner);
        if (p == null)
            return true;
        WWPlayer wwp = plugin.getGame().getPlayer(p.getUniqueId());
        if (wwp == null)
            return true;
        if (!plugin.getGame().getDeadPlayers().contains(wwp))
            return true;
        Boolean[] pot = this.potions.get(wwp.getUUID());
        if (pot == null || !pot[0])
            return true;
        pot[0] = false;
        plugin.getGame().getDeadPlayers().remove(wwp);
        openStand(plugin, wwp);
        return true;
    }
    
    private boolean onStandInventoryClick(WWPlugin plugin, WWPlayer source, ItemStack item)
    {
        if (item.getType() == Material.BARRIER)
        {
            source.getPlayerIfOnline().closeInventory();
            plugin.getGame().nextNightEvent();
            return true;
        }
        if (item.getType() != Material.POTION)
            return false;
        Set<WWPlayer> dead = plugin.getGame().getDeadPlayers();
        if (item.getDurability() == 8193 && !dead.isEmpty())
        {
            Inventory inv = plugin.getServer().createInventory(null, 27, ChatColor.LIGHT_PURPLE + POTION_LIFE);
            for (WWPlayer player : dead)
                inv.addItem(ItemsUtil.createHead(player.getDisplayName()));
            source.getPlayerIfOnline().openInventory(inv);
            return true;
        }
        else if (item.getDurability() == 8268)
        {
            Inventory inv = plugin.getServer().createInventory(null, 27, ChatColor.DARK_PURPLE + POTION_DEATH);
            for (WWPlayer wwp : plugin.getGame().getInGamePlayers().values())
            {
                if (!wwp.isOnline() || wwp.isSpectator() || wwp.isModerator() || dead.contains(wwp))
                    continue ;
                inv.addItem(ItemsUtil.createHead(wwp.getDisplayName()));
            }
            source.getPlayerIfOnline().openInventory(inv);
            return true;
        }
        return true;
    }

    public void setHouseLocation(Location h, Location s)
    {
        this.house = h;
        this.stand = s;
    }

}
