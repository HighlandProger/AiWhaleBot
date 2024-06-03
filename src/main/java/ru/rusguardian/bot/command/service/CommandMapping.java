package ru.rusguardian.bot.command.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandMapping {
    String[] viewCommands() default {};

    String blindCommand() default "";

    boolean isViewVariable() default false;

    boolean isBlindVariable() default false;
}
