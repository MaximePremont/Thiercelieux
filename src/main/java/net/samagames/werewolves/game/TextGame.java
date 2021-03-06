package net.samagames.werewolves.game;

import java.util.Set;

import net.samagames.api.games.GamePlayer;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.classes.WereWolf;
import net.samagames.werewolves.util.GameState;

import org.bukkit.ChatColor;

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
public class TextGame extends WWGame
{
    public TextGame(WWPlugin plugin)
    {
        super(plugin);
    }

    @Override
    public void handleChatMessage(WWPlayer player, String message)
    {
        if (player.isModerator())
        {
            broadcastMessage(ChatColor.GRAY + player.getDisplayName() + ChatColor.WHITE + ": " + message);
            return ;
        }
        if (player.isSpectator())
        {
            String msg = ChatColor.GRAY + "[SPEC] " + player.getDisplayName() + ChatColor.WHITE + ": " + message;
            this.getSpectatorPlayers().values().stream().filter(GamePlayer::isOnline).forEach(wwp -> wwp.getPlayerIfOnline().sendMessage(msg));
            return ;
        }
        if (getGameState() != GameState.NIGHT)
        {
            broadcastMessage(ChatColor.GRAY + player.getDisplayName() + ChatColor.WHITE + ": " + message);
            return ;
        }
        if (WWClass.getNightOrder()[this.currentevent] == WWClass.WEREWOLF && player.getPlayedClass() instanceof WereWolf)
        {
            Set<WWPlayer> receivers = this.getPlayersByClass(WWClass.WEREWOLF);
            String msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + player.getDisplayName() + ChatColor.WHITE + ": " + message;
            for (WWPlayer wwp : receivers)
                wwp.getPlayerIfOnline().sendMessage(msg);
            receivers = this.getPlayersByClass(WWClass.LITTLE_GIRL);
            msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + "Loup" + ChatColor.WHITE + ": " + message;
            for (WWPlayer wwp : receivers)
                wwp.getPlayerIfOnline().sendMessage(msg);
            this.plugin.getServer().getConsoleSender().sendMessage(msg);
        }

    }
}
