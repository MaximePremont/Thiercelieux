package net.samagames.tsexample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TSSamaSimpleClient
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine())
		{
			String read = sc.nextLine();
			String hostName = "0.0.0.0";
			int portNumber = 6789;
			try
			{
			    Socket echoSocket = new Socket(hostName, portNumber);
			    PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			    out.println(read);
			    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));            
	            System.out.println(inFromClient.readLine());
			    out.close();
	            inFromClient.close();
			    echoSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sc.close();
	}
}
