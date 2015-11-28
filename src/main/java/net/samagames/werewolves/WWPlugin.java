package net.samagames.werewolves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.samagames.api.SamaGamesAPI;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.game.TextGame;
import net.samagames.werewolves.game.VocalGame;
import net.samagames.werewolves.game.WWGame;
import net.samagames.werewolves.game.WWHouse;
import net.samagames.werewolves.listener.PlayerListener;
import net.samagames.werewolves.listener.WorldListener;
import net.samagames.werewolves.task.InfiniteSleepTask;
import net.samagames.werewolves.util.JsonUtils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class WWPlugin extends JavaPlugin
{
	private static WWPlugin instance;
	public static final String NAME_BICOLOR = ChatColor.GRAY + "" + ChatColor.BOLD + "Loups Garous";
	
	private WWGame game;
	private SamaGamesAPI api;
	private Map<WWClass, Integer> roles;
	private List<WWHouse> houses;
	private Location spawn;
	
	@Override
	public void onEnable()
	{
		instance = this;
		
		api = SamaGamesAPI.get();
		roles = new HashMap<WWClass, Integer>();
		houses = new ArrayList<WWHouse>();
		
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		getServer().getScheduler().runTaskTimer(this, new InfiniteSleepTask(this), 50, 50);
		
		for (WWClass clazz : WWClass.VALUES)
		{
			JsonElement element = api.getGameManager().getGameProperties().getOption(clazz.getID(), null);
			if (element == null)
				continue ;
			int n = element.getAsInt();
			if (n != 0)
			{
				roles.put(clazz, n);
				getServer().getLogger().info("[WWPlugin] Class loaded " + clazz.getID() + " x" + n);
			}
		}
		if (api.getGameManager().getGameProperties().getOption("vocal", new JsonPrimitive(false)).getAsBoolean())
			game = new VocalGame(this);
		else
			game = new TextGame(this);
		api.getGameManager().registerGame(game);
		
		int n = 0;
		for (Integer i : roles.values())
			n += i;
		JsonElement element;
		int i = 0;
		while ((element = api.getGameManager().getGameProperties().getOption("house-" + i, null)) != null)
		{
			WWHouse loc = JsonUtils.getHouse(element).safeReset();
			if (loc != null)
				houses.add(loc);
			i++;
		}
		spawn = JsonUtils.getLocation(api.getGameManager().getGameProperties().getOption("spawn", null));
		if (n != api.getGameManager().getGameProperties().getMaxSlots() ||
				api.getGameManager().getGameProperties().getMaxSlots() != api.getGameManager().getGameProperties().getMinSlots() ||
				n != houses.size() || spawn == null)
		{
			getServer().getLogger().severe("[WWPlugin] Problem in server slots (min != max != roles != houses) or missing spawn");
			getServer().shutdown();
			return ;
		}
	}
	
	public static WWPlugin getInstance()
	{
		return instance;
	}
	
	public WWGame getGame()
	{
		return game;
	}
	
	public SamaGamesAPI getApi()
	{
		return api;
	}
	
	public Map<WWClass, Integer> getRoles()
	{
		return roles;
	}
	
	public List<WWHouse> getHouses()
	{
		return houses;
	}
	
	public Location getRandomSpawn()
	{
		Random r = new Random();
		final double range = 3D;
		return spawn.clone().add(r.nextDouble() * range - range / 2, 0, r.nextDouble() * range - range / 2);
	}
}
