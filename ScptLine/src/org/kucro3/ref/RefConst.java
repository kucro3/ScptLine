package org.kucro3.ref;

import org.kucro3.lambda.LambdaObject;

public class RefConst extends Ref {
	public RefConst(Ref ref, LambdaObject<RuntimeException> lambda)
	{
		super(ref.getRefType());
		this.ref = ref;
		this.lambda = lambda;
	}
	
	@Override
	public void set(Object obj) 
	{
		if(lambda != null)
			throw lambda.function();
	}

	@Override
	public Object get() 
	{
		return ref.get();
	}

	@Override
	public Class<?> getType() 
	{
		return ref.getType();
	}
	
	private final LambdaObject<RuntimeException> lambda;
	
	private final Ref ref;
}
