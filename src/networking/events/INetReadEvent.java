package networking.events;

public interface INetReadEvent extends INetworkEvent {
	
	/**
	 * @return Returns the contents that were read.
	 */
	public byte[] getRead();
	
}
