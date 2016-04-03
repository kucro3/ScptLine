package org.kucro3.ref;

public class RefChar extends Ref {
	public RefChar()
	{
		super(Ref.REF_CHAR);
	}
	
	public RefChar(char value)
	{
		this();
		this.ref = value;
	}
	
	@Override
	public Class<?> getType()
	{
		return char.class;
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = (Character)obj;
	}
	
	public char ref;
}
