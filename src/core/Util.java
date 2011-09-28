package core;

import core.exceptions.TryGetException;

public class Util {

	public static int characterPlaces( int i ){
		if( i < 0){
			throw new IllegalArgumentException("Can't check a negative number, use characterPlacesAbs instead");
		}
		int places = 0;
		while( i > 0 ){
			places++;
			i = i/10;
		}
		return places;
	}
	
	public static int characterPlacesAbs( int i){
		if( i < 0 ){
			return 1+characterPlaces( Math.abs(i) );
		}else{
			return characterPlaces( i );
		}
	} 
	
	public static int firstIndex( char c, byte[] buffer, int startIndex ){
		return firstIndex( (byte)c, buffer, startIndex );
	}
	public static int firstIndex( byte b, byte[] buffer, int startIndex ){
		while( startIndex < buffer.length ){
			if( buffer[startIndex] == b ){
				return startIndex;
			}
			startIndex++;
		}
		return -1;
	}
	
	public static byte[] shift( int i, byte[] array ){
		byte[] results = new byte[ array.length - i ];
		System.arraycopy(array, i, results, 0, results.length);
		return results;
	}
	
	public static byte[] sub( int start, int length, byte[] array ){
		if( array.length < start+length ){
			throw new ArrayIndexOutOfBoundsException(start+length);
		}
		byte[] result = new byte[length];
		System.arraycopy(array, start, result, 0, length);
		return result;
	}
	
	public static byte[] add( byte[] start, byte[] end ){
		byte[] dst = new byte[ start.length + end.length ];
		System.arraycopy(start, 0, dst, 0, start.length);
		System.arraycopy(end, 0, dst, start.length, end.length);
		return dst;
	}
	
	public static byte[] getRandom( int length ){
		byte[] result = new byte[length];
		for(int i=0; i < length; i++){
			result[i] = (byte)(Math.random()*Byte.MAX_VALUE*2);
		}
		return result;
	}
	
	public static <T,G> int tuppleContainsFirst( Tupple<T,G>[] ta, T one ){
		return tuppleContains( ta, one, null );
	}
	
	public static <T,G> int tuppleContainsSecond( Tupple<T,G>[] ta, G two ){
		return tuppleContains( ta, null, two );
	}
	
	private static <T,G> int tuppleContains( Tupple<T,G>[] ta, T one, G two ){
		
		if( two == null ){
			for( int i=0; i < ta.length; i++ ){
				if( ta[i].Item1.equals( one ) ){
					return i;
				}
			}
		}else if( one == null ){
			for( int i=0; i < ta.length; i++ ){
				if( ta[i].Item2.equals( two ) ){
					return i;
				}
			}
		}
		return -1;
	}
	
	public static <T> void TryGetArrayCheck( T[] t ){
		if( t == null || t.length != 1 ){
			ThrowTryGetArrayException();
		}
		t[0] = null;
	}
	
	public static void TryGetArrayCheck( int[] i ){
		if( i == null || i.length != 1 ){
			ThrowTryGetArrayException();
		}
		i[0] = 0;
	}
	
	public static void TryGetArrayCheck( boolean[] i ){
		if( i == null || i.length != 1 ){
			ThrowTryGetArrayException();
		}
		i[0] = false;
	}
	
	public static void TryGetArrayCheck( char[] i ){
		if( i == null || i.length != 1 ){
			ThrowTryGetArrayException();
		}
		i[0] = 0;
	}

	private static void ThrowTryGetArrayException(){
		throw new TryGetException("To use a TryGet method you need to pass in an array of length 1");
	}
	
}
