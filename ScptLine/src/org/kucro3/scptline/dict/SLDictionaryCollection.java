package org.kucro3.scptline.dict;

import java.util.*;

import org.kucro3.lambda.*;
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
	
	public final SLFieldLoaded requireField(String dict, String name)
	{
		return require(qField, dict, name,
				(field) -> SLDictionaryCollectionException.newNoSuchField(env, name),
				(objd) -> objd.requireField(name),
				() -> SLDictionaryCollectionException.newNoSuchFieldInDict(env, dict, name));
	}
	
	public final SLMethodLoaded requireMethod(String dict, String name)
	{
		return require(qMethod, dict, name,
				(insn) -> SLDictionaryCollectionException.newUnknownInsn(env, name),
				(objd) -> objd.requireMethod(name),
				() -> SLDictionaryCollectionException.newUnknownInsnInDict(env, dict, name));
	}
	
	@SuppressWarnings("unchecked")
	private <T> T require(Map<String, Object> qindex, String dict, String name,
			LambdaObjectSP<SLException, String> exceptionQIFailed,
			LambdaObjectSP<T, SLDictionaryLoaded> rtGetter,
			LambdaObject<SLException> exceptionNotFoundInDict)
	{
		SLDictionaryLoaded objDict;
		Object rt = qindex.get(name);
		if(rt == null || rt == DUPLICATED)
			if(dict == null)
				throw exceptionQIFailed.function(name);
			else if((objDict = dicts.get(dict)) == null)
				throw SLDictionaryCollectionException.newNoSuchDictionary(env, dict);
			else if((rt = rtGetter.function(objDict)) == null)
				throw exceptionNotFoundInDict.function();
			else;
		else;
		return (T)rt;
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
		
		public static SLDictionaryCollectionException newNoSuchDictionary(SLEnvironment env,
				String dict)
		{
			return new SLDictionaryCollectionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_NO_SUCH_DICTIONARY,
					String.format(MESSAGE_NO_SUCH_DICTIONARY, dict));
		}
		
		public static SLDictionaryCollectionException newUnknownInsn(SLEnvironment env,
				String name)
		{
			return new SLDictionaryCollectionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_UNKNOWN_INSN,
					String.format(MESSAGE_UNKNOWN_INSN, name));
		}
		
		public static SLDictionaryCollectionException newNoSuchField(SLEnvironment env,
				String name)
		{
			return new SLDictionaryCollectionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_NO_SUCH_FIELD,
					String.format(MESSAGE_NO_SUCH_FIELD, name));
		}
		
		public static SLDictionaryCollectionException newNoSuchFieldInDict(SLEnvironment env,
				String dict, String name)
		{
			return new SLDictionaryCollectionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_NO_SUCH_FIELD_IN_DICT,
					String.format(MESSAGE_NO_SUCH_FIELD_IN_DICT, name, dict));
		}
		
		public static SLDictionaryCollectionException newUnknownInsnInDict(SLEnvironment env,
				String dict, String name)
		{
			return new SLDictionaryCollectionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_UNKNOWN_INSN_IN_DICT,
					String.format(MESSAGE_UNKNOWN_INSN_IN_DICT, name, dict));
		}
		
		public static final String MESSAGE_UNKNOWN_INSN = "Unknown instruction: %s";
		
		public static final String MESSAGE_UNKNOWN_INSN_IN_DICT = "No such insn (%s) in dictionary: %s";
		
		public static final String MESSAGE_NO_SUCH_FIELD = "No such field: %s";
		
		public static final String MESSAGE_NO_SUCH_FIELD_IN_DICT = "No such field (%s) in dicitonary: %s";
		
		public static final String MESSAGE_NO_SUCH_DICTIONARY = "No such dictionary: %s";
		
		public static final String DESCRIPTION = "An exception occurred in dictionary namespace";
	}
}
