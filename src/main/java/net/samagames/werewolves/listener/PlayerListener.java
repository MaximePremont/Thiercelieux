package net.samagames.werewolves.listener;

import java.util.Set;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.GameState;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.ItemsUtil;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerListener implements Listener
{
	private WWPlugin plugin;
	
	public PlayerListener(WWPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent ev)
	{
		if (ev.getItem() == null)
			return ;
		if (ev.getItem().getType() == Material.NETHER_STAR && plugin.getGame().getGameState() == GameState.WAITING)
			;//TODO: Class Selector
		if (ev.getItem().getType() == ItemsUtil.SELECTOR.getType() && plugin.getGame().getGameState() == GameState.NIGHT)
		{
			ev.setCancelled(true);
			WWPlayer wwp = plugin.getGame().getPlayer(ev.getPlayer().getUniqueId());
			if (wwp == null || wwp.isModerator() || wwp.isSpectator())
				return ;
			if (!plugin.getGame().isCurrentlyPlayed(wwp.getPlayedClass()))
			{
				ev.getPlayer().sendMessage(ChatColor.RED + "Ce n'est pas à vous de jouer.");
				return ;
			}
			Inventory i = plugin.getServer().createInventory(null, 27, "Sélecteur");
			Set<WWPlayer> excluded = plugin.getGame().getPlayersByClass(wwp.getPlayedClass());
			for (WWPlayer player : plugin.getGame().getInGamePlayers().values())
			{
				if (excluded.contains(player))
					continue ;
				if (player.isModerator() || player.isSpectator() || !player.isOnline())
					continue ;
				i.addItem(ItemsUtil.createHead(player.getOfflinePlayer().getName()));
			}
			ev.getPlayer().openInventory(i);
			return ;
		}
		if (plugin.getGame().getGameState() == GameState.NIGHT && (ev.getAction() == Action.LEFT_CLICK_BLOCK || ev.getAction() == Action.RIGHT_CLICK_BLOCK))
		{
			WWPlayer player = plugin.getGame().getPlayer(ev.getPlayer().getUniqueId());
			if (player.getPlayedClass() != null && plugin.getGame().isCurrentlyPlayed(player.getPlayedClass()))
				player.getPlayedClass().handlePlayerBlockClick(plugin, player, ev.getClickedBlock());
		}
	}
	
	@EventHandler (ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent ev)
	{
		ev.setCancelled(true);
		WWPlayer player = plugin.getGame().getPlayer(ev.getPlayer().getUniqueId());
		if (player != null)
			plugin.getGame().handleChatMessage(player, ev.getMessage());
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent ev)
	{
		ev.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent ev)
	{
		ev.setCancelled(true);
		if (ev.getEntity() == null || !(ev.getEntity() instanceof Player) || ev.getDamager() == null || !(ev.getDamager() instanceof Player) || plugin.getGame().getGameState() != GameState.NIGHT)
			return ;
		WWPlayer source = plugin.getGame().getPlayer(ev.getDamager().getUniqueId());
		WWPlayer target = plugin.getGame().getPlayer(ev.getEntity().getUniqueId());
		if (source.getPlayedClass() != null && plugin.getGame().isCurrentlyPlayed(source.getPlayedClass()))
			source.getPlayedClass().handlePlayerClick(plugin, source, target);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent ev)
	{
		if (ev.getEntity() == null || !(ev.getEntity() instanceof Player) || ev.getCause() != DamageCause.ENTITY_ATTACK)
		{
			if (ev.getCause() == DamageCause.FIRE)
				plugin.getServer().getScheduler().runTaskLater(plugin, () -> ev.getEntity().setFireTicks(0), 1);
			ev.setCancelled(true);
			return ;
		}
	}
	
	@EventHandler
	public void onEntityInventoryClick(InventoryClickEvent ev)
	{
		ev.setCancelled(true);
		Player p;
		if (!(ev.getWhoClicked() instanceof Player) || (p = (Player)ev.getWhoClicked()) == null || ev.getCurrentItem() == null || ev.getCurrentItem().getType() == Material.AIR)
			return ;
		WWPlayer wwp = plugin.getGame().getPlayer(p.getUniqueId());
		if (wwp == null || wwp.isModerator() || wwp.isSpectator())
			return ;
		if (ev.getCurrentItem().getType() == Material.SKULL_ITEM && ev.getCurrentItem().getDurability() == 3)
		{
			String name = ((SkullMeta)ev.getCurrentItem().getItemMeta()).getOwner();
			Player p2 = plugin.getServer().getPlayerExact(name);
			WWPlayer wwp2 = plugin.getGame().getPlayer(p2.getUniqueId());
			if (wwp.getPlayedClass() != null)
				wwp.getPlayedClass().handlePlayerClick(plugin, wwp, wwp2);
		}
	}
}
