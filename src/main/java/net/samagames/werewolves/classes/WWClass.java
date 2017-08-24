package net.samagames.werewolves.classes;

import java.util.Arrays;
import java.util.Set;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.entities.WWDisguise;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.ItemsUtil;
import net.samagames.werewolves.util.WinType;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
public abstract class WWClass
{
    public static final WWClass SIMPLE_VILLAGER = new SimpleVillager(); //Villageois
    public static final WWClass SEER = new Seer();                      //Voyante
    public static final WWClass WEREWOLF = new WereWolf();              //Loup Garou
    public static final WWClass LITTLE_GIRL = new LittleGirl();         //Petite Fille
    public static final WWClass WITCH = new Witch();                    //Sorcière
    public static final WWClass CUPIDON = new Cupidon();                //Cupidon
    public static final WWClass SALVATOR = new Salvator();              //Salvateur
    public static final WWClass ELDER = new Elder();                    //Ancien
    public static final WWClass ANGEL = new Angel();                    //Ange

    private static final WWClass[] VALUES = new WWClass[]{SEER, WEREWOLF, SIMPLE_VILLAGER, LITTLE_GIRL, WITCH, CUPIDON, SALVATOR, ELDER, ANGEL};
    private static final WWClass[] NIGHT_ORDER = new WWClass[]{CUPIDON, SALVATOR, SEER, WEREWOLF, WITCH, ANGEL};

    private String prefix;
    private String id;
    private String name;
    private ItemStack item;
    private String[] description;
    private WWDisguise disguise;
    private boolean disabled;

    protected WWClass(String id, String prefix, String name, ItemStack item, String[] description, WWDisguise disguise)
    {
        this.prefix = prefix;
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.description = description;
        this.item = ItemsUtil.setItemMeta(item, name, description);
        this.disguise = disguise;
        this.id = id;
        this.disabled = false;
    }

    public static WWClass[] getValues()
    {
        return Arrays.copyOf(VALUES, VALUES.length);
    }

    public static WWClass[] getNightOrder()
    {
        return Arrays.copyOf(NIGHT_ORDER, NIGHT_ORDER.length);
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public String getLowercasePrefix()
    {
        return this.prefix.toLowerCase();
    }

    public String getName()
    {
        return this.name;
    }

    public String[] getDescription()
    {
        return this.description;
    }

    public boolean isSimilar(ItemStack item)
    {
        if (item == null || item.getType() != this.item.getType())
            return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getDisplayName().equals(this.name);
    }

    public WWDisguise getDisguise()
    {
        return this.disguise;
    }

    public String getID()
    {
        return this.id;
    }

    public ItemStack getItem()
    {
        return this.item;
    }

    public void setDisabled(boolean d)
    {
        this.disabled = d;
    }

    public boolean isDisabled()
    {
        return this.disabled;
    }

    /* Let's go for personnalisation methods */

    public abstract boolean canPlayAtNight();
    public abstract WinType getWinType();
    public void handleNightTurnStart(WWPlugin plugin, Set<WWPlayer> players){}
    public void handleNightTurnEnd(WWPlugin plugin, Set<WWPlayer> oldplayers) {}
    public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target){}
    public void handlePlayerBlockClick(WWPlugin plugin, WWPlayer player, Block block){}

    public boolean overrideInventoryClick(WWPlugin plugin, WWPlayer source, Inventory i, ItemStack current)
    {
        // Sonar doesn't like unused variables, even if i need it for POO
        plugin.getClass();
        source.getClass();
        i.getClass();
        current.getClass();
        return false;
    }

    public boolean canBeKilled(WWPlayer player, WWClass by)
    {
        // Sonar doesn't like unused variables, even if i need it for POO
        player.getClass();
        by.getClass();
        return true;
    }

    public String getTextAtNight()
    {
        return null;
    }

    public boolean handleDeath(WWPlugin plugin, WWPlayer player, WWClass by)
    {
        // Sonar doesn't like unused variables, even if i need it for POO
        plugin.getClass();
        player.getClass();
        by.getClass();
        return true;
    }

    public boolean hasSelector()
    {
        return true;
    }

    public int getMaximumDelay()
    {
        return 60;
    }
}
