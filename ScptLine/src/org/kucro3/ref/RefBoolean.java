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
	
	public boolean ref;
}
