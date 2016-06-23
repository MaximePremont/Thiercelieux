package net.samagames.werewolves.util;

import net.samagames.tools.RulesBook;
import net.samagames.werewolves.WWPlugin;

import org.bukkit.inventory.ItemStack;

public class RulesUtil
{
    private static ItemStack itemStack;

    private RulesUtil() {}

    public static ItemStack getRulesBook()
    {
        return RulesUtil.itemStack;
    }

    static
    {
        RulesUtil.itemStack = new RulesBook(WWPlugin.NAME_BICOLOR).addOwner("Rigner")
                .addPage("Comment jouer ?", "")
                .addPage("Objectifs", "")
                .addPage("Classes", "").toItemStack();
    }
}
