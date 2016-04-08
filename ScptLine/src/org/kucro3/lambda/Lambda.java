package org.kucro3.lambda;

public class Lambda {
	private Lambda()
	{
	}
	
	public static void lambda(LambdaVoid lambda)
	{
		lambda.function();
	}
	
	public static <P> void lambda(LambdaVoidSP<P> lambda, P p)
	{
		lambda.function(p);
	}
	
	public static void lambda(LambdaVoidMP lambda, Object... args)
	{
		lambda.function(args);
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
	
	public static <R, P> R lambda(LambdaObjectSP<R, P> lambda, P arg)
	{
		return lambda.function(arg);
	}
	
	public static <R> R lambda(LambdaObjectMP<R> lambda, Object... args)
	{
		return lambda.function(args);
	}
}
