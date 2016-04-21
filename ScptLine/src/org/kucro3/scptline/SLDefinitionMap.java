package org.kucro3.scptline;

import java.util.Map;
import java.util.HashMap;

import org.kucro3.lambda.LambdaObject;
import org.kucro3.lambda.LambdaObjectSP;
import org.kucro3.lambda.LambdaVoidSP;
import org.kucro3.ref.*;
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
	
	final <T> void defineVirtual(String name, LambdaObject<T> func)
	{
		this.checkDuplication(name);
		this.map.put(name, new RefVirtual<>(func, () ->
			SLDefinitionException.newConstant(env, name))
		);
	}
	
	final void regConst(String name)
	{
		assert this.defined(name);
		
		Ref ref = this.getRef(name);
		RefConst constant = new RefConst(ref, () ->
			SLDefinitionException.newConstant(env, name)
		);
		this.map.put(name, constant);
	}
	
	public SLDefinitionMap define(String name, Object obj)
	{
		if(requireCheck())
			this.checkName(name);
		this.checkDuplication(name);
		map.put(name, new RefObject(obj));
		return this;
	}
	
	public Ref getRef(String name)
	{
		return map.get(name);
	}
	
	public Object get(String name)
	{
		return get0(name);
	}
	
	final Object get0(String name)
	{
		Ref ref;
		if((ref = map.get(name)) == null)
			return null;
		return ref.get();
	}
	
	static Ref wrapObject(Object obj)
	{
		return new RefObject(obj);
	}
	
	public boolean set(String name, Object obj, LambdaObjectSP<Ref, Object> func)
	{
		if(map.get(name) == null)
			return false;
		map.put(name, func.function(obj));
		return true;
	}
	
	public <T extends Ref> void operate(String name, LambdaObjectSP<T, Ref> func,
			LambdaObjectSP<Boolean, Ref> type)
	{
		Ref ref = operate0(name, type);
		if(func.function(ref) != ref)
			map.put(name, ref);
	}
	
	public void operate(String name, LambdaVoidSP<Ref> func,
			LambdaObjectSP<Boolean, Ref> type)
	{
		func.function(operate0(name, type));
	}
	
	private final Ref operate0(String name, LambdaObjectSP<Boolean, Ref> type)
	{
		Ref ref;
		if((ref = map.get(name)) == null)
			throw SLDefinitionException.newVarUndefined(env, name);
		if(!type.function(ref))
			throw SLDefinitionException.newInvalidOperation(env, ref.getRefType());
		return ref;
	}
	
	static Ref wrapNumber(Object obj)
	{
		Number number = (Number) obj;
		if(number == null)
			number = (int)0;
		return new RefNumber(number);
	}
	
	static Ref wrapBool(Object obj)
	{
		Boolean b = (Boolean) obj;
		if(b == null)
			b = false;
		return new RefBoolean(b);
	}
	
	public void putNumber(String name, Number obj)
	{
		if(!set(name, obj, SLDefinitionMap::wrapNumber))
			throw SLDefinitionException.newVarUndefined(env, name);
	}
	
	public void putBool(String name, Boolean obj)
	{
		if(!set(name, obj, SLDefinitionMap::wrapBool))
			throw SLDefinitionException.newVarUndefined(env, name);
	}
	
	public void put(String name, Object obj)
	{
		if(!set(name, obj, SLDefinitionMap::wrapObject))
			throw SLDefinitionException.newVarUndefined(env, name);
	}
	
	public Object require(String name)
	{
		Object obj;
		if((obj = get0(name)) == null)
			throw SLDefinitionException.newVarUndefined(env, name);
		return obj;
	}
	
	public Ref requireRef(String name)
	{
		Ref ref;
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
	
	private final Map<String, Ref> map;
	
	private final SLEnvironment env;
	
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
		
		public static SLDefinitionException newConstant(SLEnvironment env, String name)
		{
			throw new SLDefinitionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_CONSTANT,
					String.format(MESSAGE_CONSTANT, name));
		}
		
		public static SLDefinitionException newInvalidOperation(SLEnvironment env, int t)
		{
			throw new SLDefinitionException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_INVALID_OPERATION,
					String.format(MESSAGE_INVALID_OPERATION, t));
		}
		
		public static final String MESSAGE_UNDEFINED = "Undefined variable: %s";
		
		public static final String MESSAGE_DEFINITION_DUPLICATED = "Variable redefinition: %s";
		
		public static final String MESSAGE_ILLEGAL_NAME = "Illegal variable name: %s";
		
		public static final String MESSAGE_CONSTANT = "Constant variable: %s";
		
		public static final String MESSAGE_INVALID_OPERATION = "Invalid operation on type: %d(%s)";
		
		public static final String DESCRIPTION = "An exception occurred in definition pool.";
	}
}
