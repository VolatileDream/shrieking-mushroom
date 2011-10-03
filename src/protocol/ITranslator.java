package protocol;


/**
 * The ITranslator interface is used by the MessageMover
 * to get messages from the event queue and push it into the IMessageFilter
 *
 */
public interface ITranslator<M extends IMessage> {
	
	public M getMessage();
	
}
