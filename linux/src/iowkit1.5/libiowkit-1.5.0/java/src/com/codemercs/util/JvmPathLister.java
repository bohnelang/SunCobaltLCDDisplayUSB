package com.codemercs.util;

import java.util.regex.PatternSyntaxException;

/**
 * List the System.properties 'java.library.path' and
 * 'java.ext.dirs' to stdout
 */
public class JvmPathLister {
    public static void main(String [] argv) {
	String [] splitPath=null;

	String libpath=System.getProperty("java.library.path");
 	String extpath=System.getProperty("java.ext.dirs");

	if(libpath==null || libpath.equals(""))
	    System.out.println("Property 'java.library.path' is empty!");
	else {
	    try {
		splitPath=libpath.split(System.getProperty("path.separator"));
		if(splitPath.length==0)
		    System.out.println("Property 'java.library.path' has no entries!");
		else {
		    System.out.println("Property 'java.library.path' has "+splitPath.length+" entries...");
		    for(int i=0;i<splitPath.length;i++)
			System.out.println(splitPath[i]);
		}
		    
	    }
	    catch(PatternSyntaxException pse) {
		//I guess this can never happen, but ..??
		System.err.println(pse);
		System.exit(1);
	    }
	}
	if(extpath==null || extpath.equals(""))
	    System.out.println("Property 'java.ext.dirs' is empty!");
	else {
	    try {
		splitPath=extpath.split(System.getProperty("path.separator"));
		if(splitPath.length==0)
		    System.out.println("Property 'java.ext.dirs' has no entries!");
		else {
		    System.out.println("Property 'java.ext.dirs' has "+splitPath.length+" entries...");
		    for(int i=0;i<splitPath.length;i++)
			System.out.println(splitPath[i]);
		}
		    
	    }
	    catch(PatternSyntaxException pse) {
		//I guess this can never happen, but ..??
		System.err.println(pse);
		System.exit(1);
	    }
	}
	System.exit(0);
    }
}
	    
