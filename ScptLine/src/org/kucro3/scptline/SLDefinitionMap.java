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
	
	public boolean defined(String name)
	{
		return map.containsKey(name);
	}
	
	final boolean requireCheck()
	{
		return env.getState() != SLEnvState.BOOTING;
	}
	
	static boolean checkName(String name)
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
		
		public static final String MESSAGE_UNDEFINED = "Undefined variable: %s";
		
		public static final String MESSAGE_DEFINITION_DUPLICATED = "Variable redefinition: %s";
		
		public static final String DESCRIPTION = "An exception occurred in definition pool.";
	}
	
	private final Map<String, RefObject> map;
	
	private final SLEnvironment env;
}
