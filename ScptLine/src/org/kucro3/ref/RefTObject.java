package org.kucro3.ref;

public class RefTObject<T> extends Ref {
	public RefTObject()
	{
		super(Ref.REF_OBJECT);
	}
	
	public RefTObject(T obj)
	{
		this();
		this.ref = obj;
	}
	
	@Override
	public Class<?> getType()
	{
		if(ref == null)
			return null;
		return ref.getClass();
	}
	
	@Override
	public T get()
	{
		return ref;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void set(Object obj)
	{
		this.ref = (T)obj;
	}
	
	public T ref;
}
