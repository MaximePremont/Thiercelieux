package net.samagames.werewolves.classes;

import java.util.ArrayList;

import net.samagames.tools.chat.ChatUtils;
import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.WinType;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Angel extends WWClass
{
	private int turns;
	
	protected Angel()
	{
		super("angel", "L'", "&bAnge", new ItemStack(Material.SNOW_BLOCK), new String[]{
			"Votre objectif est de mourir",
			"lors de la première nuit ou de", "la première journée. Sinon, vous",
			"devenez un simple villageois."}, null);
		turns = 0;
	}

	@Override
	public boolean canPlayAtNight()
	{
		turns++;
		return false;
	}

	@Override
	public WinType getWinType()
	{
		return WinType.INNOCENTS;
	}

	@Override
	public boolean handleDeath(WWPlugin plugin, WWPlayer who, WWClass by)
	{
		if (turns == 1)
		{
			plugin.getServer().getScheduler().runTask(plugin, () -> {
				ArrayList<String> list = new ArrayList<String>();
				list.add(ChatUtils.getCenteredText("L'ange (" + who.getDisplayName() + ") est mort au premier tour."));
				list.add(ChatUtils.getCenteredText("Il gagne donc la partie !"));
				plugin.getGame().getCoherenceMachine().getTemplateManager().getBasicMessageTemplate().execute(list);
				who.win();
				plugin.getGame().finishGame();
			});
			return false;
		}
		return true;
	}
}
