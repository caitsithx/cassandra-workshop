/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.jpalike;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
@Target (ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Key_A {
	String name() default "";

}
