package org.kucro3.ref;

public class RefByte extends Ref {
	public RefByte()
	{
		super(Ref.REF_BYTE);
	}
	
	public RefByte(byte value)
	{
		this();
		this.ref = value;
	}
	
	@Override
	public Class<?> getType()
	{
		return byte.class;
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = (Byte)obj;
	}
	
	public byte ref;
}
