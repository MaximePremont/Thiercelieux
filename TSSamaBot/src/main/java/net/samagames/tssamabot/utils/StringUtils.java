package net.samagames.tssamabot.utils;

import java.util.Random;

public class StringUtils
{
	private StringUtils(){}
	
	public static String randomString()
	{
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++)
		{
			int n = r.nextInt(36);
			sb.append((char)(n < 26 ? 'A' + n : '0' + (n - 26)));
		}
		return sb.toString();
	}
}
