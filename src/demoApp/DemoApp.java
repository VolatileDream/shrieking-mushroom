package demoApp;

import java.io.IOException;
import java.net.InetAddress;

import shriekingMushroom.CommonAccessObject;
import shriekingMushroom.config.IVariable;
import shriekingMushroom.config.IVariableHandler;
import shriekingMushroom.config.IVariableStore;
import shriekingMushroom.config.implementation.JointVariableStore;
import shriekingMushroom.config.implementation.UserVariableHandler;
import shriekingMushroom.config.implementation.def.DefaultNetworkingVariableStore;
import shriekingMushroom.config.implementation.def.DefaultProtocolVariableStore;
import shriekingMushroom.events.IEventQueue;
import shriekingMushroom.events.implementation.EventQueue;
import shriekingMushroom.logging.ILogger;
import shriekingMushroom.logging.implementation.JoinedLogger;
import shriekingMushroom.logging.implementation.LocalTextLogger;
import shriekingMushroom.logging.implementation.StandardErrorLogger;
import shriekingMushroom.networking.TCPConnectionBuilder;
import shriekingMushroom.networking.TCPNetworkAccess;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.networking.implementation.tcp.TCPConnectionPool;
import shriekingMushroom.protocol.IMessageFactory;
import shriekingMushroom.protocol.INetworkEventsHandler;
import shriekingMushroom.protocol.IProtocolConnection;
import shriekingMushroom.protocol.events.IProtoConnectEvent;
import shriekingMushroom.protocol.events.IProtoReadEvent;
import shriekingMushroom.protocol.events.IProtocolEvent;
import shriekingMushroom.protocol.implementation.ProtocolSetup;
import shriekingMushroom.threading.IRunner;
import shriekingMushroom.threading.IWaiter;
import shriekingMushroom.threading.implementation.CommonWaitTime;
import demoApp.protocol.DemoHandlers;
import demoApp.protocol.DemoMessage;
import demoApp.protocol.DemoMessageFactory;
import demoApp.protocol.DemoMessageType;
import demoApp.protocol.interfaces.DemoMyMessage;

public class DemoApp {

	public static void doDemo() throws IOException {

		// *

		System.out.println("Starting demo");

		// General Setup

		DefaultNetworkingVariableStore netStore = new DefaultNetworkingVariableStore();
		DefaultProtocolVariableStore protoStore = new DefaultProtocolVariableStore();
		IVariableStore store = new JointVariableStore(netStore, protoStore);
		IVariableHandler handler = new UserVariableHandler();

		IVariable logFile = handler.GetRequiredVariable("logging.logFile",
				store);
		int logFlag = handler.GetRequiredVariableAsInt("logging.logProfile",
				store);

		ILogger log1 = new LocalTextLogger(logFile.GetValue(), logFlag);
		ILogger log2 = new StandardErrorLogger(logFlag);

		ILogger log = new JoinedLogger(log1, log2);

		CommonAccessObject cao = new CommonAccessObject(store, handler, log);

		System.out.println("General Setup completed...");

		// Network Layer Setup

		long netSleep = cao.handler.GetRequiredVariableAsInt(
				"threading.default_sleep_millis", cao.store);

		IWaiter netWait = new CommonWaitTime(netSleep);

		TCPNetworkAccess netAccess = new TCPConnectionPool(cao, netWait);

		TCPConnectionBuilder netF = new TCPConnectionBuilder(netAccess);

		netF.withPort(54444);
		IEventQueue<INetworkEvent> nQueue = new EventQueue<INetworkEvent>();
		netF.withQueue(nQueue);

		System.out.println("Network layer setup completed...");

		// Protocol Layer setup

		DemoMessageType text = new DemoMessageType("TEXT");
		DemoMessageType con = new DemoMessageType("CONNECTION");
		DemoMessageType data = new DemoMessageType("DATA");

		IMessageFactory<DemoMyMessage> msgF = new DemoMessageFactory(cao.log, text,
				con, data);

		INetworkEventsHandler<DemoMyMessage> pHandler = new DemoHandlers(cao, msgF);

		ProtocolSetup<DemoMyMessage> pSetup = new ProtocolSetup<DemoMyMessage>(cao,
				pHandler);
		IEventQueue<IProtocolEvent<DemoMyMessage>> pQueue = new EventQueue<IProtocolEvent<DemoMyMessage>>();
		IRunner mover = pSetup.build(nQueue, pQueue);

		System.out.println("Protocol layer setup completed...");

		// Actual usage Setup

		// protocol layer start
		mover.start();

		System.out.println("Protocol layer started...");

		// networking listen
		IRunner tcp54444 = netF.allowConnection();
		tcp54444.start();

		System.out.println("Server started...");

		// networking connect
		InetAddress adr = InetAddress.getLocalHost();
		netF.connect(adr);

		System.out.println("Client started...");

		long timeOut = 10 * 1000;// one minute

		long start = System.currentTimeMillis();
		long current = 0;

		System.out.println("Starting to wait");

		while (start + timeOut > current) {
			current = System.currentTimeMillis();

			if (pQueue.poll()) {

				IProtocolEvent<DemoMyMessage> event = pQueue.remove();
				if (event instanceof IProtoConnectEvent<?>) {

					IProtoConnectEvent<DemoMyMessage> pc = (IProtoConnectEvent<DemoMyMessage>) event;
					IProtocolConnection<DemoMyMessage> c = pc.getConnection();
					System.out.println("Connected to: " + c.getAddress() + ":"
							+ c.getPort());

					DemoMyMessage m = new DemoMessage("Hello World".getBytes(), text);

					c.write(m);

				} else if (event instanceof IProtoReadEvent<?>) {

					IProtoReadEvent<DemoMyMessage> pr = (IProtoReadEvent<DemoMyMessage>) event;
					IProtocolConnection<DemoMyMessage> c = pr.getConnection();

					System.out.println("Sent from(" + c.getAddress() + ":"
							+ c.getPort() + "):"
							+ new String(pr.getMessage().getContents()));

				} else {

					System.out.println(event.getClass());

				}
			}
			sleep(200);
		}

		System.out.println("Done waiting");

		// networking close
		tcp54444.stop();
		netAccess.close();

		System.out.println("networking layer shutdown...");

		mover.stop();

		System.out.println("Protocol layer shutdown...");
		// */

		System.out.println("DONE DEMO");

	}

	private static void sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e1) {
		}
	}

}
