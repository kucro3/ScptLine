package org.kucro3.scptline.opstack;

import java.util.EnumMap;

import org.kucro3.fastinstanceof.InstanceProvider;
import org.kucro3.ref.Ref;
import org.kucro3.scptline.InternalError;
import org.kucro3.scptline.SLAbstractParser;
import org.kucro3.scptline.SLEnvironment;
import org.kucro3.scptline.SLException;
import org.kucro3.scptline.anno.SLLimited;
import org.kucro3.scptline.dict.SLMethodLoaded;
import org.kucro3.scptline.opstack.SLMethodParam.SLResolvedParam;
import org.kucro3.scptline.opstack.SLProcEngine.ParamParsers.*;

@SLLimited // Limited runtime object, caller env sensitive
public class SLProcEngine extends SLHandler {
	public SLProcEngine(SLEnvironment env)
	{
		this.env = env;
	}
	
	protected final void checkCaller(SLEnvironment env)
	{
		if(env != this.env)
			InternalError.IntersectedFunctionCall();
	}
	
	public void invalidExtending(SLEnvironment env, SLHandler extending)
	{
		throw SLHandlerException.newExtendingRefused(env, this, extending);
	}
	
	@Override
	protected final boolean ppInternalException(SLEnvironment env, SLException e)
	{
		checkCaller(env);
		return true;
	}
	
	@Override
	protected final boolean ppIntPoint(SLEnvironment env)
	{
		checkCaller(env);
		return true;
	}
	
	@Override
	protected final boolean ppInvalidExtending(SLEnvironment env, SLHandler extending)
	{
		checkCaller(env);
		return true;
	}
	
	@Override
	protected final boolean ppPreprocess(SLEnvironment env, String line)
	{
		checkCaller(env);
		return true;
	}
	
	@Override
	protected final boolean ppProcess(SLEnvironment env, String[] lines)
	{
		checkCaller(env);
		return true;
	}
	
	@Override
	public String[] preprocess(SLEnvironment env, String line)
	{
		char c;
		String[] result = null;
		String[] fsplit = line.split("", 2);
		if(!Character.isLetter(c = line.charAt(0)))
			return new String[] {
					fsplit[0].substring(1),
					fsplit.length > 1 ? fsplit[1] : "",
					new String(new char[] {c})
				};
		else
			result = new String[] {
					fsplit[0],
					fsplit.length > 1 ? fsplit[1] : ""};
		return result;
	}
	
	@Override
	public boolean process(SLEnvironment env, String[] line)
	{
		if(line.length > 1)
		{
			Prefix prefix;
			char c = line[1].charAt(0);
			if((prefix = Prefix.index(c)) != null)
				switch(prefix.handle(this, line))
				{
				case Prefix.ABORTED:
					return false;
				case Prefix.DELEGATED:
					return true;
				case Prefix.PASSED:
					break;
				default:
					InternalError.ShouldNotReachHere();
				}
			else
				throw SLProcEngineException.newInvalidPrefix(env,
						new String(new char[] {c}));
		}
		String // ->
			insn = line[0],
			arg = line[1];
		String[] // ->
				args = arg.split(" "),
				pInsn = insn.split(":");
		
		String pInsnDict = null, pInsnStr;
		if(pInsn.length == 1)
			pInsnStr = pInsn[0];
		else
		{
			pInsnStr = pInsn[1];
			pInsnDict = pInsn[0];
		}
		SLMethodLoaded mloaded = env.getDictionaries().requireMethod(pInsnDict, pInsnStr);
		SLResolvedParam[] params = mloaded.getResolvedParams();
		
		int current = 0;
		SLResolvedParam param;
		ParamParsers parser;
		Object[] objs = new Object[params.length + 1];
		for(int i = 0; i < params.length; i++)
		{
			param = params[i];
			parser = parsers.get(param.getType());
			clearCell();
			objs[i + 1] = parser.parse(this, current, args, cell);
			current += cell[0];
		}
		objs[0] = env;
		
		mloaded.invoke(objs);
		return true;
	}
	
	final void clearCell()
	{
		clearCell(cell);
	}
	
	static void clearCell(int[] cell)
	{
		cell[0] = -1;
	}
	
	static {
		EnumMap<SLMethodParam, ParamParsers> map = new EnumMap<>(SLMethodParam.class);
		map.put(SLMethodParam.BYTE, new __Byte());
		map.put(SLMethodParam.L_OBJECT, new _LObject());
		map.put(SLMethodParam.L_STRING, new _LString());
		map.put(SLMethodParam.N_DOUBLE, new _NDouble());
		map.put(SLMethodParam.N_FLOAT, new _NFloat());
		map.put(SLMethodParam.N_INT, new _NInt());
		map.put(SLMethodParam.N_LONG, new _NLong());
		map.put(SLMethodParam.N_SHORT, new _NShort());
		map.put(SLMethodParam.T_OBJECT, new _TObject());
		parsers = map;
	}
	
	private static final EnumMap<SLMethodParam, ParamParsers> parsers;
	
	private final SLProcParser nParser = new SLProcParser();
	
	private final int[] cell = new int[1];
	
	private final SLEnvironment env;
	
	public static class SLProcEngineException extends SLException
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2220811159415687972L;
		
		public SLProcEngineException(SLEnvironment env, SLExceptionLevel level,
				String message)
		{
			super(env, level, DESCRIPTION, message);
		}
		
		public SLProcEngineException(SLEnvironment env, SLExceptionLevel level,
				String stub, String message)
		{
			super(env, level, DESCRIPTION, stub, message);
		}
		
		public static SLProcEngineException newIllegalArgument(SLEnvironment env,
				SLMethodParam p, String v)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_ILLEGAL_ARGUMENT,
					String.format(MESSAGE_ILLEGAL_ARGUMENT, p.getName(), v));
		}
		
		public static SLProcEngineException newClassCast(SLEnvironment env,
				Object obj, String dest)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_CLASS_CAST,
					String.format(MESSAGE_CLASS_CAST,
							obj.getClass().getSimpleName(),
							dest));
		}
		
		public static SLProcEngineException newInvalidPrefix(SLEnvironment env,
				String prefix)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_INVALID_PREFIX,
					String.format(MESSAGE_INVALID_PREFIX,
							prefix));
		}
		
		public static final String MESSAGE_INVALID_PREFIX = "Invalid or not prefix: %s";
		
		public static final String MESSAGE_ILLEGAL_ARGUMENT = "Illegal Argument (%s): %s";
		
		public static final String MESSAGE_CLASS_CAST = "%s cannot be cast to %s";
		
		public static final String DESCRIPTION = "An exception occurred in proc engine(handler).";
	}
	
	class SLProcParser extends SLAbstractParser
	{
		SLProcParser()
		{
			super(env);
		}
		
		SLProcParser param(SLMethodParam p)
		{
			this.last = p;
			return this;
		}
		
		@Override
		protected SLException newInvalidValueException(String s)
		{
			return SLProcEngineException.newIllegalArgument(env, last, s);
		}
		
		private SLMethodParam last;
	}
	
	static interface ParamParsers
	{
		default Object parse(SLProcEngine self, int current, String[] line, int[] used,
				String variable)
		{
			return parse(self, current, line, used);
		}
		
		default Object parse(SLProcEngine self, int current, String[] line, int[] used)
		{
			return null;
		}
		
		static class __Byte implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Object obj = self.nParser.param(SLMethodParam.BYTE)
						.parseByte(line[current]);
				used[0] = 1;
				return obj;
			}
		}
		
		static class _LObject implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Ref ref;
				Object obj = null;
				String var = line[current];
				switch(var.charAt(0))
				{
				case '$':
					var = var.substring(1);
					ref = self.env.getVarMap().requireRef(var);
					obj = ref.get();
					break;
					
				default:
					throw SLProcEngineException.newIllegalArgument(self.env,
							override(), var);
				}
				used[0] = 1;
				return obj;
			}
			
			protected SLMethodParam override()
			{
				return SLMethodParam.L_OBJECT;
			}
		}
		
		static class _LString implements ParamParsers
		{
			
		}
		
		static class _TObject extends _LObject implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used,
					String variable)
			{
				Object obj = super.parse(self, current, line, used);
				clearCell(used);
				try {
					if(!InstanceProvider.getDefault().isInstance(obj, variable))
						throw SLProcEngineException.newClassCast(self.env, obj, variable);
					used[0] = 1;
				} catch (InstantiationException | IllegalAccessException e) {
					InternalError.ShouldNotReachHere();
				}
				return obj;
			}
			
			@Override
			protected SLMethodParam override()
			{
				return SLMethodParam.T_OBJECT;
			}
		}
		
		static class _NDouble implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Object obj = self.nParser.param(SLMethodParam.N_DOUBLE)
						.parseDouble(line[current]);
				used[0] = 1;
				return obj;
			}
		}
		
		static class _NFloat implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Object obj = self.nParser.param(SLMethodParam.N_FLOAT)
						.parseFloat(line[current]);
				used[0] = 1;
				return obj;
			}
		}
		
		static class _NInt implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Object obj = self.nParser.param(SLMethodParam.N_INT)
						.parseInt(line[current]);
				used[0] = 1;
				return obj;
			}
		}
		
		static class _NLong implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Object obj = self.nParser.param(SLMethodParam.N_LONG)
						.parseLong(line[current]);
				used[0] = 1;
				return obj;
			}
		}
		
		static class _NShort implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Object obj = self.nParser.param(SLMethodParam.N_SHORT)
						.parseShort(line[current]);
				used[0] = 1;
				return obj;
			}
		}
	}
	
	static abstract class Prefix
	{
		Prefix(char c)
		{
			if(c < 0 || c > 127)
				return;
			prefixes[c] = this;
		}
		
		abstract int handle(SLProcEngine self, String[] preprocessed);
		
		public static class _0x21 /* ! */ extends Prefix
		{
			_0x21()
			{
				super('!');
			}
			
			int handle(SLProcEngine self, String[] preprocessed)
			{
				if(!self.env.getRegister().pcBool())
					return ABORTED;
				return PASSED;
			}
		}
		
		public static Prefix index(char c)
		{
			if(c < 0 || c > 127)
				return null;
			return prefixes[c];
		}
		
		private static final Prefix[] prefixes = new Prefix[128];
		
		public static final int ABORTED = 0;
		
		public static final int DELEGATED = 2;
		
		public static final int PASSED = 1;
	}
}
