package net.samagames.werewolves.listener;

import java.util.Set;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.GameState;
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
import org.bukkit.event.player.*;
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
        if (ev.getItem() != null && ev.getItem().getType() == Material.NETHER_STAR && this.plugin.getGame().getGameState() == GameState.WAITING)
            ev.getClass();//TODO: Class Selector
        ev.setCancelled(true);
        if (ev.getItem() != null && ev.getItem().getType() == ItemsUtil.SELECTOR.getType() && this.plugin.getGame().getGameState() == GameState.NIGHT)
        {
            WWPlayer wwp = this.plugin.getGame().getPlayer(ev.getPlayer().getUniqueId());
            if (wwp == null || wwp.isModerator() || wwp.isSpectator())
                return ;
            if (!this.plugin.getGame().isCurrentlyPlayed(wwp.getPlayedClass()))
            {
                ev.getPlayer().sendMessage(ChatColor.RED + "Ce n'est pas à vous de jouer.");
                return ;
            }
            Inventory i = this.plugin.getServer().createInventory(null, 27, "Sélecteur");
            Set<WWPlayer> excluded = this.plugin.getGame().getPlayersByClass(wwp.getPlayedClass());
            for (WWPlayer player : this.plugin.getGame().getInGamePlayers().values())
            {
                if (excluded.contains(player))
                    continue ;
                i.addItem(ItemsUtil.createHead(player.getOfflinePlayer().getName()));
            }
            ev.getPlayer().openInventory(i);
            return ;
        }
        if (ev.getItem() != null && ev.getItem().getType() == ItemsUtil.SELECTOR.getType() && (this.plugin.getGame().getGameState() == GameState.DAY_1 || this.plugin.getGame().getGameState() == GameState.DAY_2))
        {
            WWPlayer wwp = this.plugin.getGame().getPlayer(ev.getPlayer().getUniqueId());
            if (wwp == null || wwp.isModerator() || wwp.isSpectator())
                return ;
            Inventory i = this.plugin.getServer().createInventory(null, 27, "Sélecteur");
            for (WWPlayer player : this.plugin.getGame().getInGamePlayers().values())
            {
                if (this.plugin.getGame().getGameState() == GameState.DAY_2 && !player.isInSecondTurn())
                    continue ;
                i.addItem(ItemsUtil.createHead(player.getOfflinePlayer().getName()));
            }
            ev.getPlayer().openInventory(i);
            return ;
        }
        if (this.plugin.getGame().getGameState() == GameState.NIGHT && (ev.getAction() == Action.LEFT_CLICK_BLOCK || ev.getAction() == Action.RIGHT_CLICK_BLOCK))
        {
            WWPlayer player = this.plugin.getGame().getPlayer(ev.getPlayer().getUniqueId());
            if (player.getPlayedClass() != null && this.plugin.getGame().isCurrentlyPlayed(player.getPlayedClass()))
                player.getPlayedClass().handlePlayerBlockClick(this.plugin, player, ev.getClickedBlock());
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent ev)
    {
        ev.setCancelled(true);
        WWPlayer player = this.plugin.getGame().getPlayer(ev.getPlayer().getUniqueId());
        if (player != null)
            this.plugin.getGame().handleChatMessage(player, ev.getMessage());
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
        if (ev.getEntity() == null || !(ev.getEntity() instanceof Player) || ev.getDamager() == null || !(ev.getDamager() instanceof Player) || this.plugin.getGame().getGameState() != GameState.NIGHT)
            return ;
        WWPlayer source = this.plugin.getGame().getPlayer(ev.getDamager().getUniqueId());
        WWPlayer target = this.plugin.getGame().getPlayer(ev.getEntity().getUniqueId());
        if (source.getPlayedClass() != null && this.plugin.getGame().isCurrentlyPlayed(source.getPlayedClass()))
            source.getPlayedClass().handlePlayerClick(this.plugin, source, target);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent ev)
    {
        if (ev.getEntity() == null || !(ev.getEntity() instanceof Player) || ev.getCause() != DamageCause.ENTITY_ATTACK)
        {
            if (ev.getCause() == DamageCause.FIRE)
                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> ev.getEntity().setFireTicks(0), 1);
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInventoryClick(InventoryClickEvent ev)
    {
        ev.setCancelled(true);
        Player p;
        if (!(ev.getWhoClicked() instanceof Player) || (p = (Player)ev.getWhoClicked()) == null || ev.getCurrentItem() == null || ev.getCurrentItem().getType() == Material.AIR || ev.getClickedInventory() == null)
            return ;
        WWPlayer wwp = this.plugin.getGame().getPlayer(p.getUniqueId());
        if (wwp == null || wwp.isModerator() || wwp.isSpectator() || !wwp.isOnline())
            return ;
        if (wwp.getPlayedClass() != null && this.plugin.getGame().isCurrentlyPlayed(wwp.getPlayedClass()) && wwp.getPlayedClass().overrideInventoryClick(this.plugin, wwp, ev.getClickedInventory(), ev.getCurrentItem()))
            return ;
        if (ev.getCurrentItem().getType() == Material.SKULL_ITEM && ev.getCurrentItem().getDurability() == 3)
        {
            String name = ((SkullMeta)ev.getCurrentItem().getItemMeta()).getOwner();
            Player p2 = this.plugin.getServer().getPlayerExact(name);
            if (p2 == null)
                return ;
            WWPlayer wwp2 = this.plugin.getGame().getPlayer(p2.getUniqueId());
            if (wwp2 == null || wwp2.isModerator() || wwp2.isSpectator())
                return ;
            if (this.plugin.getGame().getGameState() == GameState.NIGHT)
            {
                if (wwp.getPlayedClass() != null && this.plugin.getGame().isCurrentlyPlayed(wwp.getPlayedClass()))
                    wwp.getPlayedClass().handlePlayerClick(this.plugin, wwp, wwp2);
            }
            else if (this.plugin.getGame().getGameState() == GameState.DAY_1 || this.plugin.getGame().getGameState() == GameState.DAY_2)
                this.plugin.getGame().handleDayVote(wwp, wwp2);
        }
    }

    @EventHandler
    public void onPotionDrink(PlayerItemConsumeEvent ev)
    {
        if (ev.getItem() != null && ev.getItem().getType().equals(Material.POTION))
            ev.setCancelled(true);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent ev)
    {
        WWPlayer wwp = this.plugin.getGame().getPlayer(ev.getPlayer().getUniqueId());
        if (wwp != null && wwp.getHouse() != null)
            wwp.getHouse().teleportToBed(ev.getPlayer());
    }

    @EventHandler
    public void onSecondHand(PlayerSwapHandItemsEvent ev)
    {
        ev.setCancelled(true);
    }
}
