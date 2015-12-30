package net.samagames.werewolves.util;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticlesUtil
{
    private ParticlesUtil(){}

    public static void sendParticle(Player player, EnumParticle particle, float data, Location loc)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                particle,
                true,
                (float)loc.getX(),
                (float)loc.getY(),
                (float)loc.getZ(),
                (float)0,
                (float)0,
                (float)0,
                data,
                1);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }
}
