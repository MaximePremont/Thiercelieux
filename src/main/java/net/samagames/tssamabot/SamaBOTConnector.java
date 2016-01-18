package net.samagames.tssamabot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class SamaBOTConnector
{
    private String hostname;
    private int portnumber;
    private static final String OK = "OK";
    private static final String NOK = "NOK";
    
    public SamaBOTConnector(String host, int port)
    {
        hostname = host;
        portnumber = port;
    }

    public synchronized String[] createChannel(String name, String[] players)
    {
        try
        {
            Socket socket = new Socket(hostname, portnumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            StringBuilder sb = new StringBuilder();
            sb.append("1 ");
            sb.append(name);
            if (players != null)
                for (String p : players)
                    sb.append(" " + p);
            out.println(sb.toString());
            out.close();
            
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));            
            String message = inFromClient.readLine();
            inFromClient.close();
            socket.close();
            
            if (message.equals(NOK))
                return new String[]{"ERROR_ERROR_ERROR"};
            String[] result = message.split(":");
            if (!result[0].equals(OK))
                return new String[]{"ERROR_ERROR_ERROR"};
            return Arrays.copyOfRange(result, 1, result.length);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        return new String[]{"ERROR_ERROR_ERROR"}; //Désolé, à cause de Sonar jpeux pas return null
    }
}
