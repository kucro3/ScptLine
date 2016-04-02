package org.kucro3.scptline.opstack;

import org.kucro3.scptline.*;
import org.kucro3.scptline.InternalError;

public abstract class SLHandler implements SLRuntimeObject, SLExceptionHandler {
	protected SLHandler()
	{
		this(true);
	}
	
	protected SLHandler(boolean extendable)
	{
		this(null, extendable);
	}
	
	protected SLHandler(SLHandler parent)
	{
		this(parent, true);
	}
	
	protected SLHandler(SLHandler parent, boolean extendable)
	{
		this.parent = parent;
		this.extendable = extendable;
	}
	
	final boolean extend(SLEnvironment env, SLHandler parent)
	{
		if(parent == null)
			return false;
		
		if(this.parent != null)
			throw SLHandlerException.newExtensionDuplicated(env);
		
		boolean extended;
		if(extended = checkExtension(env, this))
			this.parent = parent;
		return extended;
	}
	
	final boolean checkExtension(SLEnvironment env, SLHandler parent)
	{
		if(parent != null)
			switch(checked)
			{
			case VIchecked_CHECKED:
				return true;
			case VIchecked_REFUSED:
				return false;
			case VIchecked_UNCHECKED:
				boolean extendable;
				if(!(extendable = parent.extendable))
				{
					checked = VIchecked_REFUSED;
					parent.invalidExtending(env, this);
				}
				else
					checked = VIchecked_CHECKED;
				return extendable;
			default:
				InternalError.ShouldNotReachHere();
			}
		else
			checked = VIchecked_CHECKED;
		return true;
	}
	
	final boolean checkExtended(SLEnvironment env)
	{
		return checkExtension(env, parent);
	}
	
	public final boolean isTop()
	{
		return parent() == null;
	}
	
	public final SLHandler getTop()
	{
		SLHandler temp = this;
		if(top == null)
			while(true)
			{
				if(temp.isTop())
					return this.top = temp;
				temp = temp.parent();
				
				assert temp.parent() != null;
			}
		else
			return top;
	}
	
	final void _intpoint(SLHandlerStack stk)
	{
		SLEnvironment env = stk.getEnv();
		SLHandler handler = this.getTop();
		if(handler.ppIntPoint(env))
			handler.intpoint(env);
	}
	
	final void _internalException(SLHandlerStack stk, SLException e)
	{
		SLEnvironment env = stk.getEnv();
		SLHandler handler = this.getTop();
		if(handler.ppInternalException(env, e))
			handler.internalException(env, e);
	}
	
	final String[] _preprocess(SLHandlerStack stk, String line)
	{
		SLEnvironment env = stk.getEnv();
		SLHandler handler = this.getTop();
		if(handler.ppPreprocess(env, line))
			return handler.preprocess(env, line);
		return null;
	}
	
	final boolean _process(SLHandlerStack stk, String[] lines)
	{
		SLEnvironment env = stk.getEnv();
		SLHandler handler = this.getTop();
		if(handler.ppProcess(env, lines))
			return handler.process(env, lines);
		return false;
	}
	
	public void intpoint(SLEnvironment env)
	{
		if(parent != null)
			parent.intpoint(env);
	}
	
	public void internalException(SLEnvironment env, SLException e)
	{
		if(parent != null)
			parent.internalException(env, e);
	}
	
	public String[] preprocess(SLEnvironment env, String line)
	{
		if(parent != null)
			return parent.preprocess(env, line);
		return null;
	}
	
	public boolean process(SLEnvironment env, String[] line)
	{
		if(parent != null)
			return parent.process(env, line);
		return true;
	}
	
	public void invalidExtending(SLEnvironment env, SLHandler handler)
	{
		if(parent != null)
			parent.invalidExtending(env, handler);
	}
	
	protected boolean ppInternalException(SLEnvironment env, SLException e)
	{
		return true;
	}
	
	protected boolean ppIntPoint(SLEnvironment env)
	{
		return true;
	}
	
	protected boolean ppInvalidExtending(SLEnvironment env, SLHandler handler)
	{
		return true;
	}
	
	protected boolean ppPreprocess(SLEnvironment env, String line)
	{
		return true;
	}
	
	protected boolean ppProcess(SLEnvironment env, String[] line)
	{
		return true;
	}
	
	protected final SLHandler parent()
	{
		return parent;
	}
	
	public final boolean handle(SLEnvironment env, SLException e)
	{
		this._internalException(env.getHandlerStack(), e);
		return true;
	}
	
	private SLHandler parent;
	
	private volatile int checked;
	
	private final boolean extendable;
	
	private static final int VIchecked_UNCHECKED = 0;
	
	private static final int VIchecked_CHECKED = 1;
	
	private static final int VIchecked_REFUSED = -1;
	
	SLHandler top;
	
	public static class SLHandlerException extends SLException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -2257965736611210264L;
		
		public SLHandlerException(SLEnvironment env, SLExceptionLevel level, String stub)
		{
			super(env, level, DESCRIPTION, stub);
		}
		
		public SLHandlerException(SLEnvironment env, SLExceptionLevel level, String stub,
				String message)
		{
			super(env, level, DESCRIPTION, stub, message);
		}
		
		public static SLHandlerException newExtensionDuplicated(SLEnvironment env)
		{
			return new SLHandlerException(env, SLExceptionLevel.STOP,
					MESSAGE_EXTENSION_DUPLICATED,
					MESSAGE_EXTENSION_DUPLICATED);
		}
		
		public static SLHandlerException newExtendingRefused(SLEnvironment env,
				SLHandler ref, SLHandler extending)
		{
			return new SLHandlerException(env, SLExceptionLevel.STOP,
					MESSAGE_EXTENDING_REFUSED,
					String.format(MESSAGE_EXTENDING_REFUSED,
							extending.getClass().getCanonicalName(),
							ref.getClass().getCanonicalName()));
		}
		
		public static final String DESCRIPTION = "An exception occurred in handler";

		public static final String MESSAGE_EXTENSION_DUPLICATED = "Extension duplicated";
		
		public static final String MESSAGE_EXTENDING_REFUSED = "Extending refused: %s extending %s";
	}
}
