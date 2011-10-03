package core.events;

public interface IEventHandler<M extends IEvent> {
	
	public void handleEvent( M e );
	
}
