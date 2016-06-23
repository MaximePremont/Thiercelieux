package net.samagames.werewolves.game;

import java.util.*;

import net.samagames.tools.teamspeak.ChannelProperty;
import net.samagames.tools.teamspeak.TeamSpeakAPI;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.classes.WWClass;
import net.samagames.werewolves.classes.WereWolf;

import org.bukkit.ChatColor;

public class VocalGame extends WWGame
{
    private Map<ChannelProperty, String> properties;
    private int channel;

    public VocalGame(WWPlugin plugin)
    {
        super(plugin);
        this.properties = new HashMap<>();
        this.properties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "1");
    }

    @Override
    public void handleChatMessage(WWPlayer player, String message)
    {
        if (player.isModerator())
        {
            broadcastMessage(player.getDisplayName() + ChatColor.WHITE + ":" + message);
            return ;
        }
        if (WWClass.getNightOrder()[this.currentevent] == WWClass.WEREWOLF && player.getPlayedClass() instanceof WereWolf)
        {
            Set<WWPlayer> receivers = this.getPlayersByClass(WWClass.WEREWOLF);
            String msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + player.getDisplayName() + ChatColor.WHITE + ": " + message;
            for (WWPlayer wwp : receivers)
                wwp.getPlayerIfOnline().sendMessage(msg);
            msg = ChatColor.RED + "[LOUPS] " + ChatColor.GRAY + "Loup" + ChatColor.WHITE + ": " + message;
            receivers = this.getPlayersByClass(WWClass.LITTLE_GIRL);
            for (WWPlayer wwp : receivers)
                wwp.getPlayerIfOnline().sendMessage(msg);
            this.plugin.getServer().getConsoleSender().sendMessage(msg);
            return ;
        }
        player.getPlayerIfOnline().sendMessage(ChatColor.RED + "Le chat est désactivé en mode vocal. Merci de vous exprimer sur TeamSpeak.");
    }

    @Override
    public void startGame()
    {
        if (this.isGameStarted())
            return ;
        List<UUID> list = new ArrayList<>(this.getInGamePlayers().keySet());

        this.broadcastMessage(this.coherenceMachine.getGameTag() + ChatColor.YELLOW + " Création d'un channel sur TeamSpeak ...");
        this.broadcastMessage(ChatColor.RED + " /!\\ Si vous n'êtes pas sur le TeamSpeak (ts.samagames.net), vous serez ejecté de la partie.");

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () ->
        {
            this.channel = TeamSpeakAPI.createChannel(this.plugin.getApi().getServerName().split("_")[1].substring(0, 8).toUpperCase(), this.properties, null);
            if (this.channel == -1)
            {
                this.getInGamePlayers().values().forEach(player -> this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getApi().getGameManager().kickPlayer(player.getPlayerIfOnline(), "Impossible de créer le channel sur le TeamSpeak. Contactez un administrateur.")));
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getServer().shutdown());
            }
            else
            {
                List<UUID> uuid = TeamSpeakAPI.movePlayers(list, this.channel);
                this.getInGamePlayers().values().stream().filter(player -> !uuid.contains(player.getUUID())).forEach(player -> this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getApi().getGameManager().kickPlayer(player.getPlayerIfOnline(), "Vous n'êtes pas sur TeamSpeak (ts.samagames.net) ou alors vous n'avez pas le même pseudo.")));
                this.plugin.getServer().getScheduler().runTask(this.plugin, super::startGame);
            }
        });
    }

    @Override
    public void handleGameEnd()
    {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> TeamSpeakAPI.deleteChannel(this.channel), 280L);
        super.handleGameEnd();
    }
}
