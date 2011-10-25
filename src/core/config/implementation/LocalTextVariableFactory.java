package core.config.implementation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import core.config.IVariable;
import core.config.IVariableFactory;
import core.config.IVariableStore;
import core.util.Util;

public class LocalTextVariableFactory implements IVariableFactory {

	private final char delimiter = ':';

	@Override
	public IVariable FromSaveString(String str) {
		int index = str.indexOf( delimiter );
		String name = str.substring( 0, index );
		String value = str.substring( index+1 );
		return new UserVariable( name, value );
	}

	@Override
	public String ToSaveString( IVariable var ) {
		return var.GetName() + delimiter + var.GetValue();
	}

	@Override
	public boolean SaveVariablesToLocation(String uri, IVariableStore store) {
		boolean result = false;

		try {
			FileOutputStream fout = new FileOutputStream(uri);

			BufferedWriter bwrite = new BufferedWriter( new OutputStreamWriter( fout ) );

			IVariable[] vars = store.GetVariables();

			for( IVariable v : vars ){
				bwrite.write( this.ToSaveString( v ) );
				bwrite.write('\n');
			}
			bwrite.flush();
			result = true;

			bwrite.close();

		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	@Override
	public boolean TryGetVariablesFromLocation( String uri, IVariableStore[] store ) {
		Util.TryGetArrayCheck( store );

		try {
			FileInputStream conn = new FileInputStream( uri );

			BufferedReader bRead = new BufferedReader( new InputStreamReader( conn ) );

			store[0] = new UserVariableStore();

			String input = null;

			while( true ){

				if( !bRead.ready() ){
					try {
						Thread.sleep( 100 );
					} catch (InterruptedException e) {}
				}

				input = bRead.readLine();

				if( input == null ){
					break;
				}

				IVariable var = this.FromSaveString( input );
				store[0].AddOrChangeValue( var );

				input = null;
			}

			bRead.close();

		} catch (IOException e) {
			e.printStackTrace();
			store[0] = null;
		}

		return store[0] != null;
	}

}
