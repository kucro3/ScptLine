package org.kucro3.ref;

public class RefLong extends Ref {
	public RefLong()
	{
		super(Ref.REF_LONG);
	}

	public RefLong(long value)
	{
		this();
		this.ref = value;
	}
	
	@Override
	public Class<?> getType()
	{
		return long.class;
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = (Long)obj;
	}
	
	public long ref;
}
