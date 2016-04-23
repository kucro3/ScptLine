package org.kucro3.scptline.opstack;

import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

import org.kucro3.fastinstanceof.InstanceProvider;
import org.kucro3.lambda.LambdaObject;
import org.kucro3.ref.*;
import org.kucro3.scptline.InternalError;
import org.kucro3.scptline.SLAbstractParser;
import org.kucro3.scptline.SLEnvironment;
import org.kucro3.scptline.SLException;
import org.kucro3.scptline.anno.SLLimited;
import org.kucro3.scptline.dict.SLMethodLoaded;
import org.kucro3.scptline.opstack.SLMethodParam.SLResolvedParam;
import org.kucro3.scptline.opstack.SLProcEngine.ParamParsers.*;

import static org.kucro3.scptline.opstack.SLProcEngine.ParserContainer.*;
import static org.kucro3.scptline.opstack.SLProcEngine.VarTypes.*;

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
	public void internalException(SLEnvironment env, SLException e)
	{
		e.printStackTrace();
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
	public void intpoint(SLEnvironment env)
	{
	}
	
	@Override
	public String[] preprocess(SLEnvironment env, String line)
	{
		char c;
		String[] result = null;
		String[] fsplit = line.split(" ", 2);
		if(!Character.isLetter(c = line.charAt(0)) && c != ':')
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
		if(line.length > 2)
		{
			Prefix prefix;
			char c = line[2].charAt(0);
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
			objs[i + 1] = parser.parse(this, current, args, cell, param.getVariable());
			current += cell[0];
		}
		objs[0] = env;
		
		Object rt = mloaded.invoke(objs);
		if(mloaded.getReturnType() != Void.class)
			if(mloaded.getReturnType() != boolean.class)
				env.getRegister().ret(rt);
			else
				env.getRegister().setPcBool((boolean)rt);
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
		map.put(SLMethodParam.BYTE, _BYTE = new __Byte());
		map.put(SLMethodParam.L_OBJECT, LOBJECT = new _LObject());
		map.put(SLMethodParam.L_STRING, LSTRING = new _LString());
		map.put(SLMethodParam.N_DOUBLE, NDOUBLE = new _NDouble());
		map.put(SLMethodParam.N_FLOAT, NFLOAT = new _NFloat());
		map.put(SLMethodParam.N_INT, NINT = new _NInt());
		map.put(SLMethodParam.N_LONG, NLONG = new _NLong());
		map.put(SLMethodParam.N_SHORT, NSHORT = new _NShort());
		map.put(SLMethodParam.T_OBJECT, TOBJECT = new _TObject());
		map.put(SLMethodParam.V_ARGS, VARGS = new _VArgs());
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
							obj.getClass().getCanonicalName(),
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
		
		public static SLProcEngineException newVarNotNumber(SLEnvironment env,
				String varname)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_VARIABLE_NOT_NUMBER,
					String.format(MESSAGE_VARIABLE_NOT_NUMBER, varname));
		}
		
		public static SLProcEngineException newNotEnoughArguments(SLEnvironment env)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_NOT_ENOUGH_ARGUMENTS,
					MESSAGE_NOT_ENOUGH_ARGUMENTS);
		}
		
		public static SLProcEngineException newSyntaxError(SLEnvironment env)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_SYNTAX_ERROR,
					MESSAGE_SYNTAX_ERROR);
		}
		
		public static SLProcEngineException newUnknownVarType(SLEnvironment env,
				String type)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_UNKNOWN_VAR_TYPE,
					String.format(MESSAGE_UNKNOWN_VAR_TYPE, type));
		}
		
		public static SLProcEngineException newUnknownOperator(SLEnvironment env,
				String operator)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_UNKNOWN_OPERATOR,
					String.format(MESSAGE_UNKNOWN_OPERATOR, operator));
		}
		
		public static SLProcEngineException newVarNotBooleanValue(SLEnvironment env,
				String varname)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_VARIABLE_NOT_BOOLEAN,
					String.format(MESSAGE_VARIABLE_NOT_BOOLEAN, varname));
		}
		
		public static SLProcEngineException newTooManyArguments(SLEnvironment env)
		{
			return new SLProcEngineException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_TOO_MANY_ARGUMENTS,
					MESSAGE_TOO_MANY_ARGUMENTS);
		}
		
		public static final String MESSAGE_UNKNOWN_OPERATOR = "Unknown operator: %s";
		
		public static final String MESSAGE_SYNTAX_ERROR = "Syntax error";
		
		public static final String MESSAGE_UNKNOWN_VAR_TYPE = "Unknown variable type: %s";
		
		public static final String MESSAGE_NOT_ENOUGH_ARGUMENTS = "Not enought arguments.";
		
		public static final String MESSAGE_INVALID_PREFIX = "Invalid or not prefix: %s";
		
		public static final String MESSAGE_ILLEGAL_ARGUMENT = "Illegal Argument (%s): %s";
		
		public static final String MESSAGE_CLASS_CAST = "%s cannot be cast to %s";
		
		public static final String MESSAGE_VARIABLE_NOT_NUMBER = "Variable \"%s\" is not a number";
		
		public static final String MESSAGE_VARIABLE_NOT_BOOLEAN = "Variable \"%s\" is not a boolean value";
		
		public static final String MESSAGE_TOO_MANY_ARGUMENTS = "Too many arguments";
		
		public static final String DESCRIPTION = "An exception occurred in proc engine(handler).";
	}
	
	static class ParserContainer
	{
		static ParamParsers
			_BYTE,
			LOBJECT,
			LSTRING,
			NDOUBLE,
			NFLOAT,
			NINT,
			NLONG,
			NSHORT,
			TOBJECT,
			VARGS,
			RVAR;
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
		
		static class _RVarname implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				String var = line[current];
				if(var.charAt(0) != '$')
					throw SLProcEngineException.newIllegalArgument(self.env, 
							override(), var);
				return var.substring(1);
			}
			
			protected SLMethodParam override()
			{
				return SLMethodParam.R_VARNAME;
			}
		}
		
		static class _RVar extends _RVarname
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				Ref ref = self.env.getVarMap()
						.requireRef((String)super.parse(self, current, line, used));
				used[0] = 1;
				return ref;
			}
			
			@Override
			protected SLMethodParam override()
			{
				return SLMethodParam.R_VAR;
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
					
				case '%':
					//reg
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
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				int count = 1, status;
				String str = line[current];
				try {
					if(!(str = str.trim()).startsWith("\""))
						return str;
					else if(current + 1 == line.length)
						return str;
					else;
					
					int i;
					StringBuilder sb = new StringBuilder(str.substring(1));
					for(status = NORMAL; current + count < line.length; count++)
						for(str = line[current + count], i = 0, sb.append(" ");
								i < str.length();
								i++)
							switch(status)
							{
							case NORMAL:
								char c = str.charAt(i);
								if(str.charAt(i) == '\\')
									status = CONVERTING;
								else if(str.charAt(i) == '"') // end
									return sb.toString();
								else
									sb.append(c);
								break;
								
							case CONVERTING:
								try {
									char p = str.charAt(i);
									switch(p)
									{
									case '\\': sb.append('\\'); break;
									case '"': sb.append('"'); break;
									case 't': sb.append('\t'); break;
									case 'n': sb.append('\n'); break;
									default: sb.append("\\").append(p);
									}
								} finally {
									status = NORMAL;
								}
								break;
								
							default:
								InternalError.ShouldNotReachHere();
							}
					return sb.toString();
				} finally {
					used[0] = count;
				}
			}
			
			static final int CONVERTING = 1;
			
			static final int NORMAL = 0;
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
		
		static class _VArgs implements ParamParsers
		{
			@Override
			public Object parse(SLProcEngine self, int current, String[] line, int[] used)
			{
				String[] str = new String[line.length];
				for(int i = 0; current < line.length; current++, i++)
					str[i] = line[current];
				return str;
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
			
			@Override
			int handle(SLProcEngine self, String[] preprocessed)
			{
				if(!self.env.getRegister().pcBool())
					return ABORTED;
				return PASSED;
			}
		}
		
		public static class _0x63 /* ? */ extends Prefix
		{
			_0x63()
			{
				super('?');
			}
			
			@Override
			int handle(SLProcEngine self, String[] preprocessed)
			{
				if(self.env.getRegister().pcBool())
					return ABORTED;
				return PASSED;
			}
		}
		
		public static class _0x24 /* $ */ extends Prefix
		{
			_0x24()
			{
				super('$');
			}
			
			@Override
			int handle(SLProcEngine self, String[] preprocessed)
			{
				String // ->
					varname = preprocessed[0],
					expression = preprocessed[1];
				String[] exps = expression.split(" ");
				Ref ref = self.env.getVarMap().requireRef(varname);
				if(exps.length == 0)
					throw SLProcEngineException.newNotEnoughArguments(self.env);
				int i = 0;
				
				String type;
				if(Character.isLetter((type = exps[i]).charAt(0)))
				{
					LambdaObject<Ref> constructor = consts.get(exps[i]);
					if(constructor == null)
						throw SLProcEngineException.newUnknownVarType(self.env, type);
					Ref con = constructor.function();
					Object obj = ref.get();
					if(type.equals(T_STRING))
						obj = obj.toString();
					else if(Ref.isNumber(con))
						obj = toNumber(self, ref, varname, type);
					else if(Ref.isBool(con))
						obj = toBool(self, ref, varname);
					else;
					con.set(obj);
					ref = con;
					i++;
				}
				
				if(i < exps.length)
					if(Ref.isNumber(ref))
					{
						
					}
					else if(Ref.isObject(ref))
					{
						
					}
					else if(Ref.isBool(ref))
						ref.set(parseExpressionBool(self, exps, i, ref));
					else
						InternalError.ShouldNotReachHere();
				else;
				self.env.getVarMap().putRef(varname, ref);
				
				return DELEGATED;
			}
			
			private static Object toBool(SLProcEngine self, Ref ref, String varname)
			{
				Object obj = ref.get();
				if(!Ref.isBool(ref))
					if(Ref.isNumber(ref))
						obj = ((Number)obj).doubleValue() != 0;
					else
						switch(obj.toString().toLowerCase())
						{
						case "true": obj = true; break;
						case "false": obj = false; break;
						default:
							throw SLProcEngineException
									.newVarNotBooleanValue(self.env, varname);
						}
				return obj;
			}
			
			private static Object toNumber(SLProcEngine self, Ref ref, String varname, String type)
			{
				Object obj = ref.get();
				ParamParsers numparser;
				if(!Ref.isNumber(ref))
					if((numparser = numparsers.get(type)) != null)
						obj = numparser.parse(self, 0,
								new String[] {obj.toString()}, unused);
					else
						InternalError.ShouldNotReachHere();
				return obj;
			}
			
			public static Object parseExpressionBool(SLProcEngine self, String[] exps, 
					int current, Ref ref)
			{
				String operator;
				if(current < exps.length)
					switch(operator = exps[current])
					{
					case "=":
						String varname;
						if(current + 1 == exps.length)
							throw SLProcEngineException.newNotEnoughArguments(self.env);
						if(current + 2 == exps.length)
						{
							String exp = exps[current + 1];
							if(exp.startsWith("$"))
								ref = self.env.getVarMap().requireRef(varname = exp.substring(1));
							else
							{
								ref = new RefObject(exp);
								varname = new StringBuilder("argument: ").append(exp).toString();
							}
							return toBool(self, ref, varname);
						}
						else
						{
							// unsupported
							throw SLProcEngineException.newTooManyArguments(self.env);
						}
					case "!":
						return !((boolean)ref.get());
					default:
						throw SLProcEngineException.newUnknownOperator(self.env, operator);
					}
				throw SLProcEngineException.newNotEnoughArguments(self.env);
			}
			
			static {
				Map<String, Numbers.LambdaFunction> _numfuncs = new HashMap<>();
				_numfuncs.put("+=", Numbers::add);
				_numfuncs.put("-=", Numbers::minus);
				_numfuncs.put("*=", Numbers::multiply);
				_numfuncs.put("/=", Numbers::divide);
				_numfuncs.put("%=", Numbers::mod);
				numfuncs = _numfuncs;
				
				Map<String, LambdaObject<Ref>> _consts = new HashMap<>();
				_consts.put(T_BOOLEAN, RefBoolean::new);
				_consts.put(T_BYTE, RefByte::new);
				_consts.put(T_CHAR, RefChar::new);
				_consts.put(T_DOUBLE, RefDouble::new);
				_consts.put(T_FLOAT, RefFloat::new);
				_consts.put(T_INT, RefInt::new);
				_consts.put(T_LONG, RefLong::new);
				_consts.put(T_OBJECT, RefObject::new);
				_consts.put(T_SHORT, RefShort::new);
				_consts.put(T_STRING, RefObject::new);
				consts = _consts;
				
				Map<String, ParamParsers> _numparsers = new HashMap<>();
				_numparsers.put(T_BYTE, ParserContainer._BYTE);
				_numparsers.put(T_DOUBLE, ParserContainer.NDOUBLE);
				_numparsers.put(T_FLOAT, ParserContainer.NFLOAT);
				_numparsers.put(T_INT, ParserContainer.NINT);
				_numparsers.put(T_LONG, ParserContainer.NLONG);
				_numparsers.put(T_SHORT, ParserContainer.NSHORT);
				numparsers = _numparsers;
			}
			
			private static final RefInt ONE = new RefInt(1);
			
			private static final int[] unused = new int[1];
			
			private static final Map<String, LambdaObject<Ref>> consts;
			
			private static final Map<String, ParamParsers> numparsers;
			
			private static final Map<String, Numbers.LambdaFunction> numfuncs;
		}
		
		public static Prefix index(char c)
		{
			if(c < 0 || c > 127)
				return null;
			return prefixes[c];
		}
		
		public static void init()
		{
			new _0x21();
			new _0x24();
			new _0x63();
		}
		
		static {
			prefixes = new Prefix[128];
			init();
		}
		
		private static final Prefix[] prefixes;
		
		public static final int ABORTED = 0;
		
		public static final int DELEGATED = 2;
		
		public static final int PASSED = 1;
	}
	
	public static final class VarTypes
	{
		
		static final String T_BOOLEAN = "bool";
		
		static final String T_CHAR = "char";
		
		static final String T_DOUBLE = "double";
		
		static final String T_FLOAT = "float";
		
		static final String T_INT = "int";
		
		static final String T_LONG = "long";
		
		static final String T_SHORT = "short";
		
		static final String T_BYTE = "byte";
		
		static final String T_OBJECT = "obj";
		
		static final String T_STRING = "string";
	}
	
	public static class VariableParser
	{
		public static void resolve(SLProcEngine self, String[] args)
		{
			
		}
		
		public static final String MODIFIER_CONST = "const";
	}
}