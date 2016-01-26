package net.samagames.tssamabot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import net.samagames.tssamabot.actions.*;
import net.samagames.tssamabot.utils.BotLogger;

public class PacketListener extends Thread
{
	private TSSamaBot main;
	private ServerSocket serverSocket;
	private Socket connectionSocket;
	private BufferedReader inFromClient;
	
	public PacketListener(TSSamaBot bot)
	{
		main = bot;
		serverSocket = null;
	}
	
	@Override
	public void run()
	{
		try {
			serverSocket = new ServerSocket(6789);
	
	        while(true)
	        {
	           connectionSocket = serverSocket.accept();
	           inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	           String message = inFromClient.readLine();
	           BotLogger.log(main.getApi(), "Received: " + message);
	           if (!handle(connectionSocket, message))
	        	   BotLogger.log(main.getApi(), "Error handling : " + message);
	           inFromClient.close();
	           connectionSocket.close();
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inFromClient != null)
					inFromClient.close();
				if (connectionSocket != null)
					connectionSocket.close();
				if (inFromClient != null)
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void stopListener()
	{
		try {
			if (inFromClient != null)
				inFromClient.close();
			if (connectionSocket != null)
				connectionSocket.close();
			if (inFromClient != null)
				serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.stop();
	}
	
	public boolean handle(Socket socket, String message)
	{
		String[] tab = message.split(" ");
		if (tab.length < 1)
			return false;
		CommandSender sender = new CommandSender(socket);
		BotAction botaction = null;
		int action;
		try {
			action = Integer.parseInt(tab[0]);
		} catch (NumberFormatException ex) {
			return false;
		}
		switch (action)
		{
		case 0:
			botaction = new KeepAlive();
			break ;
		case 1:
			botaction = new CreateChannel();
			break ;
		case 2:
			botaction = new DeleteChannel();
			break ;
		case 3:
			botaction = new MutePlayer();
		}
		if (botaction == null)
			return false;
		String response = botaction.run(main, sender, Arrays.copyOfRange(tab, 1, tab.length));
		sender.send(response == null ? "NOK" : response);
		return true;
	}
}
