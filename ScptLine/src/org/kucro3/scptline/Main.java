package org.kucro3.scptline;

import org.kucro3.ini.*;
import org.kucro3.scptline.anno.SLExport;
import org.kucro3.scptline.dict.SLDictionary;
import org.kucro3.scptline.dict.SLDictionaryFactory;
import org.kucro3.scptline.dict.SLDictionaryLoader;
import org.kucro3.scptline.dict.SLMain;
import org.kucro3.scptline.opstack.SLProcEngine;

public class Main {
	public static void main(String[] args)
	{
		IniProfile profile = new IniProfile();
		IniSection section = profile.createSection(SLProperty.SECTION_OPTIONS);
		section.setValue(SLProperty.PROP_ENV_HANDLER_STACK_SIZE, "20");
		section.setValue(SLProperty.PROP_ENV_INTERRUPT_POINT_ENABLED, "true");
		SLEnvironment env = new SLEnvironment(profile);
		section = profile.createSection(SLDictionaryLoader.SECTION_MAIN);
		section.setValue(SLDictionaryLoader.KEY_MAIN_CLASS_IN_SECTION_MAIN,
				"org.kucro3.scptline.Main$Test");
		SLDictionaryFactory.bind(env.getDictionaries(), 
				SLDictionaryFactory.load(env, profile));
		env.getHandlerStack().add(new SLProcEngine(env));
		env.execute("?Test:test 1", 1);
	}
	
	@SLExport(name = "Test")
	public static class Test implements SLMain, SLDictionary
	{
		@SLExport(name = "test", targs = {"nint"})
		public void test(SLEnvironment env, int i)
		{
			System.out.println("Hello world" + i);
		}
		
		@Override
		public SLDictionary onLoad(SLEnvironment env, Object reserved) 
		{
			return this;
		}
	}
}
