package org.kucro3.ref;

import org.kucro3.lambda.LambdaObjectSP;

public final class Numbers {
	private Numbers()
	{
	}
	
	public static boolean add(Ref a, Ref b, Ref x)
	{
		return operate(a, b, x, OPERATORS_ADD);
	}
	
	public static boolean minus(Ref a, Ref b, Ref x)
	{
		return operate(a, b, x, OPERATORS_MINUS);
	}
	
	public static boolean multiply(Ref a, Ref b, Ref x)
	{
		return operate(a, b ,x, OPERATORS_MULTIPLY);
	}
	
	public static boolean divide(Ref a, Ref b, Ref x)
	{
		return operate(a, b, x, OPERATORS_DIVIDE);
	}
	
	public static boolean mod(Ref a, Ref b, Ref x)
	{
		return operate(a, b, x, OPERATORS_MOD);
	}
	
	private static boolean operate(Ref a, Ref b, Ref x, LambdaObjectSDP[] operators)
	{
		if(a == null || b == null || x == null)
			return false;
		
		try {
			int fp = 0;
			if(Ref.isNumber(a) && Ref.isNumber(b))
			{
				if(Ref.isFPNumber(a) || Ref.isFPNumber(b))
					fp = 1;
				x.set(operators[fp].function(get(a, fp), get(b, fp)));
			}
		} catch (ClassCastException e) {
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private static Number get(Object obj, int fp)
	{
		return ((LambdaObjectSP<Number, Object>)GET_NUM[fp]).function(obj);
	}
	
	private static final LambdaObjectSDP[] 
		OPERATORS_ADD = 
		{
			(a, b) -> (long)a + (long)b,
			(a, b) -> (double)a + (double)b
		},
		
		OPERATORS_MINUS =
		{
			(a, b) -> (long)a - (long)b,
			(a, b) -> (double)a - (double)b
		},
		
		OPERATORS_MULTIPLY = 
		{
			(a, b) -> (long)a * (long)b,
			(a, b) -> (double)a * (double)b
		},
		
		OPERATORS_DIVIDE =
		{
			(a, b) -> (long)a / (long)b,
			(a, b) -> (double)a / (double)b
		},
		
		OPERATORS_MOD =
		{
			(a, b) -> (long)a % (long)b,
			(a, b) -> (double)a % (double)b
		};
	
	private static final LambdaObjectSP<?, ?>[] GET_NUM =
		{
			(obj) -> ((Number)obj).longValue(),
			(obj) -> ((Number)obj).doubleValue()
		};
	
	static interface LambdaObjectSDP
	{
		Object function(Object a, Object b);
	}
	
	public static interface LambdaFunction
	{
		boolean function(Ref a, Ref b, Ref x);
	}
}
