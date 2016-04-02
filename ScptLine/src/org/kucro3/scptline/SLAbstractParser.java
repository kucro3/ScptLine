package org.kucro3.scptline;

public abstract class SLAbstractParser implements SLObject {
	protected SLAbstractParser(SLEnvironment env)
	{
		this.env = env;
	}
	
	public final boolean parseBoolean(String s)
	{
		if(s.equalsIgnoreCase("true"))
			return true;
		else if(s.equalsIgnoreCase("false"))
			return false;
		else
			throw this.newInvalidValueException(s);
	}
	
	public final byte parseByte(String s)
	{
		try {
			return Byte.parseByte(s);
		} catch (NumberFormatException e) {
			throw this.newInvalidValueException(s);
		}
	}
	
	public final short parseShort(String s)
	{
		try {
			return Short.parseShort(s);
		} catch (NumberFormatException e) {
			throw this.newInvalidValueException(s);
		}
	}
	
	public final int parseInt(String s)
	{
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw this.newInvalidValueException(s);
		}
	}
	
	public final float parseFloat(String s)
	{
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			throw this.newInvalidValueException(s);
		}
	}
	
	public final long parseLong(String s)
	{
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			throw this.newInvalidValueException(s);
		}
	}
	
	public final double parseDouble(String s)
	{
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw this.newInvalidValueException(s);
		}
	}
	
	protected abstract SLException newInvalidValueException(String s);
	
	@Override
	public final SLEnvironment getEnv()
	{
		return env;
	}
	
	private final SLEnvironment env;
}
