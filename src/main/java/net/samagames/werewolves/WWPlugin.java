package net.samagames.werewolves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.samagames.api.SamaGamesAPI;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.classes.Witch;
import net.samagames.werewolves.game.GameCommand;
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
public class WWPlugin extends JavaPlugin
{
    public static final String NAME_BICOLOR = ChatColor.GRAY + "" + ChatColor.BOLD + "Loups Garous";

    private WWGame game;
    private SamaGamesAPI api;
    private Map<WWClass, Integer> roles;
    private List<WWHouse> houses;
    private Location spawn;

    @Override
    public void onEnable()
    {
        this.api = SamaGamesAPI.get();
        this.roles = new HashMap<>();
        this.houses = new ArrayList<>();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getServer().getScheduler().runTaskTimer(this, new InfiniteSleepTask(this), 50, 50);
        getServer().getPluginCommand("game").setExecutor(new GameCommand(this));

        loadClassesConfiguration();
        
        if (this.api.getGameManager().getGameProperties().getOption("vocal", new JsonPrimitive(false)).getAsBoolean())
            this.game = new VocalGame(this);
        else
            this.game = new TextGame(this);
        this.api.getGameManager().registerGame(this.game);

        int n = 0;
        for (Integer i : this.roles.values())
            n += i;
        JsonElement element;
        int i = 0;
        while ((element = this.api.getGameManager().getGameProperties().getOption("house-" + i, null)) != null)
        {
            WWHouse loc = JsonUtils.getHouse(element);
            if (loc != null)
            {
                loc.safeReset();
                this.houses.add(loc);
            }
            i++;
        }
        this.spawn = JsonUtils.getLocation(this.api.getGameManager().getGameProperties().getOption("spawn", null));
        if (n != this.api.getGameManager().getGameProperties().getMaxSlots() ||
                this.api.getGameManager().getGameProperties().getMaxSlots() != this.api.getGameManager().getGameProperties().getMinSlots() ||
                n != this.houses.size() || this.spawn == null)
        {
            getServer().getLogger().severe("[WWPlugin] Problem in server slots (min != max != roles != houses) or missing spawn");
            getServer().shutdown();
        }
    }

    private void loadClassesConfiguration()
    {
        for (WWClass clazz : WWClass.getValues())
        {
            JsonElement element = this.api.getGameManager().getGameProperties().getOption(clazz.getID(), null);
            if (element == null)
                continue ;
            int n = element.getAsInt();
            if (n != 0)
            {
                this.roles.put(clazz, n);
                getServer().getLogger().info("[WWPlugin] Class loaded " + clazz.getID() + " x" + n);
                if (clazz == WWClass.WITCH)
                    ((Witch)clazz).setHouseLocation(JsonUtils.getLocation(this.api.getGameManager().getGameProperties().getOption("witch-house", null)),
                            JsonUtils.getLocation(this.api.getGameManager().getGameProperties().getOption("witch-stand", null)));
            }
        }
    }

    public WWGame getGame()
    {
        return this.game;
    }

    public SamaGamesAPI getApi()
    {
        return this.api;
    }

    public Map<WWClass, Integer> getRoles()
    {
        return this.roles;
    }

    public List<WWHouse> getHouses()
    {
        return this.houses;
    }

    public Location getRandomSpawn()
    {
        Random r = new Random();
        final double range = 3D;
        return this.spawn.clone().add(r.nextDouble() * range - range / 2, 0, r.nextDouble() * range - range / 2);
    }
}
