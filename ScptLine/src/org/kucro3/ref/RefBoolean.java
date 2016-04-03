package org.kucro3.ref;

public class RefBoolean extends Ref {
	public RefBoolean()
	{
		super(Ref.REF_BOOLEAN);
	}
	
	public RefBoolean(boolean value)
	{
		this();
		this.ref = value;
	}
	
	@Override
	public Class<?> getType()
	{
		return boolean.class;
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = (Boolean)obj;
	}
	
	public boolean ref;
}
