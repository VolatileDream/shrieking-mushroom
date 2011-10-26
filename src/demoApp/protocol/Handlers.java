package demoApp.protocol;

import java.util.Hashtable;

import networking.IConnection;
import networking.events.INetCloseEvent;
import networking.events.INetConnectEvent;
import networking.events.INetErrorEvent;
import networking.events.INetReadEvent;
import networking.events.INetworkEvent;
import protocol.IMessageFactory;
import protocol.INetworkEventsHandler;
import protocol.events.IProtoReadEvent;
import protocol.events.IProtocolEvent;
import protocol.implementation.ProtocolConnectionFactory;
import protocol.implementation.events.ProtocolCloseEvent;
import protocol.implementation.events.ProtocolConnectEvent;
import protocol.implementation.events.ProtocolReadEvent;
import core.CommonAccessObject;
import core.logging.ILogger.LogLevel;
import core.util.Tupple;
import core.util.Util;
import demoApp.protocol.interfaces.MyMessage;

public class Handlers implements INetworkEventsHandler<MyMessage> {

	private final CommonAccessObject cao;
	private final ProtocolConnectionFactory<MyMessage> protoFact;
	private final IMessageFactory<MyMessage> msgFact;

	private final Hashtable<TableEntry, ConnectionInfo> table = new Hashtable<TableEntry, ConnectionInfo>();

	public Handlers(CommonAccessObject c, IMessageFactory<MyMessage> f) {
		cao = c;
		msgFact = f;
		protoFact = new ProtocolConnectionFactory<MyMessage>(msgFact);
	}

	@Override
	public IProtocolEvent<MyMessage> handleClose(INetCloseEvent e) {
		IConnection c = e.getConnection();
		TableEntry te = TableEntry.getEntry(c);
		table.remove(te);
		return new ProtocolCloseEvent<MyMessage>(e, protoFact.transform(c));
	}

	@Override
	public IProtocolEvent<MyMessage> handleConnect(INetConnectEvent e) {
		IConnection c = e.getConnection();
		TableEntry te = TableEntry.getEntry(c);
		table.put(te, new ConnectionInfo());
		return new ProtocolConnectEvent<MyMessage>(e, protoFact.transform(c));
	}

	@Override
	public IProtocolEvent<MyMessage> handleError(INetErrorEvent e) {
		return null;
	}

	@Override
	public IProtocolEvent<MyMessage> handleRead(INetReadEvent e) {
		IConnection con = e.getConnection();
		TableEntry te = TableEntry.getEntry(con);

		ConnectionInfo info = table.get(te);

		if (info == null) {
			cao.log.Log("Couldn't find connection info for connection",
					LogLevel.Error);
			info = new ConnectionInfo();
			table.put(te, info);
		}
		info.buffer = Util.concat(info.buffer, e.getRead());
		Tupple<MyMessage, Integer> result = msgFact
				.transformToMessage(info.buffer);

		if (result.Item2 > 0) {
			// shift the buffer if we get a non zero return, they might want to
			// clean it.
			info.buffer = Util.shift(result.Item2, info.buffer);
		}

		if (result.Item1 == null) {
			return null;
		}

		IProtoReadEvent<MyMessage> event = new ProtocolReadEvent<MyMessage>(e,
				protoFact.transform(con), result.Item1);
		return event;
	}

	@Override
	public IProtocolEvent<MyMessage> handleUnknown(INetworkEvent e) {
		cao.log.Log("Unknown INetworkEvent type: " + e.getClass().getName(),
				LogLevel.Warn);
		return null;
	}

}

class TableEntry {

	final byte[] address;
	final int port;

	private TableEntry(byte[] a, int p) {
		address = a;
		port = p;
	}

	protected static TableEntry getEntry(IConnection c) {
		return new TableEntry(c.getAddress().getAddress(), c.getPort());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TableEntry) {
			return equals((TableEntry) o);
		}
		return false;
	}

	public boolean equals(TableEntry te) {
		// ports and addresses must be the same.
		return this.port == te.port
				&& Util.sameContents(this.address, te.address);
	}

	@Override
	public int hashCode() {
		int code = 0;
		int i = 0;
		while (i < address.length) {
			code = code << 8;
			code += address[i];
			i++;
		}
		code += port;
		return code;
	}

}