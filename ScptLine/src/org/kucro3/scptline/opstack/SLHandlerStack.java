package org.kucro3.scptline.opstack;

import org.kucro3.lambda.*;
import org.kucro3.scptline.SLEnvironment;
import org.kucro3.scptline.SLException;
import org.kucro3.scptline.SLObject;

public class SLHandlerStack implements SLObject {
	public SLHandlerStack(SLEnvironment owner)
	{
		this.owner = owner;
		this.size = owner.getProperties().getHandlerStackSize();
		this.stack = new SLHandler[size];
	}

	public final int size()
	{
		return size;
	}
	
	@Override
	public SLEnvironment getEnv() 
	{
		return owner;
	}
	
	public int depth()
	{
		return pointer;
	}
	
	public void add(SLHandler handler)
	{
		checkOverflow();
		stack[pointer++] = handler;
	}
	
	public void remove()
	{
		checkUnderflow();
		stack[--pointer] = null;
	}
	
	public SLHandler peek()
	{
		checkUnderflow();
		return stack[pointer - 1];
	}
	
	public SLHandler poll()
	{
		checkUnderflow();
		SLHandler handler = stack[--pointer];
		stack[pointer] = null;
		return handler;
	}
	
	private final void checkOverflow()
	{
		if(pointer + 1 == size)
			throw SLHandlerStackException.newStackOverflow(owner);
	}
	
	private final void checkUnderflow()
	{
		if(pointer < 1)
			throw SLHandlerStackException.newStackUnderflow(owner);
	}
	
	public final void internalException(SLException e)
	{
		execute((handler) -> {
			handler._internalException(this, e);
			return (Void)null;
		});
	}
	
	public final void intpoint()
	{
		execute((handler) -> {
			handler._intpoint(this);
			return (Void)null;
		});
	}
	
	public final String[] preprocess(String line)
	{
		return execute((handler) -> handler._preprocess(this, line));
	}
	
	public final boolean process(String[] line)
	{
		return execute((handler) -> handler._process(this, line));
	}
	
	final <R> R execute(LambdaObjectSP<R, SLHandler> lambda)
	{
		SLHandler handler;
		if((handler = peek()) != null)
			return lambda.function(handler);
		throw SLHandlerStackException.newStackUnderflow(owner);
	}
	
	private int pointer = 0;
	
	private final SLHandler[] stack;
	
	private final int size;
	
	private final SLEnvironment owner;
	
	public static class SLHandlerStackException extends SLException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2854181267941856346L;
		
		public SLHandlerStackException(SLEnvironment env, SLExceptionLevel level,
				String stub)
		{
			super(env, level, DESCRIPTION, stub);
		}
		
		public SLHandlerStackException(SLEnvironment env, SLExceptionLevel level, 
				String stub, String message)
		{
			super(env, level, DESCRIPTION, stub, message);
		}
		
		public static SLHandlerStackException newStackOverflow(SLEnvironment env)
		{
			return new SLHandlerStackException(env, SLExceptionLevel.STOP,
					MESSAGE_STACK_OVERFLOW,
					MESSAGE_STACK_OVERFLOW);
		}
		
		public static SLHandlerStackException newStackUnderflow(SLEnvironment env)
		{
			return new SLHandlerStackException(env, SLExceptionLevel.STOP,
					MESSAGE_STACK_UNDERFLOW,
					MESSAGE_STACK_UNDERFLOW);
		}
		
		public static final String MESSAGE_STACK_OVERFLOW = "Handler stack overflow";
		
		public static final String MESSAGE_STACK_UNDERFLOW = "Handler stack underflow";
		
		public static final String DESCRIPTION = "A exception caused by handler stack";
	}
}
