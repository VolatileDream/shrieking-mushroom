package demoApp;

import java.io.IOException;

public class run {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		if( args.length <= 0 ){
			System.out.println("No demo specified");
			System.out.println("Demos are:");
			System.out.println("\tnettcp");
			System.out.println("\tmulticast");
		}
		
		for( String s : args ){
		
			if( s.equals("nettcp") ){
				DemoApp.doNetAndTCP();
			}else if( s.equals("multicast") ){
				DemoApp.doMulticast();
			}
		}

	}

}
