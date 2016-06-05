package net.samagames.werewolves.entities;

import net.samagames.werewolves.util.PacketUtils;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public abstract class WWDisguise
{
    private EntityType type;

    protected WWDisguise(EntityType type)
    {
        this.type = type;
    }

    public void disguisePlayer(Player player)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        PacketUtils.broadcastDestroyPacket(player.getEntityId());
        PacketUtils.broadcastSpawnEntityPacket(player.getEntityId(), type, player.getLocation(), null);
    }

    public void undisguisePlayer(Player player)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        PacketUtils.broadcastDestroyPacket(player.getEntityId());
        PacketUtils.broadcastSpawnPlayerPacket(player, player.getUniqueId());
    }
}
