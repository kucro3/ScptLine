package org.kucro3.scptline.dict;

import org.kucro3.scptline.SLEnvironment;

public interface SLMain {
	SLDictionary onLoad(SLEnvironment env, Object reserved);
	
//	void onUnload(SLEnvironment env, Object reserved);
}
