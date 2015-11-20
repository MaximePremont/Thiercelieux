package net.samagames.werewolves.util;

import net.samagames.werewolves.WWPlugin;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class RulesUtil
{
	private RulesUtil() {}

	public static ItemStack getRulesBook()
	{
		String[] raw = new String[]{
				"\n   ]--------------[" +
				"\n    " + WWPlugin.NAME_BICOLOR + "§0" +
				"\n    par §lSamaGames§0" +
				"\n   ]--------------[" +
				"\n" +
				"\n" +
				"\n §11.§0 Comment jouer ?" +
				"\n" +
				"\n §12.§0 Objectifs",
				
				"\n §lComment jouer ?§0\n" +
				"\n Vous incarnez une\n petite" +
				" cellule qui doit\n grandir" +
				" avec le temps\n\n Mangez d'autres\n" + 
				" cellules plus petites\n pour" +
				" augmenter votre\n taille !",
				
				"\n §lComment jouer ?§0\n" +
				"\n Mais attention à vos\n adversaires" +
				" qui\n peuvent vous manger\n\n Soyez" +
				" intelligents et\n restez gros !",
				
				"\n       §lObjectifs §0 " +
				"\n       §lMode §6§lFFA §0 \n\n" +
				" Chacun pour soi,\n devenez le" +
				" meilleur\n de tous !",
				
				"\n       §lObjectifs §0" +
				"\n      §lMode §6§lTeams §0 \n\n" +
				" Gagnez le plus de\n points" +
				" pour votre\n équipe (§c§lRouge§0," +
				" §2§lVert§0\n ou §1§lBleu§0) !",
				
				"\n       §lObjectifs §0" +
				"\n    §lMode §6§lHardcore §0 \n\n" +
				" Vous voulez un peu\n de challenge ? Alors\n retrouvez" +
				" le FFA\n avec le mode\n   §lHardcore§0 !",
				
				"\n\nJeu développé par :" +
				"\n\n - §lRigner§0" +
				"\n\n\n\n\n\n\n      SamaGames" + 
				"\n Tout droits réservés."
		};
		ItemStack item = ItemsUtil.setItemMeta(Material.WRITTEN_BOOK, 1, (short)0, "&6&lLivre de règles", null);
		BookMeta meta = (BookMeta)item.getItemMeta();
		meta.addPage(raw);
		item.setItemMeta(meta);
		return item;
	}
}
