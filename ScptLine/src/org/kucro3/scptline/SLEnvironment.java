package org.kucro3.scptline;

import java.util.Map;
import java.util.HashMap;

import org.kucro3.ini.*;
import org.kucro3.scptline.dict.SLDictionaryCollection;
import org.kucro3.scptline.dict.SLDictionaryFactory;
import org.kucro3.scptline.opstack.SLHandler;
import org.kucro3.scptline.opstack.SLHandlerStack;

public class SLEnvironment implements SLExceptionHandler {
	public SLEnvironment(IniProfile ini)
	{
		_booting();
		this.reg = new SLRegisterRestricted(this);
		this.property = new SLProperty(this, ini);
		this.opstack = new SLHandlerStack(this);
		this.collection = SLDictionaryFactory.newCollection(this);
		this.definitions = new SLDefinitionMap(this);
		this.initHandlers();
		this.initVariable();
		_idle();
	}
	
	public final SLDefinitionMap getVarMap()
	{
		return definitions;
	}
	
	public final SLProperty getProperties()
	{
		return property;
	}
	
	public final SLHandlerStack getHandlerStack()
	{
		return opstack;
	}
	
	public final SLDictionaryCollection getDictionaries()
	{
		return collection;
	}
	
	public final SLRegisterRestricted getRegister()
	{
		return reg;
	}
	
	@Override
	public final boolean handle(SLEnvironment env, SLException e)
	{
		if(env != this)
			InternalError.IntersectedFunctionCall();
		//TODO handle uncaught exceptions
		return true;
	}
	
	final void __state(SLEnvState state)
	{
		this.lastState = this.state;
		this.state = state;
	}
	
	final void _booting()
	{
		__state(SLEnvState.BOOTING);
	}
	
	final void _idle()
	{
		__state(SLEnvState.IDLE);
	}
	
	final void __state_last()
	{
		__state(lastState);
	}
	
	final void _executing()
	{
		__state(SLEnvState.EXECUTING);
	}
	
	final void _intpoint()
	{
		__state(SLEnvState.INTPOINT_CALLBACK);
	}
	
	final void _exception()
	{
		__state(SLEnvState.FAILURE_CALLBACK);
	}
	
	final void _loading()
	{
		__state(SLEnvState.LOADING);
	}
	
	public final SLEnvState getState()
	{
		return state;
	}
	
	public final SLEnvState getLastState()
	{
		return lastState;
	}
	
	public final void exception(SLException e)
	{
		_exception();
		try {
			SLExceptionHandler handler;
			if((handler = exceptionHandlers.get(getLastState())) == null)
				uncaughtException(e);
			if(!handler.handle(this, e))
				uncaughtException(e);
		} catch (Exception e0) {
			uncaughtException(e0);
		}
		__state_last();
	}
	
	final void uncaughtException(Exception e)
	{
		// TODO handle all the uncaught exceptions
	}
	
	private final void initHandlers()
	{
		this.exceptionHandlers.put(SLEnvState.IDLE, this);
		this.exceptionHandlers.put(SLEnvState.BOOTING, this);
		this.exceptionHandlers.put(SLEnvState.EXECUTING, new SLOpStackExceptionHandler());
	}
	
	private final void initVariable()
	{
		this.definitions.define("null", null).regConst("null");
	}
	
	final Map<SLEnvState, SLExceptionHandler> exceptionHandlers = new HashMap<>();
	
	private final SLDefinitionMap definitions;
	
	private final SLDictionaryCollection collection;
	
	private final SLHandlerStack opstack;
	
	private final SLProperty property;
	
	private final SLRegisterRestricted reg;
	
	volatile SLEnvState lastState;
	
	volatile SLEnvState state;
	
	public static enum SLEnvState
	{
		BOOTING,
		LOADING,
		IDLE,
		EXECUTING,
		INTPOINT_CALLBACK,
		FAILURE_CALLBACK;
	}
	
	private final class SLOpStackExceptionHandler implements SLExceptionHandler
	{
		private SLOpStackExceptionHandler()
		{
		}
		
		@Override
		public boolean handle(SLEnvironment env, SLException e) 
		{
			SLHandler handler = opstack.peek();
			if(handler == null)
				return false;
			return handler.handle(SLEnvironment.this, e);
		}
	}
}
