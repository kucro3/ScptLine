package org.kucro3.scptline.dict;

import java.io.File;
import java.net.URL;
import java.util.*;

import org.kucro3.ini.IniProfile;
import org.kucro3.scptline.SLEnvironment;
import org.kucro3.scptline.SLFactory;

public final class SLDictionaryFactory implements SLFactory {
	private SLDictionaryFactory()
	{
	}
	
	public static SLDictionaryCollection newCollection(SLEnvironment env)
	{
		if(refs.containsKey(env))
			return null;
		SLDictionaryCollection sldc = newCollectionInstance(env);
		refs.put(env, sldc);
		return sldc;
	}
	
	@Deprecated
	public static SLDictionaryCollection newCollectionEscaped(SLEnvironment env)
	{
		return newCollectionInstance(env);
	}
	
	private static SLDictionaryCollection newCollectionInstance(SLEnvironment env)
	{
		return new SLDictionaryCollection(env);
	}
	
	public static SLDictionaryCollection getCollection(SLEnvironment env)
	{
		return refs.get(env);
	}
	
	public static SLDictionaryLoader newLoader(SLEnvironment env, File file)
	{
		return SLDictionaryLoader.newLoader(env, file);
	}
	
	public static SLDictionaryLoader newLoader(SLEnvironment env, URL url)
	{
		return SLDictionaryLoader.newLoader(env, url);
	}
	
	public static SLDictionaryLoader newLoader(SLEnvironment env, IniProfile profile)
	{
		return SLDictionaryLoader.newLoader(env, profile);
	}
	
	public static SLDictionaryLoaded load(SLEnvironment env, File file)
	{
		return load(env, newLoader(env, file));
	}
	
	public static SLDictionaryLoaded load(SLEnvironment env, URL url)
	{
		return load(env, newLoader(env, url));
	}
	
	public static SLDictionaryLoaded load(SLEnvironment env, IniProfile profile)
	{
		return load(env, newLoader(env, profile));
	}
	
	public static SLDictionaryLoaded load(SLEnvironment env, SLDictionaryLoader loader)
	{
		SLDictionaryLoaded loaded = loader.callOnLoad(env);
		if(loaded == null)
			return null;
		
		Map<String, SLFieldLoaded> mapF = new HashMap<>();
		Map<String, SLMethodLoaded> mapM = new HashMap<>();
		loader.loadExports(mapF, mapM);
		loaded.addFields(mapF.values());
		loaded.addMethods(mapM.values());
		
		return loaded;
	}
	
	public static boolean bind(SLDictionaryCollection collection, SLDictionaryLoaded loaded)
	{
		return collection.bind(loaded);
	}
	
	private static final Map<SLEnvironment, SLDictionaryCollection> refs = new WeakHashMap<>();
}
