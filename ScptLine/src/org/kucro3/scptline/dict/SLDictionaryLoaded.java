package org.kucro3.scptline.dict;

import java.util.*;

import org.kucro3.scptline.*;

public class SLDictionaryLoaded implements SLObject {
	SLDictionaryLoaded(SLEnvironment env, String name, SLDictionary ref,
			SLMain provider, ClassLoader loader)
	{
		this.env = env;
		this.ref = ref;
		this.name = name;
		this.loader = loader;
		this.provider = provider;
	}
	
	@Override
	public final SLEnvironment getEnv()
	{
		return env;
	}
	
	public final SLMain getProvider()
	{
		return provider;
	}
	
	public final SLDictionary getRef()
	{
		return ref;
	}
	
	public final String getName()
	{
		return name;
	}
	
	public Collection<SLFieldLoaded> getFields()
	{
		return fields.values();
	}
	
	public Collection<SLMethodLoaded> getMethods()
	{
		return methods.values();
	}
	
	public SLFieldLoaded getField(String name)
	{
		return fields.get(name);
	}
	
	public SLMethodLoaded getMethod(String name)
	{
		return methods.get(name);
	}
	
	public boolean containsField(String name)
	{
		return fields.containsKey(name);
	}
	
	public boolean containsMethod(String name)
	{
		return methods.containsKey(name);
	}
	
	public SLFieldLoaded requireField(String name)
	{
		SLFieldLoaded field = fields.get(name);
		if(field == null)
			throw SLDictionaryException.newNoSuchField(env, name);
		return field;
	}
	
	public SLMethodLoaded requireMethod(String name)
	{
		SLMethodLoaded method = methods.get(name);
		if(method == null)
			throw SLDictionaryException.newNoSuchMethod(env, name);
		return method;
	}
	
	public ClassLoader getLoader()
	{
		return loader;
	}
	
	final void addField(SLFieldLoaded loaded)
	{
		this.fields.put(loaded.getName(), loaded);
	}
	
	final void addMethod(SLMethodLoaded loaded)
	{
		this.methods.put(loaded.getName(), loaded);
	}
	
	final void addFields(Collection<SLFieldLoaded> loaded)
	{
		for(SLFieldLoaded field : loaded)
			addField(field);
	}
	
	final void addMethods(Collection<SLMethodLoaded> loaded)
	{
		for(SLMethodLoaded method : loaded)
			addMethod(method);
	}
	
	final String name;
	
	final Map<String, SLFieldLoaded> fields = new HashMap<>();
	
	final Map<String, SLMethodLoaded> methods = new HashMap<>();
	
	final SLDictionary ref;
	
	private final SLMain provider;
	
	private final ClassLoader loader;
	
	private final SLEnvironment env;
	
	public static class SLDictionaryException extends SLException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1349243290471799514L;
		
		public SLDictionaryException(SLEnvironment env, SLExceptionLevel level,
				String stub)
		{
			super(env, level, DESCRIPTION, stub);
		}
		
		public SLDictionaryException(SLEnvironment env, SLExceptionLevel level,
				String stub, String message)
		{
			super(env, level, DESCRIPTION, stub, message);
		}
		
		public static SLDictionaryException newNoSuchField(SLEnvironment env,
				String s)
		{
			return new SLDictionaryException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_FIELD_NOT_FOUND,
					String.format(MESSAGE_FIELD_NOT_FOUND, s));
		}
		
		public static SLDictionaryException newNoSuchMethod(SLEnvironment env,
				String s)
		{
			return new SLDictionaryException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_METHOD_NOT_FOUND,
					String.format(MESSAGE_METHOD_NOT_FOUND, s));
		}
		
		public static final String DESCRIPTION = "A exception occurred in dictionary";
	
		public static final String MESSAGE_FIELD_NOT_FOUND = "No such field: %s";
		
		public static final String MESSAGE_METHOD_NOT_FOUND = "No such method: %s";
	}
}
