package org.kucro3.scptline.dict;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.kucro3.scptline.SLEnvironment;
import org.kucro3.scptline.SLException;
import org.kucro3.scptline.SLObject;
import org.kucro3.scptline.dict.SLDictionaryLoader.SLExportedInfo;

public class SLFieldLoaded extends SLExported implements SLDictionaryObject {
	SLFieldLoaded(SLEnvironment env, SLDictionaryLoaded dict, SLDictionary reference,
			SLExportedInfo metadata, Field field)
	{
		this(env, dict, reference, metadata, new ReflectorDefault(env, field));
	}
	
	SLFieldLoaded(SLEnvironment env, SLDictionaryLoaded dict, SLDictionary reference,
			SLExportedInfo metadata, Method method, boolean envRequired)
	{
		this(env, dict, reference, metadata, new ReflectorDelegate(env, method, envRequired));
	}
	
	SLFieldLoaded(SLEnvironment env, SLDictionaryLoaded dict, SLDictionary reference,
			SLExportedInfo metadata, ReflectorNull reflector)
	{
		super(metadata);
		this.env = env;
		this.dict = dict;
		this.name = metadata.name();
		this.reference = reference;
		this.owner = reference;
		this.reflector = reflector;
	}
	
	@Override
	public final SLEnvironment getEnv() 
	{
		return env;
	}
	
	@Override
	public final SLDictionaryLoaded getDict()
	{
		return dict;
	}
	
	public Field getField()
	{
		return reflector.getField();
	}
	
	public Object getReference()
	{
		return reference;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	public String getRealName()
	{
		return reflector.getName();
	}
	
	public Object get()
	{
		try {
			return reflector.get(reference);
		} catch (IllegalArgumentException e) {
			throw SLFieldException.newIllegalArgument(env, e);
		} catch (IllegalAccessException e) {
			throw SLFieldException.newIllegalAccess(env, e);
		}
	}
		
	public void set(Object obj)
	{
		try {
			reflector.set(reference, obj);
		} catch (IllegalArgumentException e) {
			throw SLFieldException.newIllegalArgument(env, e);
		} catch (IllegalAccessException e) {
			throw SLFieldException.newIllegalAccess(env, e);
		}
	}
	
	public Class<?> getType()
	{
		return getField().getType();
	}
	
	@Override
	public SLDictionary getOwner()
	{
		return owner;
	}
	
	public boolean isDelegate()
	{
		return getField() == null;
	}
	
	final Object reference;
	
	private final String name;
	
	private final SLDictionary owner;
	
	private final SLEnvironment env;
	
	private final SLDictionaryLoaded dict;
	
	private final Reflector reflector;
	
	public static abstract interface Reflector extends SLObject
	{
		abstract Field getField();
		
		abstract String getName();
		
		abstract Object get(Object ref)
			throws IllegalArgumentException, IllegalAccessException;
		
		abstract void set(Object ref, Object obj)
			throws IllegalArgumentException, IllegalAccessException;
	}
	
	public static class ReflectorNull implements Reflector
	{
		public ReflectorNull(SLEnvironment env)
		{
			this.env = env;
		}
		
		public Object get(Object ref)
				throws IllegalArgumentException, IllegalAccessException
		{
			throw SLFieldException.newGetNotSupported(env);
		}
		
		public void set(Object ref, Object obj)
				throws IllegalArgumentException, IllegalAccessException
		{
			throw SLFieldException.newSetNotSupported(env);
		}
		
		public Field getField()
		{
			return null;
		}
		
		public String getName()
		{
			return null;
		}
		
		@Override
		public final SLEnvironment getEnv()
		{
			return env;
		}
		
		private final SLEnvironment env;
	}
	
	public static class ReflectorDefault extends ReflectorNull
	{
		public ReflectorDefault(SLEnvironment env, Field field)
		{
			super(env);
			this.field = field;
		}
		
		@Override
		public void set(Object ref, Object obj)
				throws IllegalArgumentException, IllegalAccessException
		{
			field.set(ref, obj);
		}
		
		@Override
		public Object get(Object ref)
				throws IllegalArgumentException, IllegalAccessException
		{
			return field.get(ref);
		}
		
		@Override
		public Field getField()
		{
			return field;
		}
		
		@Override
		public String getName()
		{
			return field.getName();
		}
		
		private final Field field;
	}
	
	public static class ReflectorDelegate extends ReflectorNull
	{
		public ReflectorDelegate(SLEnvironment env, Method method, boolean envRequired)
		{
			super(env);
			this.method = method;
			this.envRequired = envRequired;
		}
		
		@Override
		public Object get(Object obj)
				throws IllegalAccessException, IllegalArgumentException
		{
			try {
				if(!envRequired)
					return method.invoke(obj);
				return method.invoke(obj, getEnv());
			} catch (InvocationTargetException e) {
				return null; //TODO handle InvocationTargetException
			}
		}
		
		@Override
		public String getName()
		{
			return method.getName();
		}
		
		private final boolean envRequired;
		
		private final Method method;
	}
	
	public static class SLFieldException extends SLException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -4070302767213012485L;
		
		public SLFieldException(SLEnvironment env, SLExceptionLevel level, String stub)
		{
			super(env, level, DESCRIPTION, stub);
		}
		
		public SLFieldException(SLEnvironment env, SLExceptionLevel level, String stub,
				String message)
		{
			super(env, level, DESCRIPTION, stub, message);
		}
		
		public static SLFieldException newIllegalAccess(SLEnvironment env,
				IllegalAccessException e)
		{
			return new SLFieldException(env, SLExceptionLevel.CRASH,
					MESSAGE_ILLEGAL_ACCESS,
					String.format(MESSAGE_ILLEGAL_ACCESS, e.getMessage()));
		}
		
		public static SLFieldException newIllegalArgument(SLEnvironment env,
				IllegalArgumentException e)
		{
			return new SLFieldException(env, SLExceptionLevel.CRASH,
					MESSAGE_ILLEGAL_ARGUMENT,
					String.format(MESSAGE_ILLEGAL_ARGUMENT, e.getMessage()));
		}
		
		public static SLFieldException newGetNotSupported(SLEnvironment env)
		{
			return new SLFieldException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_GET_NOT_SUPPORTED,
					MESSAGE_GET_NOT_SUPPORTED);
		}
		
		public static SLFieldException newSetNotSupported(SLEnvironment env)
		{
			return new SLFieldException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_SET_NOT_SUPPORTED,
					MESSAGE_SET_NOT_SUPPORTED);
		}
		
		public static final String DESCRIPTION = "A exception caused by field in dicitonary";
	
		public static final String MESSAGE_ILLEGAL_ACCESS = "Illegal Access: %s";
		
		public static final String MESSAGE_ILLEGAL_ARGUMENT = "Illegal Argument: %s";
		
		public static final String MESSAGE_GET_NOT_SUPPORTED = "Get Operation not supported";
		
		public static final String MESSAGE_SET_NOT_SUPPORTED = "Set Operation not supported";
	}
}
