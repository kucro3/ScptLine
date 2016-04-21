package org.kucro3.ref;

public class RefNumber extends Ref {
	public RefNumber(Number num)
	{
		super(Ref.REF_NUM);
		if(num == null)
			throw new NullPointerException("NaN");
		this.num = num;
	}
	
	@Override
	public void set(Object obj) 
	{
		this.num = (Number)obj;
	}

	@Override
	public Object get() 
	{
		return num;
	}

	@Override
	public Class<?> getType() 
	{
		return num.getClass();
	}
	
	private Number num;
}
