package org.kucro3.scptline;

public class SLRegister implements SLObject {
	public SLRegister(SLEnvironment env)
	{
		this.env = env;
	}
	
	@Override
	public final SLEnvironment getEnv() 
	{
		return env;
	}
	
	public final boolean _getBool0()
	{
		return bool0;
	}
	
	public final boolean _getBool1()
	{
		return bool1;
	}
	
	public final boolean _getBool2()
	{
		return bool2;
	}
	
	public final boolean _getBool3()
	{
		return bool3;
	}
	
	public final void _setBool0(boolean b)
	{
		this.bool0 = b;
	}
	
	public final void _setBool1(boolean b)
	{
		this.bool1 = b;
	}
	
	public final void _setBool2(boolean b)
	{
		this.bool2 = b;
	}
	
	public final void _setBool3(boolean b)
	{
		this.bool3 = b;
	}
	
	public final boolean _getAndSetBool0(boolean b)
	{
		boolean r = this.bool0;
		this.bool0 = b;
		return r;
	}
	
	public final boolean _getAndSetBool1(boolean b)
	{
		boolean r = this.bool1;
		this.bool1 = b;
		return r;
	}
	
	public final boolean _getAndSetBool2(boolean b)
	{
		boolean r = this.bool2;
		this.bool2 = b;
		return r;
	}
	
	public final boolean _getAndSetBool3(boolean b)
	{
		boolean r = this.bool3;
		this.bool3 = b;
		return r;
	}
	
	public final Object _getObj0()
	{
		return obj0;
	}
	
	public final Object _getObj1()
	{
		return obj1;
	}
	
	public final Object _getObj2()
	{
		return obj2;
	}
	
	public final Object _getObj3()
	{
		return obj3;
	}
	
	public final void _setObj0(Object obj)
	{
		this.obj0 = obj;
	}
	
	public final void _setObj1(Object obj)
	{
		this.obj1 = obj;
	}
	
	public final void _setObj2(Object obj)
	{
		this.obj2 = obj;
	}
	
	public final void _setObj3(Object obj)
	{
		this.obj3 = obj;
	}
	
	public final Object _getAndSetObj0(Object obj)
	{
		Object r = this.obj0;
		this.obj0 = obj;
		return r;
	}
	
	public final Object _getAndSetObj1(Object obj)
	{
		Object r = this.obj1;
		this.obj1 = obj;
		return r;
	}
	
	public final Object _getAndSetObj2(Object obj)
	{
		Object r = this.obj2;
		this.obj2 = obj;
		return r;
	}
	
	public final Object _getAndSetObj3(Object obj)
	{
		Object r = this.obj3;
		this.obj3 = obj;
		return r;
	}
	
	private /*volatile*/ Object obj0, obj1, obj2, obj3;
	
	private /*volatile*/ boolean bool0, bool1, bool2, bool3;
	
	private final SLEnvironment env;
}
