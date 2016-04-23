package org.kucro3.scptline;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;

import org.kucro3.ini.*;
import org.kucro3.ref.Ref;
import org.kucro3.scptline.anno.SLExport;
import org.kucro3.scptline.dict.SLDictionary;
import org.kucro3.scptline.dict.SLDictionaryCollection;
import org.kucro3.scptline.dict.SLDictionaryFactory;
import org.kucro3.scptline.dict.SLDictionaryLoaded;
import org.kucro3.scptline.dict.SLDictionaryLoader;
import org.kucro3.scptline.dict.SLMain;
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
		this.intpointEnabled = property.getOrDefault(property::getBoolean,
				SLProperty.PROP_ENV_INTERRUPT_POINT_ENABLED, false);
		this.inlineDictEnabled = property.getOrDefault(property::getBoolean, 
				SLProperty.PROP_ENV_INLINE_DICT_ENABLED, true);
		this.initHandlers();
		this.initVariable();
		this.initInlineDict();
		_idle();
	}
	
	public final boolean isBooting()
	{
		return state == SLEnvState.BOOTING;
	}
	
	public final boolean isLoadingInline()
	{
		return (state == SLEnvState.LOADING) && (lastState == SLEnvState.BOOTING);
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
		e.printStackTrace();
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
	
	public SLDictionaryLoaded load(File file)
	{
		return load0(SLDictionaryFactory::load, file);
	}
	
	public SLDictionaryLoaded load(URL url)
	{
		return load0(SLDictionaryFactory::load, url);
	}
	
	public SLDictionaryLoaded load(IniProfile ini)
	{
		return load0(SLDictionaryFactory::load, ini);
	}
	
	public SLDictionaryLoaded load(Class<?> clz)
	{
		IniProfile profile = new IniProfile();
		IniSection section = profile.createSection(SLDictionaryLoader.SECTION_MAIN);
		section.setValue(SLDictionaryLoader.KEY_MAIN_CLASS_IN_SECTION_MAIN,
				clz.getTypeName());
		SLDictionaryLoaded loaded = load0(SLDictionaryFactory::load, profile);
		if(loaded == null)
			return null;
		SLDictionaryFactory.bind(collection, loaded);
		return loaded;
	}
	
	public void execute(String... lines)
	{
		for(int i = 0; i < lines.length; i++)
			execute(lines[i], i + 1);
	}
	
	public boolean execute(String line)
	{
		return execute(line, 1);
	}
	
	public boolean execute(String line, int linenumber)
	{
		try {
			try {
				_executing();
				String[] lines = opstack.preprocess(line);
				if(lines == null)
					return false;
				boolean r = opstack.process(lines);
				_intpoint();
				if(this.intpointEnabled)
					opstack.intpoint();
				return r;
			} catch (SLException e) {
				this.exception(e);
			} catch (Exception e) {
				this.uncaughtException(e);
			}
		} finally {
			_idle();
		}
		return false;
	}
	
	public final void exception(SLException e)
	{
		try {
			_exception();
			SLExceptionHandler handler;
			if(exceptionHandlers == null
					|| (handler = exceptionHandlers.get(getLastState())) == null)
				uncaughtException(e);
			else if(!handler.handle(this, e))
				uncaughtException(e);
		} catch (Exception e0) {
			uncaughtException(e0);
		} finally {
			__state_last();
		}
	}
	
	final void uncaughtException(Exception e)
	{
		// TODO handle all the uncaught exceptions
		e.printStackTrace();
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
		this.definitions.define("env", this).regConst("env");
		this.definitions.defineVirtual("ret", reg::ret);
		this.definitions.defineVirtual("env_state", this::getState);
		this.definitions.defineVirtual("env_laststate", this::getLastState);
		this.definitions.defineVirtual("current_proc", opstack::peek);
		this.definitions.defineVirtual("systime_ms", System::currentTimeMillis);
		this.definitions.defineVirtual("systime_ns", System::nanoTime);
	}
	
	private final void initInlineDict()
	{
		if(this.inlineDictEnabled)
			this.load(SLInlineDict.class);
	}
	
	private final <T> SLDictionaryLoaded load0(LambdaLoading<T> lambda, T v)
	{
		SLEnvState last = state;
		try {
			try {
				_loading();
				return lambda.function(this, v);
			} catch (SLException e) {
				this.exception(e);
			} catch (Exception e) {
				this.uncaughtException(e);
			}
		} finally {
			__state(last);
		}
		return null;
	}
	
	final Map<SLEnvState, SLExceptionHandler> exceptionHandlers = new HashMap<>();
	
	private final SLDefinitionMap definitions;
	
	private final SLDictionaryCollection collection;
	
	private final SLHandlerStack opstack;
	
	private final SLProperty property;
	
	private final SLRegisterRestricted reg;
	
	volatile SLEnvState lastState;
	
	volatile SLEnvState state;
	
	private final boolean intpointEnabled;
	
	private final boolean inlineDictEnabled;
	
	public static enum SLEnvState
	{
		BOOTING,
		LOADING,
		IDLE,
		EXECUTING,
		INTPOINT_CALLBACK,
		FAILURE_CALLBACK;
	}
	
	interface LambdaLoading<T>
	{
		public abstract SLDictionaryLoaded function(SLEnvironment env, T t);
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
	
	@SLExport(name = "")
	public static class SLInlineDict implements SLMain, SLDictionary
	{
		@Override
		public SLDictionary onLoad(SLEnvironment env, Object reserved) 
		{
			return this;
		}
		
		@SLExport(name = "var", targs = {"vargs"})
		public void var(SLEnvironment env, String[] args)
		{
			
		}
		
		@SLExport(name = "ifnum", targs = {"refvar"})
		public boolean ifnum(SLEnvironment env, Ref ref)
		{
			return Ref.isNumber(ref);
		}
		
		@SLExport(name = "ifbool", targs = {"refvar"})
		public boolean ifbool(SLEnvironment env, Ref ref)
		{
			return Ref.isBool(ref);
		}
		
		@SLExport(name = "ifobj", targs = {"refvar"})
		public boolean ifobj(SLEnvironment env, Ref ref)
		{
			return Ref.isObject(ref);
		}
		
		@SLExport(name = "ifstr", targs = {"refvar"})
		public boolean ifstr(SLEnvironment env, Ref ref)
		{
			return Ref.isObject(ref) && ref.getType().equals(String.class);
		}
		
		@SLExport(name = "ifdef", targs = {"varname"})
		public boolean ifdef(SLEnvironment env, String name)
		{
			return env.getVarMap().defined(name);
		}
	}
}
