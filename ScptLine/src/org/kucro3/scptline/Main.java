package org.kucro3.scptline;

import org.kucro3.ini.*;
import org.kucro3.scptline.anno.SLExport;
import org.kucro3.scptline.dict.SLDictionary;
import org.kucro3.scptline.dict.SLMain;
import org.kucro3.scptline.opstack.SLProcEngine;

public class Main {
	public static void main(String[] args)
	{
		try {
			IniProfile profile = new IniProfile();
			IniSection section = profile.createSection(SLProperty.SECTION_OPTIONS);
			section.setValue(SLProperty.PROP_ENV_HANDLER_STACK_SIZE, "20");
			section.setValue(SLProperty.PROP_ENV_INTERRUPT_POINT_ENABLED, "true");
			SLEnvironment env = new SLEnvironment(profile);
			env.load(Test.class);
			env.getHandlerStack().add(new SLProcEngine(env));
			env.execute(new String[] {
				"print \"Hello world!\"aa"
			});
		} finally {
		}
	}
	
	@SLExport(name = "Test")
	public static class Test implements SLMain, SLDictionary
	{
		@SLExport(name = "test", targs = {"nint", "nint", "nint"})
		public void test(SLEnvironment env, int i, int j, int k)
		{
			System.out.println("Hello world" + i + j + k);
		}
		
		@SLExport(name = "print", targs = {"lstr"})
		public void print(SLEnvironment env, String s)
		{
			System.out.print(s);
		}
		
		@Override
		public SLDictionary onLoad(SLEnvironment env, Object reserved) 
		{
			return this;
		}
	}
}
