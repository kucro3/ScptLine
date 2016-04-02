package org.kucro3.scptline;

public class SLRegisterRestricted extends SLRegister {
	public SLRegisterRestricted(SLEnvironment env)
	{
		super(env);
	}
	
	public boolean getPcBool()
	{
		return _getBool0();
	}
	
	public void setPcBool(boolean b)
	{
		_setBool0(b);
	}
	
	public boolean pcBool()
	{
		return _getAndSetBool0(false);
	}
	
	public Object env()
	{
		return _getObj0();
	}
	
	public void env(Object obj)
	{
		_setObj0(obj);
	}
	
	public Object self()
	{
		return _getObj1();
	}
	
	public Object ret()
	{
		return _getObj3();
	}
}
