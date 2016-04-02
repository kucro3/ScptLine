package org.kucro3.scptline.opstack;

import java.util.Map;
import java.util.HashMap;

public enum SLMethodParam {
	CHAR("char"),
	BYTE("byte"),
	
	N_SHORT("nshort"),
	N_INT("nint"),
	N_LONG("nlong"),
	N_FLOAT("nfloat"),
	N_DOUBLE("ndouble"),
	
	L_OBJECT("lobj"),
	L_STRING("lstr"),
	
	T_OBJECT("tobj");
	
	SLMethodParam(String name)
	{
		this.name = name;
		reg(name, this);
	}
	
	static void reg(String name, SLMethodParam p)
	{
		if(map == null)
			map = new HashMap<>();
		map.put(name, p);
	}
	
	public static SLMethodParam getParam(String t)
	{
		return map.get(t);
	}
	
	public final String getName()
	{
		return name;
	}
	
	private final String name;

	private static Map<String, SLMethodParam> map;
}
