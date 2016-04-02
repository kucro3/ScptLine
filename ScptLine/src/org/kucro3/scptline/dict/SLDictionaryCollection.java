package org.kucro3.scptline.dict;

import java.util.*;

import org.kucro3.scptline.SLEnvironment;
import org.kucro3.scptline.SLException;
import org.kucro3.scptline.SLObject;

public class SLDictionaryCollection implements SLObject {
	SLDictionaryCollection(SLEnvironment env)
	{
		this.env = env;
	}
	
	@Override
	public SLEnvironment getEnv() 
	{
		return env;
	}
	
	public boolean contiansDictionary(String name)
	{
		return dicts.containsKey(name);
	}
	
	void removeDictionary(String name)
	{
		dicts.remove(name);
	}
	
	public SLDictionaryLoaded getDictionary(String name)
	{
		return dicts.get(name);
	}
	
	final boolean bind(SLDictionaryLoaded loaded)
	{
		if(dicts.containsKey(loaded.getName()))
			return false;
		String temp;
		
		for(SLFieldLoaded f : loaded.fields.values())
			if(qField.put(temp = f.getName(), f) != null)
				qField.put(temp, DUPLICATED);
		
		for(SLMethodLoaded m : loaded.methods.values())
			if(qMethod.put(temp = m.getName(), m) != null)
				qMethod.put(temp, DUPLICATED);
		
		return true;
	}
	
	public final SLFieldLoaded quickIndexField(String name)
	{
		return quickIndex(qField, name);
	}
	
	public final SLMethodLoaded quickIndexMethod(String name)
	{
		return quickIndex(qMethod, name);
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T quickIndex(Map<String, Object> map, String key)
	{
		Object rt;
		if((rt = map.get(key)) == DUPLICATED)
			return null;
		return (T)rt;
	}
	
	private final Map<String, Object> qField = new HashMap<>();
	
	private final Map<String, Object> qMethod = new HashMap<>();
	
	private final Map<String, SLDictionaryLoaded> dicts = new HashMap<>();
	
	private final SLEnvironment env;
	
	public static final Object DUPLICATED = new Object();
	
	public static class SLDictionaryCollectionException extends SLException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6822170719734111730L;
	
		public SLDictionaryCollectionException(SLEnvironment env, SLExceptionLevel level,
				String stub)
		{
			super(env, level, DESCRIPTION, stub);
		}
		
		public SLDictionaryCollectionException(SLEnvironment env, SLExceptionLevel level,
				String stub, String message)
		{
			super(env, level, DESCRIPTION, stub, message);
		}
		
		
		public static final String DESCRIPTION = "An exception occurred in dictionary namespace";
	}
}
