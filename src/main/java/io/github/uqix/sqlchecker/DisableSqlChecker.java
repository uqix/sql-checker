package io.github.uqix.sqlchecker;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = java.lang.annotation.ElementType.METHOD)
@Documented
public @interface DisableSqlChecker {
}
