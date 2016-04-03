package org.kucro3.util;

import java.lang.reflect.Array;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.ProtectionDomain;
import java.util.*;

public class ClassDef extends ClassLoader {
	private ClassDef()
	{
		super(getProvidedClassLoader());
	}
	
	public static ClassLoader getProvidedClassLoader()
	{
//		return ClassLoader.getSystemClassLoader();
		return ClassDef.class.getClassLoader();
	}
	
	@SuppressWarnings("deprecation")
	public static Class<?> defClass(byte[] byts, int off, int len)
	{
		return classDef.defineClass(byts, off, len);
	}
	
	public static Class<?> defClass(String name, byte[] byts, int off, int len)
	{
		return classDef.defineClass(name, byts, off, len);
	}
	
	public static Class<?> defClass(String name, byte[] byts, int off, int len, ProtectionDomain pD)
	{
		return classDef.defineClass(name, byts, off, len, pD);
	}
	
	public static Class<?> defClass(String name, ByteBuffer b, ProtectionDomain protectionDomain) 
	{
		return classDef.defineClass(name, b, protectionDomain);
	}
	
	public static Package defPackage(String name, String specTitle, String specVersion, String specVendor, 
			String implTitle, String implVersion, String implVendor, URL sealBase) 
	{
		return classDef.definePackage(name, specTitle, specVersion, specVendor, implTitle,
				implVersion, implVendor, sealBase);
	}
	
	public static void resolvClass(Class<?> clazz)
	{
		classDef.resolveClass(clazz);
	}
	
	public static Class<?> getClassByCanonicalName(String name) throws ClassNotFoundException
	{
		int index;
		if((index = name.indexOf("[")) == -1)
			return Class.forName(name);
		else
		{
			Class<?> rootClass;
			String root = name.substring(0, index);
			String acount = name.substring(index);
			
//			System.out.println(acount); debug
			
			rootClass = Class.forName(root);
			
			assert (acount.length() & 0x1) == 0; // debug
			int[] is = new int[acount.length() >> 1];
			Arrays.fill(is, 0);
			
			return Array.newInstance(rootClass, is).getClass();
		}
	}
	
	public static Class<?> getPrimitiveClass(String name) throws ClassNotFoundException
	{
		Class<?> c;
		if((c = pClass.get(name)) == null)
			throw new ClassNotFoundException(name);
		return c;
	}
	
	public static Class<?> getClassByName(String name) throws ClassNotFoundException
	{
		Class<?> root = getClassByName0(name);
		
		if(!name.startsWith("["))
			return root;
		
		int arraycount = 0;
		do
			arraycount++;
		while(name.substring(arraycount, name.length()).startsWith("["));
		
		int[] is = new int[arraycount];
		Arrays.fill(is, 1);
		
		return Array.newInstance(root, is).getClass();
	}
	
	public static Class<?> getRootClassByName(String name) throws ClassNotFoundException
	{
		return getClassByName0(name);
	}
	
	public static boolean isPrimitiveClass(String name)
	{
		return pClass.containsKey(name);
	}
	
	public static Class<?> toPrimitiveClass(Class<?> clazz)
	{
		return classes.get(clazz);
	}
	
	private static final Class<?> getClassByName0(String name) throws ClassNotFoundException
	{
		String n = name;
		if(!n.endsWith(";"))
			switch(n)
			{
			case "Z": return boolean.class;
			case "B": return byte.class;
			case "C": return char.class;
			case "D": return double.class;
			case "F": return float.class;
			case "I": return int.class;
			case "J": return long.class;
			case "S": return short.class;
			default: return Class.forName(name);
			}
		int i = n.indexOf("L");
		if(i == -1)
			throw new IllegalArgumentException();
		String classname = n.substring(++i, n.length() - 1).replace("/", ".");
		return Class.forName(classname);
	}
	
	public static void touch()
	{
		// <clinit>
	}
	
	static {
		Map<String, Class<?>> pC = new HashMap<>(8);
		pC.put("boolean", boolean.class);
		pC.put("byte", byte.class);
		pC.put("char", char.class);
		pC.put("double", double.class);
		pC.put("float", float.class);
		pC.put("int", int.class);
		pC.put("long", long.class);
		pC.put("short", short.class);
		pClass = pC;
		
		Map<Class<?>, Class<?>> cls = new HashMap<>(8);
		cls.put(Boolean.class, boolean.class);
		cls.put(Byte.class, byte.class);
		cls.put(Character.class, char.class);
		cls.put(Double.class, double.class);
		cls.put(Float.class, float.class);
		cls.put(Integer.class, int.class);
		cls.put(Long.class, long.class);
		cls.put(Short.class, short.class);
		classes = cls;
	}
	
	private static final Map<String, Class<?>> pClass;
	
	private static final Map<Class<?>, Class<?>> classes;
	
	private static final ClassDef classDef = new ClassDef();
}
