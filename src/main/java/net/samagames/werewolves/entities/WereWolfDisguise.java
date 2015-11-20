package net.samagames.werewolves.entities;

import java.util.UUID;

import net.samagames.werewolves.util.PacketUtils;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class WereWolfDisguise extends WWDisguise
{
	public WereWolfDisguise()
	{
		super(null);
	}
	
	@Override
	public void disguisePlayer(Player player)
	{
		if (!(player instanceof CraftPlayer))
			return ;
		PacketUtils.broadcastDestroyPacket(player.getEntityId());
		PacketUtils.broadcastSpawnPlayerPacket(player, UUID.fromString("da508ecc-dbd9-46c5-8095-47b91aa4ff5f"));
	}
	
	//this.werewolfAccount.put(ClanType.WerewolfBite, "SM_Werewolf");
    //this.werewolfAccount.put(ClanType.WildBite, "BM_Werewolf");
	//this.werewolfAccountId.put(ClanType.WerewolfBite, UUID.fromString("b68a8f00-7d24-4c52-b6ad-1423bfbe26ee"));
    //this.werewolfAccountId.put(ClanType.WildBite, UUID.fromString("da508ecc-dbd9-46c5-8095-47b91aa4ff5f"));
}
