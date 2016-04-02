package org.kucro3.scptline.dict;

import org.kucro3.scptline.dict.SLDictionaryLoader.SLExportedInfo;

public abstract class SLExported {
	SLExported(SLExportedInfo metadata)
	{
		this.metadata = metadata;
	}
	
	public final SLExportedInfo getMetadata()
	{
		return metadata;
	}
	
	public abstract String getName();
	
	final SLExportedInfo metadata;
}
