package net.samagames.tssamabot.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandSender
{
	private Socket socket;
	
	public CommandSender(Socket socket)
	{
		this.socket = socket;
	}
	
	public void send(String msg)
	{
		try {
			System.out.println("Response : " + msg);
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    out.println(msg);
		    out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
