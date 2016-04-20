package org.kucro3.scptline.dict;

import java.io.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.kucro3.ini.IniProfile;
import org.kucro3.ini.IniSection;
import org.kucro3.scptline.SLEnvironment;
import org.kucro3.scptline.SLException;
import org.kucro3.scptline.SLFactory;
import org.kucro3.scptline.SLObject;
import org.kucro3.scptline.anno.SLExport;

import static org.kucro3.scptline.InternalError.ShouldNotReachHere;

public class SLDictionaryLoader extends URLClassLoader implements SLFactory, SLObject {
	private SLDictionaryLoader(SLEnvironment env, URL[] urls)
	{
		this(env, urls, null);
	}
	
	private SLDictionaryLoader(SLEnvironment env, URL url)
	{
		this(env, url, null);
	}
	
	private SLDictionaryLoader(SLEnvironment env, File file) throws MalformedURLException
	{
		this(env, file, null);
	}
	
	private SLDictionaryLoader(SLEnvironment env, IniProfile profile)
	{
		super(new URL[0], env.getClass().getClassLoader());
		this.file = null;
		this.env = env;
		
		this.initProfile(profile);
	}
	
	private SLDictionaryLoader(SLEnvironment env, File file, IniProfile profile)
			throws MalformedURLException
	{
		super(new URL[] {file.toURI().toURL()}, env.getClass().getClassLoader());
		this.file = file;
		this.env = env;
		
		this.initProfile(profile);
	}
	
	private SLDictionaryLoader(SLEnvironment env, URL url, IniProfile profile)
	{
		this(env, new URL[] {url}, profile);
	}
	
	private SLDictionaryLoader(SLEnvironment env, URL[] url, IniProfile profile)
	{
		super(url, env.getClass().getClassLoader());
		this.file = null;
		this.env = env;
		
		this.initProfile(profile);
	}
	
	static SLDictionaryLoader newLoader(SLEnvironment env, IniProfile profile)
	{
		return new SLDictionaryLoader(env, profile);
	}
	
	static SLDictionaryLoader newLoader(SLEnvironment env, URL[] urls)
	{
		return new SLDictionaryLoader(env, urls);
	}
	
	static SLDictionaryLoader newLoader(SLEnvironment env, URL[] urls, IniProfile profile)
	{
		return new SLDictionaryLoader(env, urls, profile);
	}
	
	static SLDictionaryLoader newLoader(SLEnvironment env, URL url)
	{
		return new SLDictionaryLoader(env, url);
	}
	
	static SLDictionaryLoader newLoader(SLEnvironment env, URL url, IniProfile profile)
	{
		return new SLDictionaryLoader(env, url, profile);
	}
	
	static SLDictionaryLoader newLoader(SLEnvironment env, File file)
	{
		return newLoader(env, file, null);
	}
	
	static SLDictionaryLoader newLoader(SLEnvironment env, File file, IniProfile profile)
	{
		try {
			return new SLDictionaryLoader(env, file, profile);
		} catch (MalformedURLException e) {
			throw SLDictionaryLoaderException.newMalformedURL(env, e);
		}
	}
	
	public static boolean addExportedField(SLDictionaryLoader loader, SLExportedInfo info,
			Field field)
	{
		loader.loadExportedFieldAsField(field, info, loader.loaded.fields, true);
		return true;
	}
	
	public static boolean addExportedField(SLDictionaryLoader loader, SLExport export,
			Field field)
	{
		return addExportedField(loader, new SLExportedInfo(export), field);
	}
	
	public static boolean addExportedField(SLDictionaryLoader loader, SLExportedInfo info,
			Method method)
	{
		loader.loadExportedMethodAsField(method, info, loader.loaded.fields, true);
		return true;
	}
	
	public static boolean addExportedField(SLDictionaryLoader loader, SLExport export,
			Method method)
	{
		return addExportedField(loader, new SLExportedInfo(export), method);
	}
	
	public static boolean replaceExportedField(SLDictionaryLoader loader, SLExportedInfo info,
			Field field)
	{
		loader.loadExportedFieldAsField(field, info, loader.loaded.fields, false);
		return true;
	}
	
	public static boolean replaceExportedField(SLDictionaryLoader loader, SLExport export,
			Field field)
	{
		return replaceExportedField(loader, new SLExportedInfo(export), field);
	}
	
	public static boolean replaceExportedField(SLDictionaryLoader loader, SLExportedInfo info,
			Method method)
	{
		loader.loadExportedMethodAsField(method, info, loader.loaded.fields, false);
		return true;
	}
	
	public static boolean replaceExportedField(SLDictionaryLoader loader, SLExport export,
			Method method)
	{
		return replaceExportedField(loader, new SLExportedInfo(export), method);
	}
	
	public static boolean removeExportedField(SLDictionaryLoader loader, String name)
	{
		return loader.loaded.fields.remove(name) != null;
	}
	
	public static boolean removeExportedField(SLDictionaryLoader loader, SLExportedInfo info)
	{
		return removeExportedField(loader, info.name());
	}
	
	public static boolean removeExportedField(SLDictionaryLoader loader, SLExport export)
	{
		return removeExportedField(loader, export.name());
	}
	
	public static boolean addExportedMethod(SLDictionaryLoader loader, SLExportedInfo info,
			Method method)
	{
		loader.loadExportedMethodAsMethod(method, info, loader.loaded.methods, true);
		return true;
	}
	
	public static boolean addExportedMethod(SLDictionaryLoader loader, SLExport export,
			Method method)
	{
		return addExportedMethod(loader, new SLExportedInfo(export), method);
	}
	
	public static boolean addExportedMethod(SLDictionaryLoader loader, SLExportedInfo info,
			Field field)
	{
		loader.loadExportedFieldAsMethod(field, info, loader.loaded.methods, true);
		return true;
	}
	
	public static boolean addExportedMethod(SLDictionaryLoader loader, SLExport export,
			Field field)
	{
		return addExportedMethod(loader, new SLExportedInfo(export), field);
	}
	
	public static boolean removeExportedMethod(SLDictionaryLoader loader, String name)
	{
		return loader.loaded.methods.remove(name) != null;
	}
	
	public static boolean removeExportedMethod(SLDictionaryLoader loader, SLExportedInfo info)
	{
		return removeExportedMethod(loader, info.name());
	}
	
	public static boolean removeExportedMethod(SLDictionaryLoader loader, SLExport export)
	{
		return removeExportedMethod(loader, export.name());
	}
	
	public static boolean replaceExportedMethod(SLDictionaryLoader loader, SLExportedInfo info,
			Method method)
	{
		loader.loadExportedMethodAsMethod(method, info, loader.loaded.methods, false);
		return true;
	}
	
	public static boolean replaceExportedMethod(SLDictionaryLoader loader, SLExport export,
			Method method)
	{
		return replaceExportedMethod(loader, new SLExportedInfo(export), method);
	}
	
	public static boolean replaceExportedMethod(SLDictionaryLoader loader, SLExportedInfo info,
			Field field)
	{
		loader.loadExportedFieldAsMethod(field, info, loader.loaded.methods, false);
		return true;
	}
	
	public static boolean replaceExportedMethod(SLDictionaryLoader loader, SLExport export,
			Field field)
	{
		return replaceExportedMethod(loader, new SLExportedInfo(export), field);
	}
	
	public File getFile()
	{
		return file;
	}
	
	public final SLEnvironment getEnv()
	{
		return env;
	}
	
	public IniSection getConfig()
	{
		return sectionConfig;
	}
	
	public Class<?> seekForMainClass()
	{
		if(mainClass == null)
			return mainClass = seekForClassInSectionMain(KEY_MAIN_CLASS_IN_SECTION_MAIN);
		return mainClass;
	}
		
	final Class<?> seekForClassInSectionMain(String key)
	{
		String clz = sectionMain.getValue(key);
		if(clz == null)
			throw SLDictionaryLoaderException.newKeyNotFoundInSection(env, key, SECTION_MAIN);
		try {
			return super.loadClass(clz, true);
		} catch (ClassNotFoundException e) {
			throw SLDictionaryLoaderException.newClassNotFound(env, e);
		}
	}
	
	final void initProfile(IniProfile profile)
	{
		try {
			if(profile == null)
			{
				InputStream is = this.getResourceAsStream(CONFIGURATION);
				if(is == null)
					throw SLDictionaryLoaderException.newConfigNotFound(env);
				profile = new IniProfile(is);
			}
			sectionMain = profile.getSection(SECTION_MAIN);
			if(sectionMain == null)
				throw SLDictionaryLoaderException.newNoSuchConfigSection(env, SECTION_MAIN);
			sectionConfig = profile.getSection(SECTION_CONFIGURATION);
			if(sectionConfig == null)
				sectionConfig = new IniSection(SECTION_CONFIGURATION);
		} catch (IOException e) {
			throw SLDictionaryLoaderException.newIOException(env, e);
		}
	}
	
	SLMain constructMainObject()
	{
		try {
			Object obj = seekForMainClass().newInstance();
			if(obj instanceof SLMain)
				return (SLMain)obj;
			throw SLDictionaryLoaderException.newConstructionInstance(env, 
					obj.getClass().getSimpleName(), SLMain.class.getSimpleName());
		} catch (IllegalAccessException e) {
			throw SLDictionaryLoaderException.newConstructionIllegalAccess(env, e);
		} catch (InstantiationException e) {
			throw SLDictionaryLoaderException.newConstructionInstantiation(env, e);
		} catch (ExceptionInInitializerError e) {
			throw SLDictionaryLoaderException.newConstructionInternal(env, e.getException());
		}
	}
	
	public void loadExports(Map<String, SLFieldLoaded> mapField,
			Map<String, SLMethodLoaded> mapMethod)
	{
		Class<?> clz;
		if((clz = this.dictionaryClass) == null)
			return;
		
		Map<String, SLFieldLoaded> mapF = new HashMap<>();
		Map<String, SLMethodLoaded> mapM = new HashMap<>();
		Field[] fields = clz.getFields();
		Method[] methods = clz.getMethods();
		AnnotatedElement[][] elements = {fields, methods};
		AnnotatedElement reference;
		SLExport[] metadataArray;
		SLExport metadata;
		
		for(int i = 0; i < 2; i++)
			for(int j = 0; j < elements[i].length; j++)
				if((metadataArray = (reference = elements[i][j]).getAnnotationsByType(SLExport.class))
						.length == 0)
					continue;
				else
					for(int k = 0; k < metadataArray.length; k++)
					{
						metadata = metadataArray[k];
						if(metadata.name().equals(METADATA_NAME_UNKNOWN))
							throw SLDictionaryLoaderException.newMetadataNameUndefined(env);
						loadExport(reference, metadata, i, mapF, mapM);
					}
		
		mapField.putAll(mapF);
		mapMethod.putAll(mapM);
	}
	
	private final void loadExport(AnnotatedElement ref, SLExport export, int type,
			Map<String, SLFieldLoaded> mapF, Map<String, SLMethodLoaded> mapM)
	{
		int realType = getType(export);
		BLOCK: switch(type)
		{
		case TYPE_FIELD:
			switch(realType)
			{
			case TYPE_FIELD:
				loadExportedFieldAsField((Field)ref, new SLExportedInfo(export), mapF,
						true);
				return;
				
			case TYPE_METHOD:
				loadExportedFieldAsMethod((Field)ref, new SLExportedInfo(export), mapM,
						true);
				return;
				
			default:
				break BLOCK;
			}
			
		case TYPE_METHOD:
			switch(realType)
			{
			case TYPE_FIELD:
				loadExportedMethodAsField((Method)ref, new SLExportedInfo(export), mapF,
						true);
				return;
				
			case TYPE_METHOD:
				loadExportedMethodAsMethod((Method)ref, new SLExportedInfo(export), mapM,
						true);
				return;
			
			default:
				break BLOCK;
			}
			
		default:
			break BLOCK;
		}
		ShouldNotReachHere();
	}
	
	private final void loadExportedFieldAsField(Field field, SLExportedInfo export,
			Map<String, SLFieldLoaded> mapF, boolean checkDuplication)
	{
		if(checkDuplication)
			this.checkFieldDuplication(export, mapF);
		
		mapF.put(export.name(), new SLFieldLoaded(env, loaded, dictionaryObject, 
				export, field));
	}
	
	private final void loadExportedFieldAsMethod(Field field, SLExportedInfo export,
			Map<String, SLMethodLoaded> mapM, boolean checkDuplication)
	{
		if(checkDuplication)
			this.checkMethodDuplication(export, mapM);
		
		String delegateMethod;
		Class<?>[] delegateParams;
		Class<?> clz = field.getType();
		if((delegateMethod = export.delegate()) == METADATA_NAME_UNKNOWN)
			throw SLDictionaryLoaderException.newDelegateNotSpecified(env, export.name());
		delegateParams = export.delegateParams();
		try {
			Method method = clz.getMethod(delegateMethod, delegateParams);
			mapM.put(export.name(), new SLMethodLoaded(env, loaded, dictionaryObject,
					dictionaryObject, field, export, method));
		} catch (NoSuchMethodException e) {
			throw SLDictionaryLoaderException.newDelegateUnsatisfied(env, 
					field.toString(), delegateMethod);
		}
	}
	
	private final void loadExportedMethodAsMethod(Method method, SLExportedInfo export,
			Map<String, SLMethodLoaded> mapM, boolean checkDuplication)
	{
		if(checkDuplication)
			this.checkMethodDuplication(export, mapM);
		
		mapM.put(export.name(), new SLMethodLoaded(env, loaded, dictionaryObject,
				export, method));
	}
	
	private final void loadExportedMethodAsField(Method method, SLExportedInfo export,
			Map<String, SLFieldLoaded> mapF, boolean checkDuplication)
	{
		if(checkDuplication)
			this.checkFieldDuplication(export, mapF);
		
		boolean envRequired;
		if(method.getParameterCount() == 0)
			envRequired = false;
		else if(method.getParameterCount() == 1 &&
				method.getParameterTypes()[0] == SLEnvironment.class)
			envRequired = true;
		else
			throw SLDictionaryLoaderException.newDelegateUnsatisfied(env, 
					method.toString(), export.name());
		mapF.put(export.name(), new SLFieldLoaded(env, loaded, dictionaryObject, export,
				method, envRequired));
	}
	
	public SLMain requireMainObject()
	{
		if(mainObject == null)
			return mainObject = constructMainObject();
		return mainObject;
	}
	
	final SLDictionaryLoaded callOnLoad(SLEnvironment env)
	{
		SLMain main = requireMainObject();
		this.ensureName();
		if((this.dictionaryObject = main.onLoad(env, null)) == null)
			return null;
		this.dictionaryClass = dictionaryObject.getClass();
		
		return constructLoaded(env);
	}
	
	public SLDictionaryLoaded callOnLoad()
	{
		return callOnLoad(env);
	}
	
	final SLDictionaryLoaded constructLoaded(SLEnvironment env)
	{
		String name = this.ensureName();
		
		if(name == null)
			return null;
		
		if(loaded != null)
			return loaded;
		else
			return this.loaded = new SLDictionaryLoaded(env, name, 
					dictionaryObject, mainObject, this);
	}
	
	public final SLDictionaryLoaded constructLoaded()
	{
		return this.constructLoaded(env);
	}
	
	private final int getType(SLExport export)
	{
		if(export.targs().length == 0)
			return TYPE_FIELD;
		return TYPE_METHOD;
	}
	
	final void checkMethodDuplication(SLExportedInfo info, Map<String, SLMethodLoaded> mapM)
	{
		String name;
		if(mapM.containsKey(name = info.name()))
			throw SLDictionaryLoaderException.newMethodNameDuplicated(env, name);
	}
	
	final void checkFieldDuplication(SLExportedInfo info, Map<String, SLFieldLoaded> mapF)
	{
		String name;
		if(mapF.containsKey(name = info.name))
			throw SLDictionaryLoaderException.newFieldNameDuplicated(env, name);
	}
	
	final String ensureName()
	{
		if(name != null)
			return name;
		if(mainClass == null)
			return null;
		SLExport metadata = mainClass.getAnnotation(SLExport.class);
		if(!env.isLoadingInline() &&
			   (metadata == null || metadata.name() == null 
								|| metadata.name().equals(METADATA_NAME_UNKNOWN)))
			throw SLDictionaryLoaderException.newMetadataNameUndefined(env);
		return name = metadata.name();
	}
	
	private final SLEnvironment env;
	
	private final File file;
	
	private SLDictionaryLoaded loaded;
	
	Class<?> mainClass;
	
	Class<?> dictionaryClass;
	
	SLMain mainObject;
	
	SLDictionary dictionaryObject;
	
	IniSection sectionMain;
	
	IniSection sectionConfig;
	
	String name;
	
	public static final String METADATA_NAME_UNKNOWN = "";
	
	public static final String CONFIGURATION = "config.ini";
	
	public static final String SECTION_MAIN = "Main";
	
	public static final String KEY_MAIN_CLASS_IN_SECTION_MAIN = "MainClass";
	
	public static final String SECTION_CONFIGURATION = "Config";
	
	private static final int TYPE_FIELD = 0;
	
	private static final int TYPE_METHOD = 1;
	
	public static class SLDictionaryLoaderException extends SLException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 7967350349061847416L;
		
		public SLDictionaryLoaderException(SLEnvironment env, SLExceptionLevel level,
				String stub)
		{
			super(env, level, DESCRIPTION, stub);
		}
		
		public SLDictionaryLoaderException(SLEnvironment env, SLExceptionLevel level,
				String stub, String message)
		{
			super(env, level, DESCRIPTION, stub, message);
		}
		
		public static SLDictionaryLoaderException newIOException(SLEnvironment env,
				IOException e)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_IO_EXCEPTION,
					String.format(MESSAGE_IO_EXCEPTION, e.getMessage()));
		}
		
		public static SLDictionaryLoaderException newMalformedURL(SLEnvironment env,
				MalformedURLException e)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_MALFORMED_URL,
					String.format(MESSAGE_MALFORMED_URL, e.getMessage()));
		}
		
		public static SLDictionaryLoaderException newConfigNotFound(SLEnvironment env)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_CONFIGURATION_NOT_FOUND,
					MESSAGE_CONFIGURATION_NOT_FOUND);
		}
		
		public static SLDictionaryLoaderException newNoSuchConfigSection(SLEnvironment env,
				String s)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_NO_SUCH_CONFIGURATION_SECTION,
					String.format(MESSAGE_NO_SUCH_CONFIGURATION_SECTION, s));
		}
		
		public static SLDictionaryLoaderException newKeyNotFoundInSection(SLEnvironment env,
				String key, String section)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_KEY_NOT_FOUND_IN_SECTION,
					String.format(MESSAGE_KEY_NOT_FOUND_IN_SECTION, key, section));
		}
		
		public static SLDictionaryLoaderException newClassNotFound(SLEnvironment env,
				ClassNotFoundException e)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_CLASS_NOT_FOUND,
					String.format(MESSAGE_CLASS_NOT_FOUND, e.getMessage()));
		}
		
		public static SLDictionaryLoaderException newFieldNameDuplicated(SLEnvironment env,
				String name)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_FIELD_NAME_DUPLICATED,
					String.format(MESSAGE_FIELD_NAME_DUPLICATED, name));
		}
		
		public static SLDictionaryLoaderException newMethodNameDuplicated(SLEnvironment env,
				String name)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_METHOD_NAME_DUPLICATED,
					String.format(MESSAGE_METHOD_NAME_DUPLICATED, name));
		}
		
		public static SLDictionaryLoaderException newMetadataNameUndefined(SLEnvironment env)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_METADATA_NAME_UNDEFINED,
					MESSAGE_METADATA_NAME_UNDEFINED);
		}
		
		public static SLDictionaryLoaderException newConstructionIllegalAccess(SLEnvironment env,
				IllegalAccessException e)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_CONSTRUCTION_ILLEGAL_ACCESS,
					String.format(MESSAGE_CONSTRUCTION_ILLEGAL_ACCESS, e.getMessage()));
		}
		
		public static SLDictionaryLoaderException newConstructionInstantiation(SLEnvironment env,
				InstantiationException e)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_CONSTRUCTION_INSTANTIATION,
					String.format(MESSAGE_CONSTRUCTION_INSTANTIATION, e.getMessage()));
		}
		
		public static SLDictionaryLoaderException newConstructionInternal(SLEnvironment env,
				Throwable e)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_CONSTRUCTION_INTERNAL,
					String.format(MESSAGE_CONSTRUCTION_INTERNAL, 
							e.getClass().getSimpleName(),
							e.getMessage()));
		}
		
		public static SLDictionaryLoaderException newNullDictionaryName(SLEnvironment env)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_NULL_DICTIONARY_NAME,
					MESSAGE_NULL_DICTIONARY_NAME);
		}
		
		public static SLDictionaryLoaderException newConstructionInstance(SLEnvironment env,
				String src, String dest)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_CONSTRUCTION_INSTANCE,
					String.format(MESSAGE_CONSTRUCTION_INSTANCE, src, dest));
		}
		
		public static SLDictionaryLoaderException newDelegateNotSpecified(SLEnvironment env,
				String name)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_DELEGATE_NOT_SPECIFIED,
					String.format(MESSAGE_DELEGATE_NOT_SPECIFIED, name));
		}
		
		public static SLDictionaryLoaderException newDelegateUnsatisfied(SLEnvironment env,
				String owner, String delegate)
		{
			return new SLDictionaryLoaderException(env, SLExceptionLevel.INTERRUPT,
					MESSAGE_DELEGATE_UNSATISFIED,
					String.format(MESSAGE_DELEGATE_UNSATISFIED, owner, delegate));
		}
		
		public static final String MESSAGE_IO_EXCEPTION = "I/O Exception: %s";
		
		public static final String MESSAGE_MALFORMED_URL = "Malformed URL: %s";
		
		public static final String MESSAGE_CONFIGURATION_NOT_FOUND = "Configuration file not found";
		
		public static final String MESSAGE_NO_SUCH_CONFIGURATION_SECTION = "Configuration section not found: %s";
		
		public static final String MESSAGE_CLASS_NOT_FOUND = "Class not found: %s";
		
		public static final String MESSAGE_KEY_NOT_FOUND_IN_SECTION = "Key not found: %s in section: %s";
		
		public static final String MESSAGE_FIELD_NAME_DUPLICATED = "Duplicated field name: %s";
		
		public static final String MESSAGE_METHOD_NAME_DUPLICATED = "Duplicated method name: %s";
		
		public static final String MESSAGE_METADATA_NAME_UNDEFINED = "Name undefined in metadata.";
		
		public static final String MESSAGE_CONSTRUCTION_ILLEGAL_ACCESS = "Construction Failure (IllegalAccessException): %s";
		
		public static final String MESSAGE_CONSTRUCTION_INSTANTIATION = "Construction Failure (InstantiationException): %s";
		
		public static final String MESSAGE_CONSTRUCTION_INTERNAL = "Construction Failure (Dictionary Internal Exception: %s): %s";
		
		public static final String MESSAGE_CONSTRUCTION_INSTANCE = "Construction Failure (ClassCastException): %s cannot be cast to %s";
		
		public static final String MESSAGE_NULL_DICTIONARY_NAME = "Null dictionary name";
		
		public static final String MESSAGE_DELEGATE_NOT_SPECIFIED = "Delegate not specified: %s";
		
		public static final String MESSAGE_DELEGATE_UNSATISFIED = "Delegate unsatisfied: %s:%s";
		
		public static final String DESCRIPTION = "An exception occurred in dictionary loader";
	}
	
	public static class SLExportedInfo
	{
		public SLExportedInfo()
		{
			this("", new String[0], "", new Class<?>[0]);
		}
		
		public SLExportedInfo(String name)
		{
			this(name, new String[0], "", new Class<?>[0]);
		}
		
		public SLExportedInfo(String name, String[] targs)
		{
			this(name, targs, "", new Class<?>[0]);
		}
		
		public SLExportedInfo(String name, String[] targs, String delegate,
				Class<?>[] delegateParams)
		{
			this.name = name;
			this.targs = targs;
			this.delegate = delegate;
			this.delegateParams = delegateParams;
		}
		
		SLExportedInfo(SLExport export)
		{
			this.name = export.name();
			this.delegate = export.delegate();
			this.delegateParams = export.delegateParams();
			this.targs = export.targs();
		}
		
		public String type()
		{
			return type;
		}
		
		public String name()
		{
			return name;
		}
		
		public String delegate()
		{
			return delegate;
		}
		
		public Class<?>[] delegateParams()
		{
			return delegateParams;
		}
		
		public String[] targs()
		{
			return targs;
		}
		
		String type; // reserved
		
		String name;
		
		Class<?>[] delegateParams;
		
		String delegate;
		
		String[] targs;
	}
}