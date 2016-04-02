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
	
	public abstract Class<?> getType();

	private final int tRef;
	
	public static final int 
		REF_BOOLEAN = 0x00000000,
		REF_BYTE	= 0x00000001,
		REF_CHAR	= 0x00000002,
		REF_DOUBLE	= 0x00000004,
		REF_FLOAT	= 0x00000008,
		REF_INT		= 0x00000010,
		REF_LONG	= 0x00000020,
		REF_SHORT	= 0x00000040,
		REF_OBJECT	= 0x10000000;
}
