package org.kucro3.ini;

import java.util.*;

public class IniSection {
	public IniSection(String name)
	{
		this(name, new LinkedHashMap<>());
	}
	
	public IniSection(String name, Map<String, String> properties)
	{
		this.name = name;
		this.properties = properties;
	}
	
	public Map<String, String> getProperties()
	{
		return properties;
	}
	
	public String getSectionName()
	{
		return name;
	}
	
	public void setSectionName(String name)
	{
		this.name = name;
	}
	
	public String getValue(String name)
	{
		return properties.get(name);
	}
	
	public boolean containsValue(String name)
	{
		return properties.containsKey(name);
	}
	
	public String setValue(String name, String value)
	{
		return properties.put(name, value);
	}
	
	protected String name;
	
	private final Map<String, String> properties;
}
