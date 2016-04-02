package org.kucro3.scptline.dict;

import org.kucro3.scptline.SLObject;

public interface SLDictionaryObject extends SLObject {
	public SLDictionaryLoaded getDict();
	
	public SLDictionary getOwner();
}
