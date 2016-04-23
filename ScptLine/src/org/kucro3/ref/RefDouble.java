package org.kucro3.ref;

public class RefDouble extends Ref {
	public RefDouble()
	{
		super(Ref.REF_DOUBLE);
	}
	
	public RefDouble(double value)
	{
		this();
		this.ref = value;
	}
	
	@Override
	public Class<?> getType()
	{
		return double.class;
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = ((Number)obj).doubleValue();
	}
	
	public double ref;
}
