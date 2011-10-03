package networking.events;

public interface IReadEvent extends INetworkEvent {
	
	/**
	 * @return Returns the contents that were read.
	 */
	public byte[] getRead();
	
}
