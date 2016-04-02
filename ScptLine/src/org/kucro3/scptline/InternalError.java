package org.kucro3.scptline;

public class InternalError extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6969505200120525076L;

	public static void ShouldNotReachHere()
	{
		throw new InternalError("ShouldNotReachHere()");
	}
	
	public static void IntersectedFunctionCall()
	{
		throw new InternalError("IntersectedFunctionCall()");
	}
	
	private InternalError(String message)
	{
		super(message);
		this.setStackTrace(new StackTraceElement[] {this.getStackTrace()[1]});
	}
}
