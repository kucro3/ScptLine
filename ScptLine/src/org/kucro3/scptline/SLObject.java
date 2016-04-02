package org.kucro3.scptline;

/**
 * 任何一个会被运行环境所拥有的环境对象<strong>都应实现</strong>此接口. 
 * 并且实现此接口的环境对象必须有符合参数格式的构造方法. 
 * 参数格式为: (SLEnvironment, ...)
 * @author Kumonda221
 * 
 */
public interface SLObject {
	/**
	 * 返回拥有此对象的运行环境.
	 * @return 运行环境
	 */
	public SLEnvironment getEnv();
}
