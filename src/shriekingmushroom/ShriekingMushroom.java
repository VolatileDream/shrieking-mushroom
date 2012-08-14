package shriekingmushroom;

import java.io.IOException;

import shriekingmushroom.tcp.TcpMushroom;
import shriekingmushroom.threading.IResetableStopper;
import shriekingmushroom.threading.implementation.Stopper;

public class ShriekingMushroom {
	
	private IResetableStopper stopper;
	private TcpMushroom tcp;
	
	public ShriekingMushroom() throws IOException{
		stopper = new Stopper();
		tcp = new TcpMushroom( stopper );
	}
	
	public TcpMushroom getTcp(){
		return tcp;
	}
	
	public void exit(){
		stopper.setStop();
	}
	
}
