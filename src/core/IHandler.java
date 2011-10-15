package core;

public interface IHandler<M,E> {

	public M handle( E e );
	
}
