package demoApp.protocol;

import java.util.Hashtable;

import shriekingMushroom.CommonAccessObject;
import shriekingMushroom.logging.ILogger.LogLevel;
import shriekingMushroom.networking.IConnection;
import shriekingMushroom.networking.events.INetCloseEvent;
import shriekingMushroom.networking.events.INetConnectEvent;
import shriekingMushroom.networking.events.INetErrorEvent;
import shriekingMushroom.networking.events.INetReadEvent;
import shriekingMushroom.networking.events.INetworkEvent;
import shriekingMushroom.protocol.IMessageFactory;
import shriekingMushroom.protocol.INetworkEventsHandler;
import shriekingMushroom.protocol.events.IProtoReadEvent;
import shriekingMushroom.protocol.events.IProtocolEvent;
import shriekingMushroom.protocol.implementation.ProtocolConnectionFactory;
import shriekingMushroom.protocol.implementation.events.ProtocolCloseEvent;
import shriekingMushroom.protocol.implementation.events.ProtocolConnectEvent;
import shriekingMushroom.protocol.implementation.events.ProtocolReadEvent;
import shriekingMushroom.util.Tupple;
import shriekingMushroom.util.Util;
import demoApp.protocol.interfaces.DemoMyMessage;

public class DemoHandlers implements INetworkEventsHandler<DemoMyMessage> {

	private final CommonAccessObject cao;
	private final ProtocolConnectionFactory<DemoMyMessage> protoFact;
	private final IMessageFactory<DemoMyMessage> msgFact;

	private final Hashtable<TableEntry, DemoConnectionInfo> table = new Hashtable<TableEntry, DemoConnectionInfo>();

	public DemoHandlers(CommonAccessObject c, IMessageFactory<DemoMyMessage> f) {
		cao = c;
		msgFact = f;
		protoFact = new ProtocolConnectionFactory<DemoMyMessage>(msgFact);
	}

	@Override
	public IProtocolEvent<DemoMyMessage> handleClose(INetCloseEvent e) {
		IConnection c = e.getConnection();
		TableEntry te = TableEntry.getEntry(c);
		table.remove(te);
		return new ProtocolCloseEvent<DemoMyMessage>(e, protoFact.transform(c));
	}

	@Override
	public IProtocolEvent<DemoMyMessage> handleConnect(INetConnectEvent e) {
		IConnection c = e.getConnection();
		TableEntry te = TableEntry.getEntry(c);
		table.put(te, new DemoConnectionInfo());
		return new ProtocolConnectEvent<DemoMyMessage>(e, protoFact.transform(c));
	}

	@Override
	public IProtocolEvent<DemoMyMessage> handleError(INetErrorEvent e) {
		return null;
	}

	@Override
	public IProtocolEvent<DemoMyMessage> handleRead(INetReadEvent e) {
		IConnection con = e.getConnection();
		TableEntry te = TableEntry.getEntry(con);

		DemoConnectionInfo info = table.get(te);

		if (info == null) {
			cao.log.Log("Couldn't find connection info for connection",
					LogLevel.Error);
			info = new DemoConnectionInfo();
			table.put(te, info);
		}
		info.buffer = Util.concat(info.buffer, e.getRead());
		Tupple<DemoMyMessage, Integer> result = msgFact
				.transformToMessage(info.buffer);

		if (result.Item2 > 0) {
			// shift the buffer if we get a non zero return, they might want to
			// clean it.
			info.buffer = Util.shift(result.Item2, info.buffer);
		}

		if (result.Item1 == null) {
			return null;
		}

		IProtoReadEvent<DemoMyMessage> event = new ProtocolReadEvent<DemoMyMessage>(e,
				protoFact.transform(con), result.Item1);
		return event;
	}

	@Override
	public IProtocolEvent<DemoMyMessage> handleUnknown(INetworkEvent e) {
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