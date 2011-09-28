package core.uservars.implementation;

import java.io.File;

import core.uservars.ILanguageLibrary;
import core.uservars.IVariableFactory;
import core.uservars.IVariableStore;

public class UserLanguageLookup implements ILanguageLibrary {

	private String libLocation;
	private String libExtension;
	private IVariableFactory varFac;
	
	public UserLanguageLookup( String libraryDir, String extension, IVariableFactory fac ){
		this.libLocation = fixPath( libraryDir );
		this.varFac = fac;
		this.libExtension = extension;
	}
	
	@Override
	public boolean tryGetLanguage(String str, IVariableStore[] vs_pt ) {
		return varFac.TryGetVariablesFromLocation( getPathOfLanguage(str), vs_pt);
	}

	private String fixPath( String str ){
		if( !str.endsWith( File.separator ) ){
			return str + File.separator;
		}
		return str;
	}
	
	private String getPathOfLanguage( String lib ){
		return libLocation + lib + libExtension;
	}
	
}
