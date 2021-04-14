#ifdef _WIN32
#include "stdafx.h"
#else
#include <string.h>
#include <stdlib.h>
#endif // _WIN32
#include "iowkit.h"
#include "iowkitjni.h"

jlong HandleToJLong(IOWKIT_HANDLE handle)
 {
  return (jlong) ((int) handle);
 }

IOWKIT_HANDLE JLongToHandle(jlong handle)
 {
  return (IOWKIT_HANDLE) ((int) handle);
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    openDevice
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_codemercs_iow_IowKit_openDevice
  (JNIEnv *env, jclass cl)
 {
  return HandleToJLong(IowKitOpenDevice());
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    closeDevice
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_codemercs_iow_IowKit_closeDevice
  (JNIEnv *env, jclass cl, jlong handle)
 {
  IowKitCloseDevice(JLongToHandle(handle));
 }
 

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    write
 * Signature: (JJ[IJ)J
 */
JNIEXPORT jlong JNICALL Java_com_codemercs_iow_IowKit_write
  (JNIEnv *env, jclass cl, jlong handle, jlong numpipe, jintArray buffer)
 {
  unsigned long  i;
  unsigned long  bl;
  ULONG          ret;
  unsigned char *wbuf;
  jint          *body;

  bl = (*env)->GetArrayLength(env, buffer);
  body = (*env)->GetIntArrayElements(env, buffer, 0);
  wbuf = (unsigned char *) malloc(bl*sizeof(unsigned char));

  for(i = 0; i < bl; i++)
    wbuf[i] = (unsigned char) body[i];

  ret = IowKitWrite(JLongToHandle(handle), (ULONG) numpipe, (PCHAR) wbuf, (ULONG) bl);
  (*env)->ReleaseIntArrayElements(env, buffer, body, 0);
  free(wbuf);

  return (jlong) ret;
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    read
 * Signature: (JJJ)[I
 */
JNIEXPORT jintArray JNICALL Java_com_codemercs_iow_IowKit_read
  (JNIEnv *env, jclass cl, jlong handle, jlong numpipe, jlong length)
 {
  ULONG          i;
  ULONG          ret;
  jint          *zbuf;
  unsigned char *rbuf;
  jintArray      jba;

  zbuf = (jint *) malloc((size_t) length * sizeof(jint));
  rbuf = (unsigned char *) malloc((size_t) length * sizeof(unsigned char));

  if (rbuf != NULL && zbuf != NULL)
   {
    memset(rbuf, 0, (size_t) length * sizeof(unsigned char));
    ret = IowKitRead(JLongToHandle(handle), (ULONG) numpipe, (PCHAR) rbuf, (ULONG) length);

    for(i = 0; i < ret; i++)
      zbuf[i] = (jint) rbuf[i];

    jba = (*env)->NewIntArray(env, (jsize) ret);
    (*env)->SetIntArrayRegion(env, jba, 0, (jsize) ret, zbuf);
   }
  else
    jba = (*env)->NewIntArray(env, 0);
  if (zbuf != NULL)
    free(zbuf);
  if (zbuf != NULL)
    free(rbuf);

  return jba;
}

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    readNonBlocking
 * Signature: (JJJ)[I
 */
JNIEXPORT jintArray JNICALL Java_com_codemercs_iow_IowKit_readNonBlocking
  (JNIEnv *env, jclass cl, jlong handle, jlong numpipe, jlong length)
 {
  ULONG          i;
  ULONG          ret;
  jint          *zbuf;
  unsigned char *rbuf;
  jintArray      jba;

  zbuf = (jint *) malloc((size_t) length * sizeof(jint));
  rbuf = (unsigned char *) malloc((size_t) length * sizeof(unsigned char));

  if (rbuf != NULL && zbuf != NULL)
   {
    memset(rbuf, 0, (size_t) length * sizeof(unsigned char));
    ret = IowKitReadNonBlocking(JLongToHandle(handle), (ULONG) numpipe, (PCHAR) rbuf, (ULONG) length);

    for(i = 0; i < ret; i++)
      zbuf[i] = (jint) rbuf[i];

    jba = (*env)->NewIntArray(env, (jsize) ret);
    if (ret > 0)
      (*env)->SetIntArrayRegion(env, jba, 0, (jsize) ret, zbuf);
   }
  else
    jba = (*env)->NewIntArray(env, 0);
  if (zbuf != NULL)
    free(zbuf);
  if (zbuf != NULL)
    free(rbuf);

  return jba;
}

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    readImmediate
 * Signature: (J)[I
 */
JNIEXPORT jintArray JNICALL Java_com_codemercs_iow_IowKit_readImmediate
  (JNIEnv *env, jclass cl, jlong handle)
 {
  int       i;
  DWORD     read;
  jint      zbuf[4] = {0, 0, 0, 0};
  jintArray jba;

  if(IowKitReadImmediate(JLongToHandle(handle), &read) != 0)
   {
    for (i = 0; i < 4; i++)
     {
      zbuf[i] = (jint) (read & 0x000000ff);
      read >>= 8;
     }
    jba = (*env)->NewIntArray(env, 4);
    (*env)->SetIntArrayRegion(env, jba, 0, 4, zbuf);
   }
  else
    jba = (*env)->NewIntArray(env, 0);

  return jba;
}

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    getNumDevs
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_codemercs_iow_IowKit_getNumDevs
  (JNIEnv *env, jclass cl)
 {
  return (jlong) IowKitGetNumDevs();
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    getDeviceHandle
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_codemercs_iow_IowKit_getDeviceHandle
  (JNIEnv *env, jclass cl, jlong num)
 {
  return HandleToJLong(IowKitGetDeviceHandle((ULONG) num));
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    getProductId
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_codemercs_iow_IowKit_getProductId
  (JNIEnv *env, jclass cl, jlong handle)
 {
  return (jlong) IowKitGetProductId(JLongToHandle(handle));
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    getRevision
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_codemercs_iow_IowKit_getRevision
  (JNIEnv *env, jclass cl, jlong handle)
 {
  return (jlong) IowKitGetRevision(JLongToHandle(handle));
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    getSerialNumber
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_codemercs_iow_IowKit_getSerialNumber
  (JNIEnv *env, jclass cl, jlong handle)
 {
  int            i;
  BOOL           ret;
  unsigned short zbuf[9];

  ret = IowKitGetSerialNumber(JLongToHandle(handle), (PWCHAR) zbuf);
  if (ret == FALSE)
   {
    for(i = 0; i < 8; i++)
      zbuf[i] = '0';
    zbuf[8] = 0;
   }
  return (*env)->NewString(env, zbuf, 8);
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    setTimeout
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_codemercs_iow_IowKit_setTimeout
  (JNIEnv *env, jclass cl, jlong handle, jlong timeout)
 {
  return (jboolean) IowKitSetTimeout(JLongToHandle(handle), (ULONG) timeout);
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    setWriteTimeout
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_codemercs_iow_IowKit_setWriteTimeout
  (JNIEnv *env, jclass cl, jlong handle, jlong timeout)
 {
  return (jboolean) IowKitSetWriteTimeout(JLongToHandle(handle), (ULONG) timeout);
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    cancleIo
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_codemercs_iow_IowKit_cancelIo
  (JNIEnv *env, jclass cl, jlong handle, jlong numpipe)
 {
  return (jboolean) IowKitCancelIo(JLongToHandle(handle), (ULONG) numpipe);
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    version
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_codemercs_iow_IowKit_version
  (JNIEnv *env, jclass cl)
 {
  return (*env)->NewStringUTF(env, IowKitVersion());
 }

/*
 * Class:     com_codemercs_iow_IowKit
 * Method:    getThreadHandle
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_codemercs_iow_IowKit_getThreadHandle
  (JNIEnv *env, jclass cl, jlong handle)
 {
  return (jlong) IowKitGetThreadHandle(JLongToHandle(handle));
 }

