package org.kucro3.lambda;

public class Lambda {
	private Lambda()
	{
	}
	
	public static boolean lambda(LambdaBoolean lambda)
	{
		return lambda.function();
	}
	
	public static byte lambda(LambdaByte lambda)
	{
		return lambda.function();
	}
	
	public static char lambda(LambdaChar lambda)
	{
		return lambda.function();
	}
	
	public static double lambda(LambdaDouble lambda)
	{
		return lambda.function();
	}
	
	public static int lambda(LambdaInt lambda)
	{
		return lambda.function();
	}
	
	public static long lambda(LambdaLong lambda)
	{
		return lambda.function();
	}
	
	public static short lambda(LambdaShort lambda)
	{
		return lambda.function();
	}
	
	public static <T> T lambda(LambdaObject<T> lambda)
	{
		return lambda.function();
	}
}
