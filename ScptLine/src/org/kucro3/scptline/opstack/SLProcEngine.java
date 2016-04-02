package org.kucro3.scptline.opstack;

import java.util.EnumMap;

import org.kucro3.scptline.InternalError;
import org.kucro3.scptline.SLAbstractParser;
import org.kucro3.scptline.SLEnvironment;
import org.kucro3.scptline.SLException;
import org.kucro3.scptline.anno.SLLimited;
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
		
		public static final String MESSAGE_ILLEGAL_ARGUMENT = "Illegal Argument (%s): %s";
		
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
		default Object parse(SLProcEngine self, int current, String[] line, int[] used)
		{
			return 0;
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
		}
		
		static class _LString implements ParamParsers
		{
			
		}
		
		static class _TObject extends _LObject implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Object obj = super.parse(self, current, line, used);
				clearCell(used);
				//TODO instanceof
				used[0] = 1;
				return obj;
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
}
