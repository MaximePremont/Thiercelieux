package net.samagames.tssamabot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.Bukkit;

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
            sb.append(hostName);//Need refactor
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
                return new String[]{"ERROR_ERROR_ERROR"};
            String[] result = message.split(":");
            return Arrays.copyOfRange(result, 1, result.length);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
        return new String[]{"ERROR_ERROR_ERROR"}; //Désolé, à cause de Sonar jpeux pas return null
    }
}
