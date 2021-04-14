SUBDIRS=src tests java 

EXTRA_DIST = java/* doc/* INSTALL_JAVA COMPILE_JAVA build.xml doxy.cfg build.settings

install-exec-hook :
	-ldconfig

java-install : 
if HAS_JAVA
	$(MAKE) -C java java-install
else
	echo "Can't find java-executeable that is needed for java-install!"
endif


java-uninstall :
if HAS_JAVA
	$(MAKE) -C java java-uninstall
else
	echo "Can't find java-executeable that is needed for java-uninstall!"
endif

srcdoc :
if HAS_DOXYGEN
	echo "Running 'doxygen' to create the source-code documentation"; \
	doxygen doxy.cfg
else
	echo "You need to have 'doxygen' installed and in your path to do this!"
endif


