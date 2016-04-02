package org.kucro3.scptline;

/**
 * 运行时环境对象,此对象无需被一个指定环境所拥有,也不需要遵守环境对象的构造方法规范.
 * 但是任何一个导出的函数都必须遵守<strong>(SLEnvrionment, SLObject[reserved],  ...)</strong>形式的参数规范.
 * @see org.kucro3.scptline.anno.SLExport
 * @author Kumonda221
 *
 */
public interface SLRuntimeObject {
	
}
