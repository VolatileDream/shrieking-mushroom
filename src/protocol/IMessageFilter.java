package protocol;


public interface IMessageFilter<M extends IMessage> {
	
	public void doAction( M m );
	
}
