package org.kucro3.scptline;

import java.util.Map;
import java.util.HashMap;

import org.kucro3.ref.RefObject;
import org.kucro3.scptline.SLEnvironment.SLEnvState;

public class SLDefinitionMap implements SLObject {
	public SLDefinitionMap(SLEnvironment env)
	{
		this.env = env;
		this.map = new HashMap<>();
	}
	
	@Override
	public final SLEnvironment getEnv()
	{
		return env;
	}
	
	public void define(String name, Object obj)
	{
		if(requireCheck())
			this.checkName(name);
		this.checkDuplication(name);
		map.put(name, new RefObject(wrap(obj)));
	}
	
	public RefObject getRef(String name)
	{
		return map.get(name);
	}
	
	public Object get(String name)
	{
		return unwrap(get0(name));
	}
	
	final Object get0(String name)
	{
		RefObject ref;
		if((ref = map.get(name)) == null)
			return null;
		return ref.ref;
	}
	
	static Object unwrap(Object obj)
	{
		if(obj == NULL)
			return null;
		return obj;
	}
	
	static Object wrap(Object obj)
	{
		if(obj == null)
			return NULL;
		return obj;
	}
	
	public boolean set(String name, Object obj)
	{
		RefObject ref;
		if((ref = map.get(name)) == null)
			return false;
		ref.ref = wrap(obj);
		return true;
	}
	
	public void put(String name, Object obj)
	{
		if(!set(name, obj))
			throw SLDefinitionException.newVarUndefined(env, name);
	}
	
	public Object require(String name)
	{
		Object obj;
		if((obj = get0(name)) == null)
			throw SLDefinitionException.newVarUndefined(env, name);
		return obj;
	}
	
	public RefObject requireRef(String name)
	{
		RefObject ref;
		if((ref = getRef(name)) == null)
			throw SLDefinitionException.newVarUndefined(env, name);
		return ref;
	}
	
	public boolean undefine(String name)
	{
		return map.remove(name) != null;
	}
	
	public boolean defined(String name)
	{
		return map.containsKey(name);
	}
	
	final boolean requireCheck()
	{
		return env.getState() != SLEnvState.BOOTING;
	}
	
	final void checkName(String name)
	{
		if(!checkName0(name))
			throw SLDefinitionException.newIllegalVarName(env, name);
	}
	
	static boolean checkName0(String name)
	{
		return (
			(name.length() > 0) &&
			(Character.isLetter(name.charAt(0))) &&
			(name.indexOf('$') == -1)
		);
	}
	
	final boolean duplicated(String name)
	{
		return map.containsKey(name);
	}
	
	final void checkDuplication(String name)
	{
		if(duplicated(name))
			throw SLDefinitionException.newIllegalVarName(env, name);
	}
	
	public static final Object NULL = new Object();
	
	public static class SLDefinitionException extends SLException
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6506514705626273741L;
		
		public SLDefinitionException(SLEnvironment env, SLExceptionLevel level,
				String stub)
		{
			super(env, level, DESCRIPTION, stub);
		}
		
		public SLDefinitionException(SLEnvironment env, SLExceptionLevel level,
				String stub, String message)
		{
			super(env, level, DESCRIPTION, stub, message);
		}
		
		public static SLDefinitionException newVarDuplicated(SLEnvironment env, String name)
		{
			return new SLDefinitionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_DEFINITION_DUPLICATED,
					String.format(MESSAGE_DEFINITION_DUPLICATED, name));
		}
		
		public static SLDefinitionException newVarUndefined(SLEnvironment env, String name)
		{
			return new SLDefinitionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_UNDEFINED,
					String.format(MESSAGE_UNDEFINED, name));
		}
		
		public static SLDefinitionException newIllegalVarName(SLEnvironment env, String name)
		{
			return new SLDefinitionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_ILLEGAL_NAME,
					String.format(MESSAGE_ILLEGAL_NAME, name));
		}
		
		public static final String MESSAGE_UNDEFINED = "Undefined variable: %s";
		
		public static final String MESSAGE_DEFINITION_DUPLICATED = "Variable redefinition: %s";
		
		public static final String MESSAGE_ILLEGAL_NAME = "Illegal variable name: %s";
		
		public static final String DESCRIPTION = "An exception occurred in definition pool.";
	}
	
	private final Map<String, RefObject> map;
	
	private final SLEnvironment env;
}
