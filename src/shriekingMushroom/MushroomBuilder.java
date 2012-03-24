package shriekingMushroom;

import java.util.concurrent.BlockingQueue;

import shriekingMushroom.config.IVariableHandler;
import shriekingMushroom.config.IVariableStore;
import shriekingMushroom.config.implementation.JointVariableStore;
import shriekingMushroom.config.implementation.UserVariableHandler;
import shriekingMushroom.config.implementation.def.DefaultNetworkingVariableStore;
import shriekingMushroom.config.implementation.def.DefaultProtocolVariableStore;
import shriekingMushroom.events.QueueBuilder;
import shriekingMushroom.logging.ILogger;
import shriekingMushroom.logging.implementation.StandardErrorLogger;
import shriekingMushroom.networking.MulticastConnectionBuilder;
import shriekingMushroom.networking.MulticastNetworkAccess;
import shriekingMushroom.networking.TCPConnectionBuilder;
import shriekingMushroom.networking.TCPNetworkAccess;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.networking.implementation.multicast.MulticastPool;
import shriekingMushroom.networking.implementation.tcp.TCPConnectionPool;
import shriekingMushroom.protocol.IMessage;
import shriekingMushroom.protocol.INetworkEventsHandler;
import shriekingMushroom.protocol.events.IProtocolEvent;
import shriekingMushroom.protocol.implementation.MessageMover;
import shriekingMushroom.threading.IRunner;
import shriekingMushroom.threading.IWaiter;
import shriekingMushroom.threading.implementation.CommonWaitTime;
import shriekingMushroom.util.Tupple;

/**
 * Class to do the initial wire up so we can easily create networking + protocol layer builders/accessors
 */

public class MushroomBuilder {
	
	private QueueBuilder qBuilder = new QueueBuilder();
	private CommonAccessObject cao = initCao();
	private IWaiter threadWaiter = initThreadWaiter(); 
	private IWaiter queueWaiter = initQueueWaiter();
	
	private static CommonAccessObject initCao(){
		//TODO we should generate our net+protocol stores dynamically
		DefaultNetworkingVariableStore netStore = new DefaultNetworkingVariableStore();
		DefaultProtocolVariableStore protoStore = new DefaultProtocolVariableStore();
		
		IVariableStore store = new JointVariableStore(netStore, protoStore);
		
		IVariableHandler handler = new UserVariableHandler();
		
		int logFlag = handler.GetRequiredVariableAsInt("logging.logProfile", store);
		ILogger log = new StandardErrorLogger(logFlag);

		CommonAccessObject cao = new CommonAccessObject(store, handler, log);
		return cao;
	}
	
	private IWaiter initThreadWaiter(){
		return initWaiter("threading.default_sleep_millis");
	}
	private IWaiter initQueueWaiter(){
		return initWaiter("queue.poll_time_milli");
	}
	private IWaiter initWaiter(String varName){
		long netSleep = cao.handler.GetRequiredVariableAsInt(varName, cao.store);
		IWaiter netWait = new CommonWaitTime(netSleep);
		return netWait;
	}
	
	//------------------------------------- START TCP -------------------------------------
	
	public TCPNetworkAccess getNewTCPAccess(){
		TCPConnectionPool p = new TCPConnectionPool( cao, threadWaiter );
		return p;
	}
	
	public TCPConnectionBuilder getTCPBuilder(){
		return getTCPBuilder( getNewTCPAccess() );
	}
	
	public TCPConnectionBuilder getTCPBuilder( TCPNetworkAccess t ){
		return new TCPConnectionBuilder( t );
	}
	
	//-------------------------------------- END TCP --------------------------------------
	//------------------------------------ START MULTI ------------------------------------
	
	public MulticastNetworkAccess getNewMulticastAccess(){
		MulticastNetworkAccess m = new MulticastPool( cao, threadWaiter );
		return m;
	}
	
	public MulticastConnectionBuilder getMulticastBuilder(){
		return getMulticastBuilder( getNewMulticastAccess() );
	}
	
	public MulticastConnectionBuilder getMulticastBuilder( MulticastNetworkAccess mna ){
		return new MulticastConnectionBuilder( mna );
	}
	
	//------------------------------------- End MULTI -------------------------------------
	
	public <M extends IMessage> Tupple<IRunner,BlockingQueue<IProtocolEvent<M>>> getProtocol( BlockingQueue<INetworkEvent> queue, INetworkEventsHandler<M> h ){
		BlockingQueue<IProtocolEvent<M>> q = this.createQueue();
		IRunner mover = getProtocol(queue, q, h);
		return new Tupple<IRunner, BlockingQueue<IProtocolEvent<M>>>( mover, q );
	}	
	
	public <M extends IMessage> IRunner getProtocol( BlockingQueue<INetworkEvent> queue, BlockingQueue<IProtocolEvent<M>> pQ, INetworkEventsHandler<M> h ){
		MessageMover<M> mover = new MessageMover<M>(queueWaiter, h, queue, pQ);
		return mover;
	}
	
	public <T> BlockingQueue<T> createQueue(){
		return qBuilder.buildQueue();
	}
	
}
