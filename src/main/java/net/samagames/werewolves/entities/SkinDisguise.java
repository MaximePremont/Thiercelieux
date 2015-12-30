package net.samagames.werewolves.entities;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class SkinDisguise extends WWDisguise
{

    public SkinDisguise(String name)
    {
        super(null);
    }

    @Override
    public void disguisePlayer(Player player)
    {
        if (!(player instanceof CraftPlayer))
            return ;
        //TODO
    }

    //WereWolf username = SM_Werewolf
}
