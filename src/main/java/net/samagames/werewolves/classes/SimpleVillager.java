package net.samagames.werewolves.classes;

import net.samagames.werewolves.util.WinType;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SimpleVillager extends WWClass
{
	public SimpleVillager()
	{
		super("villager", "Un", "&eSimple Villageois", new ItemStack(Material.SAND), new String[]{"Un simple villageois, qui n'a rien de spécial..."}, null);
	}

	@Override
	public boolean canPlayAtNight()
	{
		return false;
	}
	
	@Override
	public WinType getWinType()
	{
		return WinType.INNOCENTS;
	}
}
