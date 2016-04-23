package org.kucro3.ref;

import org.kucro3.lambda.LambdaObject;

public abstract class Ref {
	Ref(int tRef)
	{
		this.tRef = tRef;
	}
	
	public final int getRefType()
	{
		return tRef;
	}
	
	public static boolean isNumber(Ref ref)
	{
		return isNumber(ref.getRefType());
	}
	
	public static boolean isNumber(int tRef)
	{
		return (tRef & REF_NUM) != 0;
	}
	
	public static boolean isBool(Ref ref)
	{
		return isBool(ref.getRefType());
	}
	
	public static boolean isBool(int tRef)
	{
		return tRef == 0;
	}
	
	public static boolean isObject(Ref ref)
	{
		return isObject(ref.getRefType());
	}
	
	public static boolean isObject(int tRef)
	{
		return (tRef & REF_OBJECT) != 0;
	}
	
	public static boolean isFPNumber(int tRef)
	{
		if(isNumber(tRef))
			return tRef > REF_LONG;
		return false;
	}
	
	public static boolean isFPNumber(Ref ref)
	{
		return isFPNumber(ref.getRefType());
	}
	
	@SuppressWarnings("unchecked")
	public static Ref newRef(int tRef)
	{
		if(isObject(tRef))
			return new RefObject();
		if(isNumber(tRef))
			if((tRef &= 0x000000FF) == 0)
				return null;
			else
				return ((LambdaObject<Ref>)NUMCONSTS[tRef]).function();
		if((tRef &= 0x0000000F) <= REF_CHAR)
			return ((LambdaObject<Ref>)NUMCONSTS[tRef]).function();
		return null;
	}
	
	public abstract void set(Object obj);
	
	public abstract Object get();
	
	public abstract Class<?> getType();

	private final int tRef;
	
	public static final int 
		REF_BOOLEAN = 0x00000000,
		REF_BYTE	= 0x00001001,
		REF_CHAR	= 0x00000002,
		REF_SHORT	= 0x00001003,
		REF_INT		= 0x00001004,
		REF_LONG	= 0x00001005,
		REF_FLOAT	= 0x00001006,
		REF_DOUBLE	= 0x00001007,
		REF_NUM		= 0x00001000,
		REF_OBJECT	= 0x10000000;
//		REF_VIRTUAL = 0x10000001;
	
	private static final LambdaObject<?>[] NUMCONSTS = 
		{
			RefBoolean::new,
			RefByte::new,
			RefChar::new,
			RefShort::new,
			RefInt::new,
			RefLong::new,
			RefFloat::new,
			RefDouble::new
		};
}
