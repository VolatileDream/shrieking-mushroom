package protocol.implementation;

import networking.IConnection;
import protocol.IMessageFactory;
import protocol.implementation.interfaces.MyMessage;
import core.ByteBuilder;
import core.Tupple;
import core.Util;
import core.logging.ILogger;
import core.logging.ILogger.LogLevel;

public class MessageFactory implements IMessageFactory<MyMessage> {

	/*
	Message Format:
	<{TYPE}:{LENGTH}@{CONTENTS}>
	 */

	private static final byte START = '<';
	private static final byte DELIM = ':';
	private static final byte SIZE = '@';
	private static final byte END = '>';

	private final ILogger logger;
	private final Tupple<MessageType,Integer>[] handleTypes;

	@SuppressWarnings("unchecked")
	public MessageFactory( ILogger log, MessageType ...types ){
		logger = log;
		handleTypes = new Tupple[ types.length ];
		for( int i = 0; i < types.length; i++){
			handleTypes[i] = new Tupple<MessageType,Integer>( types[i], types[i].typeName.length() );

		}
	}

	@Override
	public byte[] transformToBytes( MyMessage m ){
		ByteBuilder bb = new ByteBuilder();
		bb.append(START);
		bb.append(m.getType().typeName);
		bb.append(DELIM);
		bb.appendAsInt( m.getContents().length );
		bb.append(SIZE);
		bb.append(m.getContents());
		bb.append(END);
		return bb.getBytes();

	}
	
	@Override
	public Tupple<MyMessage,Integer> transformToMessage( IConnection con, byte[] array ) {
		Message result = new Message( con );
		int iStart = getFirstValidMessageStart( array );

		if( iStart != 0 ){
			String err = "Message doesn't start with : '"+ START+"'";
			logger.Log(err, LogLevel.Error);
			//TODO What do?
			//throw new MalformedMessageException( err );
		}
		int iDelim = Util.firstIndex( DELIM, array, iStart );

		int typeLength = iDelim - iStart - 1;

		if( Util.tuppleContainsSecond( handleTypes, typeLength ) < 0 ){
			String err = "Unknown message type has length : "+typeLength;
			logger.Log(err, LogLevel.Error );
			//TODO What do?
			//throw new MalformedMessageException( err );
		}

		byte[] type = Util.sub( iStart+1, typeLength, array );
		result.type = new MessageType( new String( type ) );

		int iSize = Util.firstIndex( SIZE, array, iDelim );
		int sizeLength = iSize - iDelim - 1;
		
		byte[] sz = Util.sub( iDelim+1, sizeLength, array );

		int size = Integer.parseInt( new String(sz) );

		if( array.length < iSize+size+1 ){
			//TODO What do?
			//throw new InsufficientMessageLengthException();
		}

		byte[] contents = Util.sub( iSize+1, size, array );

		if( array[iSize+size+1] != END ){
			//TODO what do?
			//throw new MalformedMessageException("Message doesn't end with : '"+END+"'");
		}

		result.contents = contents;

		return new Tupple<MyMessage, Integer>( result, iSize + size + 2 );
	}

	/**
	 * Returns the first index that is a possible start of a message, or -1 if there is no possible start.
	 * @param contents the buffer to check
	 * @param isTorturedStream If the buffer or input has been "tortured" and may not have the end of the previous message
	 * @return Returns the first index that could possibly be the start of a message or -1 if there is no possible start
	 */
	private int getFirstValidMessageStart( byte[] contents ){
		int index = -1;
		while( index < contents.length ){

			index = core.Util.firstIndex( START, contents, index+1 );

			if( index < 0 ) return -1;

			if( isValidMessageStart( contents, index ) ){
				return index;
			}
		}
		return -1;
	}

	/**
	 * Returns true if the index indicated by start is the valid start of a message
	 * @param contents the buffer to read
	 * @param start index to check
	 * @param isTorturedStream If this is a "tortured" stream, and may not have an ending of a previous message
	 * @return Returns true if the index passed is a valid start message
	 */
	private boolean isValidMessageStart( byte[] contents, int start ){

		if( start > contents.length ){
			throw new IllegalArgumentException("Offset value must be less then byte[] length");
		}

		if( contents[start-1] != END ){
			return false;
		}
		

		if( contents[start] != START ){
			return false;
		}

		start = start+1;
		int typeLength = Util.firstIndex( DELIM, contents, start) - start;

		boolean contains = Util.tuppleContainsSecond( handleTypes, typeLength ) >= 0;

		if( !contains ){
			return false;
		}

		return true;
	}
		
}
