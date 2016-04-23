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
			env.getVarMap().define("bool", 1);
			env.execute(new String[] {
				"printobj $bool",
				"$bool int",
				"$bool bool",
				"$bool !",
				"printobj $bool",
				"$bool = true",
				"printobj $bool",
			});
		} finally {
		}
	}
	
	@SLExport(name = "Test")
	public static class Test implements SLMain, SLDictionary
	{
		@SLExport(name = "printobj", targs = {"lobj"})
		public void printobj(SLEnvironment env, Object obj)
		{
			System.out.println(obj);
		}
		
		@SLExport(name = "print", targs = {"lstr"})
		public Object print(SLEnvironment env, String s)
		{
			System.out.print(s);
			return s;
		}
		
		@SLExport(name = "println", targs = {"vargs"})
		public void println(SLEnvironment env, String[] strs)
		{
			for(String str : strs)
				System.out.println(str);
		}
		
		@Override
		public SLDictionary onLoad(SLEnvironment env, Object reserved) 
		{
			return this;
		}
	}
}
