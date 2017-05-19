package com.realcraft.sockets;

import java.io.Serializable;
import java.util.HashMap;

public class SocketData implements Serializable {

	private static final long serialVersionUID = 1;
	private String channel;
	private final HashMap<String,SocketDataObject> objects = new HashMap<String,SocketDataObject>();

	public SocketData(String channel){
		this.channel = channel;
	}

	public String getChannel(){
		return channel;
	}

	public void setByte(String key,String data){
		objects.put(key,new SocketDataObject(SocketDataType.BYTE,data));
	}

	public void setShort(String key,short data){
		objects.put(key,new SocketDataObject(SocketDataType.SHORT,data));
	}

	public void setInt(String key,int data){
		objects.put(key,new SocketDataObject(SocketDataType.INT,data));
	}

	public void setLong(String key,long data){
		objects.put(key,new SocketDataObject(SocketDataType.LONG,data));
	}

	public void setFloat(String key,float data){
		objects.put(key,new SocketDataObject(SocketDataType.FLOAT,data));
	}

	public void setDouble(String key,double data){
		objects.put(key,new SocketDataObject(SocketDataType.DOUBLE,data));
	}

	public void setBoolean(String key,boolean data){
		objects.put(key,new SocketDataObject(SocketDataType.BOOLEAN,data));
	}

	public void setString(String key,String data){
		objects.put(key,new SocketDataObject(SocketDataType.STRING,data));
	}

	public void setObject(String key,Object data){
		objects.put(key,new SocketDataObject(SocketDataType.OBJECT,data));
	}

	public byte getByte(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return 0;
		if(object.getType() != SocketDataType.BYTE) return 0;
		return ((byte)object.getData());
	}

	public short getShort(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return 0;
		if(object.getType() != SocketDataType.SHORT) return 0;
		return ((short)object.getData());
	}

	public int getInt(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return 0;
		if(object.getType() != SocketDataType.INT) return 0;
		return ((int)object.getData());
	}

	public long getLong(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return 0;
		if(object.getType() != SocketDataType.LONG) return 0;
		return ((long)object.getData());
	}

	public float getFloat(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return 0;
		if(object.getType() != SocketDataType.FLOAT) return 0;
		return ((float)object.getData());
	}

	public double getDouble(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return 0;
		if(object.getType() != SocketDataType.DOUBLE) return 0;
		return ((double)object.getData());
	}

	public boolean getBoolean(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return false;
		if(object.getType() != SocketDataType.BOOLEAN) return false;
		return ((boolean)object.getData());
	}

	public String getString(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return null;
		if(object.getType() != SocketDataType.STRING) return null;
		return ((String)object.getData());
	}

	public Object getObject(String key){
		SocketDataObject object = objects.get(key);
		if(object == null) return null;
		if(object.getType() != SocketDataType.OBJECT) return null;
		return (object.getData());
	}

	private class SocketDataObject implements Serializable {

		private static final long serialVersionUID = 1;
		private SocketDataType type;
		private Object data;

		private SocketDataObject(SocketDataType type,Object data){
			this.type = type;
			this.data = data;
		}

		private SocketDataType getType(){
			return type;
		}

		private  Object getData(){
			return data;
		}
	}

	private enum SocketDataType implements Serializable {
		BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BOOLEAN, STRING, OBJECT;
	}
}