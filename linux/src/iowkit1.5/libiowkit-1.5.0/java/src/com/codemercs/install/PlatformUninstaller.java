package com.codemercs.install;

import java.io.*;
import java.util.regex.PatternSyntaxException;

/**
 * Base class for platform-specific uninstallation classes.
 * @author Eberhard Fahle
 */ 
class PlatformUninstaller {
    /** The name of the library we want to remove */
    protected final String libName;
    /** The is the plattform-specific name of the library */
    protected final String libFileName;
    /** The name of the jar that we want to remove */
    protected String jarName;
    /** An array of all the directories in the java.library.path */
    protected File [] javaLibDirs;
    /** An array of all the directories in the java.ext.dirs */
    protected File [] javaExtDirs;

    /**
     * Creates a new PlatformUninstaller.
     * @param libName the name of the library that is to be removed
     * @param jarName the name of the jar-file that is to be removed
     */
    protected PlatformUninstaller(String libName, String jarName) {
	this.libName=libName;
	this.libFileName=System.mapLibraryName(libName);
	if(!jarName.endsWith(".jar"))
	    jarName=jarName+".jar";
	this.jarName=jarName;
	buildJavaDirs();
    }

    /**
     * Build two arrays of filedescriptors, one from the java.library.path
     * and one from the java.ext.dirs.
     */
    private void buildJavaDirs() {
	String path;
	String [] splitPath=null;
 	//get the library path for the JVM
	path=System.getProperty("java.library.path");
	if(path==null || path.equals("")) {
	    //no entries found
	    javaLibDirs=new File[0];
	}
	else {
	    try {
		splitPath=path.split(System.getProperty("path.separator"));
	    }
	    catch(PatternSyntaxException pse) {
		//I guess this can never happen, but ..??
		javaLibDirs=new File[0];
	    }
	    if(splitPath.length==0) {
		//No entries ? Huh...
		javaLibDirs=new File[0];
	    }
	    javaLibDirs=new File[splitPath.length];
	    for(int i=0,j=0;i<splitPath.length;i++) {
		try {
		    javaLibDirs[j]=new File(splitPath[i]).getCanonicalFile();
		    j++;
		}
		catch(IOException ioe) {
		    //problem with the canonical name
		    //the index for the filearray is not updated
		}
	    }
	}
 	//get the extentions path for the JVM
	path=System.getProperty("java.ext.dirs");
	if(path==null || path.equals("")) {
	    //no entries found
	    javaExtDirs=new File[0];
	}
	else {
	    try {
		splitPath=path.split(System.getProperty("path.separator"));
	    }
	    catch(PatternSyntaxException pse) {
		//I guess this can never happen, but ..??
		javaExtDirs=new File[0];
	    }
	    if(splitPath.length==0) {
		//No entries ? Huh...
		javaExtDirs=new File[0];
	    }
	    javaExtDirs=new File[splitPath.length];
	    for(int i=0,j=0;i<splitPath.length;i++) {
		try {
		    javaExtDirs[j]=new File(splitPath[i]).getCanonicalFile();
		    j++;
		}
		catch(IOException ioe) {
		    //problem with the canonical name
		    //the index for the filearray is not updated
		}
	    }
	}

    }

    /**
     * Tests wether the library exists in the java.library.path.
     * @return boolean True if the library exists at least in one of 
     * the java.library.path directories, false otherwise.
     */
    public boolean libraryExists() {
	File d=null;
	File l=null;
	for(int i=0;i<javaLibDirs.length;i++) {
	    d=javaLibDirs[i];
	    if(d!=null && d.exists() && d.isDirectory()) {
		l=new File(d,libFileName);
		if(l.exists() && l.isFile()) {
		    //we have the lib in one dir at least
		    return true;
		}
	    }
	}
	return false;
    }
    
    /**
     * Tests wether the jar-file exists in the  java.ext.dirs.
     * @return boolean True if the library at least in one of 
     * the java.library.path directories, false otherwise.
     */
    public boolean jarExists() {
	File d=null;
	File j=null;
	for(int i=0;i<javaExtDirs.length;i++) {
	    d=javaExtDirs[i];
	    if(d!=null && d.exists() && d.isDirectory()) {
		j=new File(d,jarName);
		if(j.exists() && j.isFile()) {
		    //we have the jar in one dir at least
		    return true;
		}
	    }
	}
	return false;
    }
	
    /**
     * Delete all the library-files from the java.library.path.
     * @return boolean True if the library was deleted from all  
     * the java.library.path directories, false otherwise.
     */
    public boolean deleteLibrary() {
	boolean retval=true;
	File d=null;
	File l=null;
	for(int i=0;i<javaLibDirs.length;i++) {
	    d=javaLibDirs[i];
	    if(d!=null && d.exists() && d.isDirectory()) {
		l=new File(d,libFileName);
		if(l.exists() && l.isFile()) {
		    if(retval && !l.delete()) {
			//we failed at least in one location
			retval=false;
		    }
		}
	    }
	}
	return retval;
    }
    
    /**
     * Delete all the jar-files from the java.ext.dirs.
     * @return boolean True if the jarfile was deleted from all  
     * the java.ext.dirs directories, false otherwise.
     */
    public boolean deleteJar() {
	boolean retval=true;
	File d=null;
	File j=null;
	for(int i=0;i<javaExtDirs.length;i++) {
	    d=javaExtDirs[i];
	    if(d!=null && d.exists() && d.isDirectory()) {
		j=new File(d,jarName);
		if(j.exists() && j.isFile()) {
		    if(retval && !j.delete()) {
			//we failed at least in one location
			retval=false;
		    }
		}
	    }
	}
	return retval;
    }
}
