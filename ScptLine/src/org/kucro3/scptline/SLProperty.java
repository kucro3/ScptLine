package org.kucro3.scptline;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.kucro3.ini.*;

public class SLProperty implements SLObject {
	public SLProperty(SLEnvironment env)
	{
		this.env = env;
	}
	
	public SLProperty(SLEnvironment env, Map<String, String> properties)
	{
		this(env);
		this.properties.putAll(properties);
	}
	
	public SLProperty(SLEnvironment env, IniProfile ini)
	{
		this(env);
		IniSection section;
		section = ini.getSection(SECTION_OPTIONS);
		if(section != null)
			properties.putAll(section.getProperties());
	}
	
	public boolean contains(String key)
	{
		return properties.containsKey(key);
	}
	
	public String getString(String key)
	{
		return properties.get(key);
	}
	
	public String getNonnull(String key)
	{
		String result = getString(key);
		if(result == null)
			throw SLPropertyException.newKeyNotFound(env, key);
		return result;
	}
	
	public boolean getBoolean(String key)
	{
		return parser.parseBoolean(getNonnull(key));
	}
	
	public byte getByte(String key)
	{
		return parser.parseByte(getNonnull(key));
	}
	
	public short getShort(String key)
	{
		return parser.parseShort(getNonnull(key));
	}
	
	public int getInt(String key)
	{
		return parser.parseInt(getNonnull(key));
	}
	
	public float getFloat(String key)
	{
		return parser.parseFloat(getNonnull(key));
	}
	
	public long getLong(String key)
	{
		return parser.parseLong(getNonnull(key));
	}
	
	public double getDouble(String key)
	{
		return parser.parseDouble(getNonnull(key));
	}
	
	public String put(String key, Object obj)
	{
		return properties.put(key, obj.toString());
	}
	
	public void save(File file) throws IOException
	{
		IniProfile profile = new IniProfile();
		profile.putSection(SECTION_OPTIONS, new IniSection(SECTION_OPTIONS, properties));
		profile.save(file);
	}
	
	public int getHandlerStackSize()
	{
		return getInt(PROP_ENV_HANDLER_STACK_SIZE);
	}
	
	@Override
	public final SLEnvironment getEnv()
	{
		return env;
	}
	
	private final SLEnvironment env;
	
	public static final String SECTION_OPTIONS = "Options";
	
	public static final String PROP_ENV_HANDLER_STACK_SIZE = "slenv.opstack_size";
	
	private final Map<String, String> properties = new HashMap<>();
	
	private final WrappedParser parser = new WrappedParser();
	
	public static class SLPropertyException extends SLException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8572410895920768642L;
		
		public SLPropertyException(SLEnvironment environment, SLExceptionLevel level)
		{
			super(environment, level, DESCRIPTION);
		}
		
		public SLPropertyException(SLEnvironment environment, SLExceptionLevel level, 
				String message)
		{
			super(environment, level, DESCRIPTION, message);
		}
		
		public SLPropertyException(SLEnvironment environment, SLExceptionLevel level,
				String stub, String message)
		{
			super(environment, level, DESCRIPTION, stub, message);
		}
		
		public static SLPropertyException newKeyNotFound(SLEnvironment env, String key)
		{
			return new SLPropertyException(env, SLExceptionLevel.STOP,
					MESSAGE_KEY_NOT_FOUND,
					String.format(MESSAGE_KEY_NOT_FOUND, key));
		}
		
		public static SLPropertyException newInvalidValue(SLEnvironment env, String key)
		{
			return new SLPropertyException(env, SLExceptionLevel.STOP,
					MESSAGE_INVALID_VALUE,
					String.format(MESSAGE_INVALID_VALUE, key));
		}
		
		public static final String MESSAGE_KEY_NOT_FOUND = "Key not found: %s";
		
		public static final String MESSAGE_INVALID_VALUE = "Invalid value: %s";
		
		public static final String DESCRIPTION = "A exception caused by property file";
	}
	
	class WrappedParser extends SLAbstractParser
	{
		WrappedParser()
		{
			super(env);
		}
		
		@Override
		public SLException newInvalidValueException(String s)
		{
			return SLPropertyException.newInvalidValue(env, s);
		}
	}
}
