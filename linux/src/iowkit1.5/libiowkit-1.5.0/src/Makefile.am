lib_LTLIBRARIES	= libiowkit.la
libiowkit_la_SOURCES = iowkit.c iowkitjni.c 
include_HEADERS = iowkit.h 
noinst_HEADERS = iowarrior.h iowkitjni.h jni.h jni_md.h

libiowkit_la_LDFLAGS = \
	-version-info $(LIBIOWKIT_CURRENT):$(LIBIOWKIT_REVISION):$(LIBIOWKIT_AGE) \
	-export-dynamic \
	$(LDADDS) -lpthread

