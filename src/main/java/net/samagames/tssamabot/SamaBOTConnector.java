package net.samagames.tssamabot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Enumeration;

public class SamaBOTConnector
{
	private SamaBOTConnector(){}
	
	private static final String hostName = "0.0.0.0";
	private static final int portNumber = 6789;
	protected static final String OK = "OK";
	protected static final String NOK = "NOK";
	
	public static synchronized String[] createChannel(String name, String[] players)
	{
		try
		{
		    Socket echoSocket = new Socket(hostName, portNumber);
		    PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
		    StringBuilder sb = new StringBuilder();
		    sb.append(getInternalIpv4().getHostAddress());
		    sb.append(" 6790 1 ");
		    sb.append(name);
		    if (players != null)
		    	for (String p : players)
		    		sb.append(" " + p);
		    out.println(sb.toString());
		    echoSocket.close();
		    out.close();
		    
		    ServerSocket serverSocket = new ServerSocket(6790);
			Socket connectionSocket = serverSocket.accept();
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String message = inFromClient.readLine();
			inFromClient.close();
			connectionSocket.close();
			serverSocket.close();
			if (message.equals(NOK))
				return null;
			String[] result = message.split(":");
			System.out.println((result.length - 1) + " players online on TeamSpeak");
			for (int i = 1; i < result.length; i++)
				System.out.println("  > " + result[i]);
			return Arrays.copyOfRange(result, 1, result.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final InetAddress getInternalIpv4() throws IOException
    {
        NetworkInterface i = NetworkInterface.getByName("venet0:0");//FOR MY DEV VPS
        if (i != null)
	        for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements(); )
	        {
	            InetAddress addr = (InetAddress) en2.nextElement();
	            if (!addr.isLoopbackAddress())
	            {
	                if (addr instanceof Inet4Address)
	                {
	                    return addr;
	                }
	            }
	        }
        i = NetworkInterface.getByName("eth0");//FOR SG
        if (i != null)
	        for (Enumeration<InetAddress> en2 = i.getInetAddresses(); en2.hasMoreElements(); )
	        {
	            InetAddress addr = (InetAddress) en2.nextElement();
	            if (!addr.isLoopbackAddress())
	            {
	                if (addr instanceof Inet4Address)
	                {
	                    return addr;
	                }
	            }
	        }
        InetAddress inet = Inet4Address.getLocalHost();
        return inet;
    }
}
