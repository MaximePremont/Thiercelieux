package net.samagames.werewolves.classes;

import java.util.HashMap;
import java.util.List;
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

public class Witch extends WWClass
{
	private Location house;
	private Location stand;
	private ArmorStand[] armorStands;
	private Map<UUID, Boolean[]> potions;
	
	protected Witch()
	{
		super("witch", "La", ChatColor.DARK_PURPLE + "Sorcière", new ItemStack(Material.POTION), new String[]{}, new SkinDisguise("Witch_Aphmau"));
		house = null;
		stand = null;
		armorStands = null;
		potions = new HashMap<UUID, Boolean[]>();
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
			p.teleport(house);
			if (!potions.containsKey(wwp.getUUID()))
				potions.put(wwp.getUUID(), new Boolean[]{true, true});
		}
		displayText("Cliquez sur l'alambic", "pour utiliser", "vos potions !");
	}
	
	private void displayText(String... text)
	{
		if (armorStands != null)
			return ;
		armorStands = new ArmorStand[text.length];
		Location loc = stand.clone();
		for (int i = text.length - 1; i >= 0; i--)
		{
			armorStands[i] = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			armorStands[i].setCustomNameVisible(true);
			armorStands[i].setCustomName(text[i]);
			armorStands[i].setVisible(false);
			armorStands[i].setGravity(false);
			armorStands[i].setSmall(true);
			loc = loc.add(0, 0.23, 0);
		}
	}
	
	private void removeText()
	{
		if (armorStands == null)
			return ;
		for (ArmorStand as : armorStands)
			as.remove();
		armorStands = null;
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
		if (block.getLocation().distanceSquared(stand) > 1 || block.getType() != Material.BREWING_STAND)
			return ;
		Boolean[] pot = potions.get(player.getUUID());
		if (pot == null)
			return ;
		Inventory inv = plugin.getServer().createInventory(null, InventoryType.BREWING, ChatColor.DARK_PURPLE + "Alambic de Sorcière");
		List<WWPlayer> deads = plugin.getGame().getDeadPlayers();
		ItemStack potion1 = ItemsUtil.setItemMeta(Material.POTION, 1, (short)8193, ChatColor.LIGHT_PURPLE + "Potion de vie", deads.size() == 0 ? new String[]{ChatColor.RED + "Aucun mort à sauver"} : null);
		ItemStack potion2 = ItemsUtil.setItemMeta(Material.POTION, 1, (short)8268, ChatColor.DARK_PURPLE + "Potion de mort", null);
		ItemStack emptypotion1 = ItemsUtil.setItemMeta(Material.GLASS_BOTTLE, 1, (short)0, ChatColor.LIGHT_PURPLE + "Potion de vie", new String[]{ChatColor.RED + "Potion déjà utilisée"});
		ItemStack emptypotion2 = ItemsUtil.setItemMeta(Material.GLASS_BOTTLE, 1, (short)0, ChatColor.LIGHT_PURPLE + "Potion de mort", new String[]{ChatColor.RED + "Potion déjà utilisée"});
		ItemStack quit = ItemsUtil.setItemMeta(Material.BARRIER, 1, (short)0, "Passer votre tour", null);
		inv.setItem(0, pot[0] ? potion1 : emptypotion1);
		inv.setItem(2, pot[1] ? potion2 : emptypotion2);
		inv.setItem(1, quit);
		player.getPlayerIfOnline().openInventory(inv);
	}

	@Override
	public boolean overrideInventoryClick(WWPlugin plugin, WWPlayer source, Inventory i, ItemStack item)
	{
		if (item == null)
			return false;
		if (i.getName().equals(ChatColor.DARK_PURPLE + "Alambic de Sorcière"))
		{
			if (item.getType() == Material.BARRIER)
			{
				source.getPlayerIfOnline().closeInventory();
				plugin.getGame().nextNightEvent();
				return true;
			}
			if (item.getType() != Material.POTION)
				return false;
			List<WWPlayer> deads = plugin.getGame().getDeadPlayers();
			if (item.getDurability() == 8193 && !deads.isEmpty())
			{
				Inventory inv = plugin.getServer().createInventory(null, 27, ChatColor.LIGHT_PURPLE + "Potion de vie");
				for (WWPlayer player : deads)
					inv.addItem(ItemsUtil.createHead(player.getOfflinePlayer().getName()));
				source.getPlayerIfOnline().openInventory(inv);
				return true;
			}
			else if (item.getDurability() == 8268)
			{
				Inventory inv = plugin.getServer().createInventory(null, 27, ChatColor.DARK_PURPLE + "Potion de mort");
				for (WWPlayer wwp : plugin.getGame().getInGamePlayers().values())
				{
					if (!wwp.isOnline() || wwp.isSpectator() || wwp.isModerator() || deads.contains(wwp))
						continue ;
					inv.addItem(ItemsUtil.createHead(wwp.getOfflinePlayer().getName()));
				}
				source.getPlayerIfOnline().openInventory(inv);
				return true;
			}
			return true;
		}
		if (i.getName().equals(ChatColor.LIGHT_PURPLE + "Potion de vie"))
		{
			return true;
		}
		return false;
	}
	
	public void setHouseLocation(Location h, Location s)
	{
		house = h;
		stand = s;
	}
	
}
