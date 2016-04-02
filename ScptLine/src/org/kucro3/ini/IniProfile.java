package org.kucro3.ini;

import java.util.*;
import java.io.*;

public class IniProfile {
	public IniProfile()
	{
		this.sections = new LinkedHashMap<>();
	}
	
	public IniProfile(File file) throws IOException
	{
		this();
		this.read(file);
	}
	
	public IniProfile(InputStream is) throws IOException
	{
		this();
		this.read(is);
	}
	
	public boolean containsSection(String name)
	{
		return sections.containsKey(name);
	}
	
	public IniSection putSection(String name, IniSection section)
	{
		return sections.put(name, section);
	}
	
	public IniSection createSection(String name)
	{
		IniSection section;
		sections.put(name, section = new IniSection(name));
		return section;
	}
	
	public IniSection getSection(String name)
	{
		return sections.get(name);
	}
	
	private void read0(Reader r) throws IOException
	{
		BufferedReader reader = null;
		Map<String, IniSection> sec = new LinkedHashMap<>();
		try {
			reader = new BufferedReader(r);
			String line;
			IniSection section = null;
			while((line = reader.readLine()) != null)
				if(line.trim().isEmpty())
					continue;
				else if(line.startsWith(";"))
					continue;
				else if(line.startsWith("[") && line.endsWith("]"))
				{
					String sname = line.substring(1, line.length() - 1);
					if(section != null)
						sec.put(section.getSectionName(), section);
					section = new IniSection(sname);
				}
				else if(line.indexOf('=') != -1)
				{
					String[] property = line.split("=", 2);
					String key = property[0];
					String value = property[1];
					
					if(section == null)
						section = new IniSection(null);
					
					section.setValue(key, value);
				}
				else
					throw new IniFormatException(line);
			if(section != null)
				sec.put(section.getSectionName(), section);
			sections.putAll(sec);
		} finally {
			if(reader != null)
				reader.close();
		}
	}
	
	public final void read(InputStream is) throws IOException
	{
		read0(new InputStreamReader(is));
	}
	
	public final void read(File file) throws IOException
	{
		if(!file.exists())
			return;
		
		if(!file.isFile())
			return;
		
		read0(new FileReader(file));
	}
	
	private static void writeProperties(BufferedWriter writer, IniSection section) throws IOException
	{
		for(Map.Entry<String, String> property : section.getProperties().entrySet())
		{
			String p = new StringBuilder(property.getKey()).append("=").append(property.getValue()).toString();
			writer.write(p);
			writer.newLine();
		}
	}
	
	public final void save(File file) throws IOException
	{
		if(!file.exists() || !file.isFile())
			file.createNewFile();
		
		saveTo(new FileOutputStream(file));
	}
	
	public final void saveTo(OutputStream os) throws IOException
	{
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
			if(sections.containsKey(null))
				writeProperties(writer, sections.get(null));
			
			for(Map.Entry<String, IniSection> section : sections.entrySet())
			{
				if(section.getKey() == null)
					continue;
				
				assert section.getKey().equals(section.getValue().getSectionName());
				
				String secHead = new StringBuilder("[").append(section.getKey()).append("]").toString();
				writer.write(secHead);
				writer.newLine();
				writeProperties(writer, section.getValue());
				writer.newLine();
			}
		}
	}
	
	private final Map<String, IniSection> sections;
}
