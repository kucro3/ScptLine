package org.kucro3.ref;

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
		return (ref.getRefType() & REF_NUM) != 0;
	}
	
	public static boolean isBool(Ref ref)
	{
		return ref.getRefType() == 0;
	}
	
	public static boolean isObject(Ref ref)
	{
		return (ref.getRefType() & REF_OBJECT) != 0;
	}
	
	public abstract void set(Object obj);
	
	public abstract Object get();
	
	public abstract Class<?> getType();

	private final int tRef;
	
	public static final int 
		REF_BOOLEAN = 0x00000000,
		REF_BYTE	= 0x00000001,
		REF_CHAR	= 0x00000002,
		REF_DOUBLE	= 0x00001004,
		REF_FLOAT	= 0x00001008,
		REF_INT		= 0x00001010,
		REF_LONG	= 0x00001020,
		REF_SHORT	= 0x00001040,
		REF_NUM		= 0x00001000,
		REF_OBJECT	= 0x10000000;
}
