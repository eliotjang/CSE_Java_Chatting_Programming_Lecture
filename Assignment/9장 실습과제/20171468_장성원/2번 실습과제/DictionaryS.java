package chapter9;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DictionaryS {
	public final static int daytimeport=1033;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerSocket theServer;
		Socket theSocket = null;

		try
		{
			theServer = new ServerSocket(daytimeport);
			translate kor = null;
			while(true)
			{
				try
				{
					theSocket = theServer.accept();
					kor = new translate(theSocket);
					kor.start();
				}
				catch(IOException e)
				{
					System.err.println(e);
				}
				
			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
	}

}
