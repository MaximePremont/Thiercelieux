package net.samagames.werewolves.classes;

import net.samagames.werewolves.util.WinType;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LittleGirl extends WWClass
{
	public LittleGirl()
	{
		super("littlegirl", "La", "Petite-Fille", new ItemStack(Material.RED_ROSE), new String[]{"Durant la nuit, espionnez les loups-garous"}, null);
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
	
	@Override
	public String getTextAtNight()
	{
		return "Ecoutez les loups-garous durant la nuit";
	}
}