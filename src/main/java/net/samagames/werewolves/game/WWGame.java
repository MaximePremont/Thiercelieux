package net.samagames.werewolves.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.samagames.api.games.Game;
import net.samagames.tools.Titles;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.entities.WWDisguise;
import net.samagames.werewolves.task.TurnPassTask;
import net.samagames.werewolves.util.ItemsUtil;
import net.samagames.werewolves.util.RulesUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

public abstract class WWGame extends Game<WWPlayer>
{
	protected WWPlugin plugin;
	protected GameState state;
	protected World world;
	protected int currentevent;
	protected List<WWPlayer> deaths;
	protected BukkitTask passtask;
	
	protected WWGame(WWPlugin plugin)
	{
		super("werewolves", "Loups Garous", "Inspiré du vrai jeu de cartes", WWPlayer.class);
		this.plugin = plugin;
		this.state = GameState.WAITING;
		world = plugin.getServer().getWorlds().get(0);
		deaths = new ArrayList<WWPlayer>();
		passtask = null;
	}

	public void giveWaitingInventory(Player p)
	{
		Inventory inv = p.getInventory();
		inv.clear();
		inv.setItem(4, ItemsUtil.setItemMeta(Material.NETHER_STAR, 1, (short)0, ChatColor.AQUA + "" + ChatColor.BOLD + "Sélecteur", null));
		inv.setItem(7, RulesUtil.getRulesBook());
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
	}
	
	public void givePlayingInventory(WWPlayer wwp)
	{
		Player p = wwp.getPlayerIfOnline();
		if (p == null)
			return ;
		Inventory inv = p.getInventory();
		inv.clear();
		if (wwp.getPlayedClass() != null)
			inv.setItem(4, wwp.getPlayedClass().getItem());
		if (wwp.getPlayedClass() != null && wwp.getPlayedClass().hasSelector())
			inv.setItem(0, ItemsUtil.SELECTOR);
		inv.setItem(7, RulesUtil.getRulesBook());
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
	}
	
	public void giveSleepingInventory(WWPlayer wwp)
	{
		Player p = wwp.getPlayerIfOnline();
		if (p == null)
			return ;
		Inventory inv = p.getInventory();
		inv.clear();
		if (wwp.getPlayedClass() != null)
			inv.setItem(4, wwp.getPlayedClass().getItem());
		inv.setItem(7, RulesUtil.getRulesBook());
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
	}
	
	public void giveSpectatingInventory(Player p)
	{
		Inventory inv = p.getInventory();
		inv.clear();
		inv.setItem(7, RulesUtil.getRulesBook());
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
	}
	
	public void giveVotingInventory(Player p)
	{
		Inventory inv = p.getInventory();
		inv.clear();
		inv.setItem(0, ItemsUtil.SELECTOR);
		inv.setItem(7, RulesUtil.getRulesBook());
		inv.setItem(8, plugin.getApi().getGameManager().getCoherenceMachine().getLeaveItem());
	}
	
	@Override
	public void startGame()
	{
		if (this.isGameStarted())
			return ;
		super.startGame();
		this.state = GameState.PREPARE;
		selectRoles();
		Bukkit.getScheduler().runTaskLater(plugin, () -> startNight(), 80);
	}
	
	public void nextNightEvent()
	{
		this.cancelPassTask();
		WWClass[] classes = WWClass.NIGHT_ORDER;
		if (currentevent >= 0)
		{
			Set<WWPlayer> oldplayers = this.getPlayersByClass(classes[currentevent]);
			WWDisguise olddisguise = classes[currentevent].getDisguise();
			for (WWPlayer player : oldplayers)
			{
				Player p = player.getPlayerIfOnline();
				player.getHouse().teleportToBed(p);
				if (olddisguise != null)
					olddisguise.undisguisePlayer(p);
				giveSleepingInventory(player);
			}
			classes[currentevent].handleNightTurnEnd(plugin, oldplayers);
		}
		currentevent++;
		if (currentevent >= classes.length)
		{
			showDeads();
			startDay();
			return ;
		}
		Set<WWPlayer> players = this.getPlayersByClass(classes[currentevent]);
		if (players.isEmpty())
		{
			nextNightEvent();
			return ;
		}
		broadcastMessage(this.getCoherenceMachine().getGameTag() + ChatColor.WHITE + " " + classes[currentevent].getPrefix() + " " + classes[currentevent].getName() + ChatColor.WHITE + " se réveille" + (classes[currentevent].getPrefix().equals("Les") ? "nt" : "") + " !");
		String n = classes[currentevent].getTextAtNight();
		WWDisguise disguise = classes[currentevent].getDisguise();
		for (WWPlayer player : players)
		{
			Player p = player.getPlayerIfOnline();
			player.getHouse().removeFromBed(p);
			p.teleport(plugin.getRandomSpawn());
			if (n != null)
				Titles.sendTitle(p, 5, 50, 5, "", ChatColor.GOLD + n);
			if (disguise != null)
				disguise.disguisePlayer(p);
			givePlayingInventory(player);
		}
		classes[currentevent].handleNightTurnStart(plugin, players);
		passtask = plugin.getServer().getScheduler().runTaskTimer(plugin, new TurnPassTask(plugin, classes[currentevent], true), 20L, 20L);
	}
	
	public void nextDayEvent()
	{
		
	}
	
	private void showDeads()
	{
		if (deaths.isEmpty())
		{
			broadcastMessage(this.coherenceMachine.getGameTag() + " Personne n'est mort cette nuit.");
			return ;
		}
		StringBuilder sb = new StringBuilder(this.coherenceMachine.getGameTag() + " Victime" + (deaths.size() == 1 ? "" : "s") + " de cette nuit : ");
		int i = 0;
		for (WWPlayer player : deaths)
		{
			player.setSpectator();
			Player p = player.getPlayerIfOnline();
			if (p != null)
				p.getWorld().strikeLightningEffect(p.getLocation());
			if (i > 0)
				sb.append(ChatColor.WHITE + ", ");
			sb.append(ChatColor.YELLOW + player.getOfflinePlayer().getName());
			i++;
		}
		broadcastMessage(sb.toString());
		deaths.clear();
	}

	public void selectRoles()
	{
		Map<WWClass, Integer> list = plugin.getRoles();
		List<WWHouse> houses = plugin.getHouses();
		Random r = new Random();
		for (WWPlayer player : this.getInGamePlayers().values())
		{
			if (player.isSpectator() || player.isModerator() || !player.isOnline())
				continue ;
			WWClass newclass = null;
			int n = r.nextInt(list.size());
			int i = 0;
			for (WWClass clazz : list.keySet())
			{
				if (n == i)
				{
					newclass = clazz;
					break ;
				}
				i++;
			}
			player.setPlayedClass(newclass);
			Titles.sendTitle(player.getPlayerIfOnline(), 5, 70, 5, "", ChatColor.GOLD + "Vous êtes : " + newclass.getName());
			n = list.get(newclass);
			if (n > 1)
				list.put(newclass, n - 1);
			else
				list.remove(newclass);
			giveSleepingInventory(player);
			n = r.nextInt(houses.size());
			player.setHouse(houses.get(n));
			houses.remove(n);
		}
	}
	
	public Set<WWPlayer> getPlayersByClass(WWClass... clazz)
	{
		Set<WWPlayer> set = new HashSet<WWPlayer>();
		for (WWPlayer player : this.getInGamePlayers().values())
		{
			if (player.isSpectator() || player.isModerator() || !player.isOnline())
				continue ;
			for (WWClass tmp : clazz)
				if (player.getPlayedClass() != null && player.getPlayedClass().equals(tmp))
				{
					set.add(player);
					break ;
				}
		}
		return set;
	}
	
	public void startNight()
	{
		this.state = GameState.NIGHT;
		world.setTime(15000L);
		currentevent = -1;
		for (WWPlayer player : this.getInGamePlayers().values())
		{
			if (player.isSpectator() || player.isModerator() || !player.isOnline() || player.getHouse() == null)
				continue ;
			Player p = player.getPlayerIfOnline();
			player.getHouse().teleportToBed(p);
			player.getHouse().displayName(p.getName());
		}
		nextNightEvent();
	}
	
	public void startDay()
	{
		this.state = GameState.DAY_1;
		for (WWPlayer player : this.getInGamePlayers().values())
		{
			if (player.isSpectator() || player.isModerator() || !player.isOnline() || player.getHouse() == null)
				continue ;
			Player p = player.getPlayerIfOnline();
			player.getHouse().removeFromBed(p);
			p.teleport(plugin.getRandomSpawn());
		}
		world.setTime(3000L);
		currentevent = -1;
	}
	
	@Override
	public void handleLogin(Player player)
	{
		super.handleLogin(player);
		giveWaitingInventory(player);
	}
	
	@Override
	public void handleLogout(Player player)
	{
		super.handleLogout(player);
	}
	
	public GameState getGameState()
	{
		return state;
	}
	
	public void setGameState(GameState state)
	{
		this.state = state;
	}
	
	public void broadcastMessage(String msg)
	{
		for (Player player : plugin.getServer().getOnlinePlayers())
			player.sendMessage(msg);
		plugin.getServer().getConsoleSender().sendMessage(msg);
	}
	
	public boolean isCurrentlyPlayed(WWClass clazz)
	{
		if (getGameState() != GameState.NIGHT)
			return false;
		return WWClass.NIGHT_ORDER[currentevent].equals(clazz);
	}
	
	public List<WWPlayer> getDeadPlayers()
	{
		return deaths;
	}
	
	public void cancelPassTask()
	{
		if (passtask == null)
			return ;
		passtask.cancel();
		passtask = null;
	}
	
	public abstract void handleChatMessage(WWPlayer player, String message);
}
