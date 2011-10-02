package core.events;

public interface IReadEvent extends IEvent {
	
	/**
	 * @return Returns the contents that were read.
	 */
	public byte[] getRead();
	
}
