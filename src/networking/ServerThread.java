package networking;


import java.util.ArrayList;
import java.util.List;

import networking.io.*;


public class ServerThread {

	private Server serv;
	
	private ArrayList<IMessageStream> connections = new ArrayList<IMessageStream>();
	
	public ServerThread( Server s ){
		serv = s;
		
	}
	
}
