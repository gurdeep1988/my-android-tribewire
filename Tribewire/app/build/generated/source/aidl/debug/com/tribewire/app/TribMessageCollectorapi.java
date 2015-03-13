/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: H:\\Tribewire\\app\\src\\main\\aidl\\com\\tribewire\\app\\TribMessageCollectorapi.aidl
 */
package com.tribewire.app;
public interface TribMessageCollectorapi extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tribewire.app.TribMessageCollectorapi
{
private static final java.lang.String DESCRIPTOR = "com.tribewire.app.TribMessageCollectorapi";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tribewire.app.TribMessageCollectorapi interface,
 * generating a proxy if needed.
 */
public static com.tribewire.app.TribMessageCollectorapi asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tribewire.app.TribMessageCollectorapi))) {
return ((com.tribewire.app.TribMessageCollectorapi)iin);
}
return new com.tribewire.app.TribMessageCollectorapi.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getLatestSearchResult:
{
data.enforceInterface(DESCRIPTOR);
com.tribewire.app.MessageSearchResult _result = this.getLatestSearchResult();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_addListenerTrib:
{
data.enforceInterface(DESCRIPTOR);
com.tribewire.app.MessageCollectorListener _arg0;
_arg0 = com.tribewire.app.MessageCollectorListener.Stub.asInterface(data.readStrongBinder());
this.addListenerTrib(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeListenerTrib:
{
data.enforceInterface(DESCRIPTOR);
com.tribewire.app.MessageCollectorListener _arg0;
_arg0 = com.tribewire.app.MessageCollectorListener.Stub.asInterface(data.readStrongBinder());
this.removeListenerTrib(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tribewire.app.TribMessageCollectorapi
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public com.tribewire.app.MessageSearchResult getLatestSearchResult() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.tribewire.app.MessageSearchResult _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLatestSearchResult, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.tribewire.app.MessageSearchResult.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void addListenerTrib(com.tribewire.app.MessageCollectorListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_addListenerTrib, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeListenerTrib(com.tribewire.app.MessageCollectorListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeListenerTrib, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getLatestSearchResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_addListenerTrib = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_removeListenerTrib = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public com.tribewire.app.MessageSearchResult getLatestSearchResult() throws android.os.RemoteException;
public void addListenerTrib(com.tribewire.app.MessageCollectorListener listener) throws android.os.RemoteException;
public void removeListenerTrib(com.tribewire.app.MessageCollectorListener listener) throws android.os.RemoteException;
}
