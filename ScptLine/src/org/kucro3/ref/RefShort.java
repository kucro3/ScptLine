package org.kucro3.ref;

public class RefShort extends Ref {
	public RefShort()
	{
		super(Ref.REF_SHORT);
	}
	
	public RefShort(short value)
	{
		this();
		this.ref = value;
	}
	
	@Override
	public Class<?> getType()
	{
		return short.class;
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = ((Number)ref).shortValue();
	}
	
	public short ref;
}
