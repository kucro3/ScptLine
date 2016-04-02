package org.kucro3.ini;

import java.io.IOException;

public class IniFormatException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8351632377020152064L;
	
	public IniFormatException(String line)
	{
		super(line);
	}
}
