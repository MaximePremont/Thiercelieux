package net.samagames.werewolves.util;

import net.samagames.werewolves.game.WWHouse;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils
{
	private JsonUtils(){}

	public static WWHouse getHouse(JsonElement element)
	{
		try {
			JsonObject json = element.getAsJsonObject();
	        World world = Bukkit.getWorlds().get(0);
	        double bx = json.get("bedx").getAsDouble();
	        double by = json.get("bedy").getAsDouble();
	        double bz = json.get("bedz").getAsDouble();
	        Location bed = new Location(world, bx, by, bz);
	        double dx = json.get("displayx").getAsDouble();
	        double dy = json.get("displayy").getAsDouble();
	        double dz = json.get("displayz").getAsDouble();
	        Location display = new Location(world, dx, dy, dz);
	        return new WWHouse(display, bed);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Location getLocation(JsonElement object)
    {
        JsonObject json = object.getAsJsonObject();
        String w = json.get("world").getAsString();
        if (w == null)
        	return null;
        World world = Bukkit.getWorld(w);
        if (world == null)
        	return null;
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        try
        {
            float yaw = (float)json.get("yaw").getAsDouble();
            float pitch = (float)json.get("pitch").getAsDouble();
            return new Location(world, x, y, z, yaw, pitch);
        }
        catch (UnsupportedOperationException | NullPointerException ex)
        {
            return new Location(world, x, y, z);
        }
    }
}
