package net.samagames.werewolves.classes;

import java.util.Set;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.entities.WWDisguise;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.ItemsUtil;
import net.samagames.werewolves.util.WinType;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class WWClass
{
	public static final WWClass SIMPLE_VILLAGER = new SimpleVillager(); //Villageois
	public static final WWClass SEER = new Seer(); //Voyante
	public static final WWClass WEREWOLF = new WereWolf(); //Loup Garou
	public static final WWClass LITTLE_GIRL = new LittleGirl(); //Petite Fille
	public static final WWClass WITCH = new Witch(); //Sorcière
	
	public static final WWClass[] VALUES = new WWClass[]{SEER, WEREWOLF, SIMPLE_VILLAGER, LITTLE_GIRL, WITCH};
	public static final WWClass[] NIGHT_ORDER = new WWClass[]{SEER, WEREWOLF, WITCH};
	
	private String prefix;
	private String id;
	private String name;
	private ItemStack item;
	private String[] description;
	private WWDisguise disguise;
	
	protected WWClass(String id, String prefix, String name, ItemStack item, String[] description, WWDisguise disguise)
	{
		this.prefix = prefix;
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.description = description;
		this.item = ItemsUtil.setItemMeta(item, name, description);
		this.disguise = disguise;
		this.id = id;
	}
	
	public String getPrefix()
	{
		return prefix;
	}
	
	public String getLowercasePrefix()
	{
		return prefix.toLowerCase();
	}
	
	public String getName()
	{
		return name;
	}
	
	public String[] getDescription()
	{
		return description;
	}
	
	public boolean isSimilar(ItemStack item)
	{
		if (item == null || item.getType() != this.item.getType())
			return false;
		ItemMeta meta = item.getItemMeta();
		return (meta != null && meta.getDisplayName().equals(name));
	}
	
	public WWDisguise getDisguise()
	{
		return disguise;
	}
	
	public String getID()
	{
		return id;
	}

	public ItemStack getItem()
	{
		return item;
	}
	
	/* Let's go for personnalisation methods */
	
	public abstract boolean canPlayAtNight();
	public abstract WinType getWinType();
	public void handleNightTurnStart(WWPlugin plugin, Set<WWPlayer> players){}
	public void handleNightTurnEnd(WWPlugin plugin, Set<WWPlayer> oldplayers) {}
	public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target){}
	public void handlePlayerBlockClick(WWPlugin plugin, WWPlayer player, Block block){}
	
	public String getTextAtNight()
	{
		return null;
	}
	
	public void handleDeath(WWPlugin plugin, WWPlayer player)
	{
		
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
