package net.samagames.werewolves.entities;

import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class SkinDisguise extends WWDisguise
{
    private String uuid;
    
    public SkinDisguise(String uuid)
    {
        super(null);
        this.uuid = uuid;
    }

    @Override
    public void disguisePlayer(Player player)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        this.uuid.getClass();
        //TODO
    }

    //WereWolf username = SM_Werewolf
}
