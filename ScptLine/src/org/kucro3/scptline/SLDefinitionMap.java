package org.kucro3.scptline;

import java.util.Map;
import java.util.HashMap;

import org.kucro3.scptline.SLEnvironment.SLEnvState;

public class SLDefinitionMap implements SLObject {
	public SLDefinitionMap(SLEnvironment env)
	{
		this.env = env;
		this.map = new HashMap<>();
	}
	
	@Override
	public final SLEnvironment getEnv()
	{
		return env;
	}
	
	final boolean requireCheck()
	{
		return env.getState() != SLEnvState.BOOTING;
	}
	
	static boolean checkName(String name)
	{
		return (
			(name.length() > 0) &&
			(Character.isLetter(name.charAt(0))) &&
			(name.indexOf('$') == -1)
		);
	}
	
	final boolean duplicated(String name)
	{
		return map.containsKey(name);
	}
	
	private final Map<String, Object> map;
	
	private final SLEnvironment env;
}
