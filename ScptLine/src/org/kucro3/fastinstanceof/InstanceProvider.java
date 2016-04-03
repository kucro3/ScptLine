package org.kucro3.fastinstanceof;

import java.lang.ref.*;
import java.util.*;

public class InstanceProvider {
	public InstanceProvider()
	{
	}
	
	public boolean isInstance(Object obj, String instance) throws InstantiationException, IllegalAccessException
	{
		SoftReference<InstanceCompiled> ref;
		if((ref = cache.get(obj)) != null && ref.get() != null)
			return ref.get().isInstance(obj);
		cache.put(instance, (ref = createRef(instance)));
		return ref.get().isInstance(obj);
	}
	
	private static SoftReference<InstanceCompiled> createRef(String instance) throws InstantiationException, IllegalAccessException
	{
		return new SoftReference<>(InstanceCompiler.compile(instance));
	}
	
	public void clearCache()
	{
		for(SoftReference<InstanceCompiled> reference : cache.values())
			reference.clear();
		cache.clear();
	}
	
	public static InstanceProvider getDefault()
	{
		return DEFAULT;
	}
	
	private static final InstanceProvider DEFAULT = new InstanceProvider();
	
	private final Map<String, SoftReference<InstanceCompiled>> cache = new HashMap<>();
}
