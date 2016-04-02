package org.kucro3.scptline;

public interface SLExceptionHandler {
	public abstract boolean handle(SLEnvironment env, SLException e);
}
