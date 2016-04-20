package org.kucro3.ref;

import org.kucro3.lambda.LambdaObject;
import org.kucro3.lambda.LambdaVoidSP;

public class RefVirtual<T> extends RefConst {
	public RefVirtual(LambdaObject<T> func, LambdaObject<RuntimeException> lambda)
	{
		this(func, null, lambda);
	}
	
	public RefVirtual(LambdaObject<T> func, LambdaVoidSP<T> setter,
			LambdaObject<RuntimeException> exception)
	{
		super(new RefObject(), exception); // fake
		this.func = func;
		this.setter = setter;
	}
	
	@Override
	public T get()
	{
		return func.function();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void set(Object obj)
	{
		if(setter != null)
			setter.function((T) obj);
		else
			super.set(obj);
	}
	
	@Override
	public Class<?> getType()
	{
		return get().getClass();
	}
	
	private final LambdaVoidSP<T> setter;
	
	private final LambdaObject<T> func;
}
