package net.samagames.werewolves.classes;

import java.util.Arrays;
import java.util.Set;

import net.samagames.werewolves.WWPlugin;
import net.samagames.werewolves.game.WWPlayer;
import net.samagames.werewolves.util.WinType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cupidon extends WWClass
{
	private boolean played;
	private WWPlayer[] couple;
	
	protected Cupidon()
	{
		super("cupidon", "", "&dCupidon", new ItemStack(Material.RED_ROSE), new String[]{"Choisissez deux joueurs", "qui s'aimeront jusqu'à la mort"}, null);
		played = false;
		couple = new WWPlayer[2];
		Arrays.fill(couple, null);
	}

	@Override
	public boolean canPlayAtNight()
	{
		return !played;
	}

	@Override
	public WinType getWinType()
	{
		return WinType.INNOCENTS;
	}
	
	@Override
	public void handleNightTurnStart(WWPlugin plugin, Set<WWPlayer> players)
	{
		played = true;
	}
	
	@Override
	public void handlePlayerClick(WWPlugin plugin, WWPlayer source, WWPlayer target)
	{
		source.getPlayerIfOnline().sendMessage(ChatColor.RED + "Vous avez choisi : " + target.getDisplayName());
		if (couple[0] == null)
			couple[0] = target;
		else if (couple[1] == null)
		{
			couple[1] = target;
			plugin.getGame().nextNightEvent();
		}
	}
	
	@Override
	public void handleNightTurnEnd(WWPlugin plugin, Set<WWPlayer> players)
	{
		String msg;
		Player[] p = new Player[2];
		p[0] = couple[0] == null ? null : couple[0].getPlayerIfOnline();
		p[1] = couple[1] == null ? null : couple[1].getPlayerIfOnline();
		if (p[0] == null || p[1] == null)
			msg = ChatColor.RED + "Aucun choix de fait, il n'y aura pas d'amoureux dans cette partie.";
		else
		{
			msg = plugin.getGame().getCoherenceMachine().getGameTag() + ChatColor.RED + "Les deux amoureux sont " + ChatColor.BOLD + p[0].getDisplayName() + ChatColor.RED + " et " + ChatColor.BOLD + p[1].getDisplayName();
			couple[0].setCouple(couple[1]);
			couple[1].setCouple(couple[0]);
			p[0].sendMessage(plugin.getGame().getCoherenceMachine().getGameTag() + ChatColor.RED + "Vous êtes amoureux de " + p[1].getDisplayName() + ", si l'un d'entre vous meurt, l'autre aussi !");
			p[1].sendMessage(plugin.getGame().getCoherenceMachine().getGameTag() + ChatColor.RED + "Vous êtes amoureux de " + p[0].getDisplayName() + ", si l'un d'entre vous meurt, l'autre aussi !");
		}
		for (WWPlayer wwp : players)
			if (wwp.isOnline())
				wwp.getPlayerIfOnline().sendMessage(msg);
	}
}
