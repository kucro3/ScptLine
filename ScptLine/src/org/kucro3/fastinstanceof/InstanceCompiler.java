package org.kucro3.fastinstanceof;

import org.kucro3.util.ClassDef;
import org.objectweb.asm.*;

public class InstanceCompiler implements Opcodes {
	public static InstanceCompiled compile(String cast) throws InstantiationException, IllegalAccessException
	{
		long id = i++;
		String name = new StringBuilder("org/kucro3/fastinstanceof/I").append(id).append(cast.replace(".", "_")).toString();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		
		cw.visit(V1_7, ACC_PUBLIC + ACC_FINAL, name, null, "org/kucro3/fastinstanceof/InstanceCompiled", null);
		
		constructor(cw);
		checkcast(cw, cast);
		
		cw.visitEnd();
		
		byte[] byts = cw.toByteArray();
		Class<?> compiled = ClassDef.defClass(byts, 0, byts.length);
		return ((InstanceCompiled)compiled.newInstance()).instance(cast);
	}
	
	private static void checkcast(ClassWriter cw, String cast)
	{
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL, "isInstance", "(Ljava/lang/Object;)Z", null, null);
		mv.visitIntInsn(ALOAD, 1);
		mv.visitTypeInsn(INSTANCEOF, cast.replace(".", "/"));
		mv.visitInsn(IRETURN); // ireturn
		mv.visitMaxs(1, 2);
		mv.visitEnd();
	}
	
	private static void constructor(ClassWriter cw)
	{
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitIntInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "org/kucro3/fastinstanceof/InstanceCompiled", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private static volatile long i;
}
