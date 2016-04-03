package org.kucro3.fastinstanceof;

public abstract class InstanceCompiled {
	protected InstanceCompiled() {}
	
	public abstract boolean isInstance(Object obj);
	
	InstanceCompiled instance(String instance)
	{
		this.classtype = instance;
		return this;
	}
	
	public String getInstance()
	{
		return classtype;
	}
	
	String classtype;
}
