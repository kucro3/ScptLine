package org.kucro3.ref;

public class RefFloat extends Ref {
	public RefFloat()
	{
		super(Ref.REF_FLOAT);
	}
	
	public RefFloat(float value)
	{
		this();
		this.ref = value;
	}
	
	@Override
	public Class<?> getType()
	{
		return float.class;
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = ((Number)obj).floatValue();
	}
	
	public float ref;
}
