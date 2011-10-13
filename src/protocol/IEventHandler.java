package protocol;

public interface IEventHandler<M,E> {

	public M handle( E e );
	
}
