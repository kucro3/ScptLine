package org.kucro3.scptline.anno;

import java.lang.annotation.*;

/**
 * 被此注解注释的方法或域将被视为导出元素.
 * 并且导出的函数必须符合参数规则,参数规则请见<code>org.kucro3.scptline.SLRuntimeObject</code>.
 * @see org.kucro3.scptline.SLRuntimeObject
 * @author Kumonda221
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(SLExport.SLExportCollection.class)
public @interface SLExport {
	String[] targs() default {};
	
	String name() default "";
	
	String delegate() default "";
	
	Class<?>[] delegateParams() default {};
	
	boolean directInput() default false;
	
	int argCount() default -1;
	
	@Target({ElementType.FIELD, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Inherited
	public static @interface SLExportCollection
	{
		SLExport[] value();
	}
}
