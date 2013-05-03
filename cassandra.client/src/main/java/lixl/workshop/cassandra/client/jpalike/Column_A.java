/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.jpalike;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import lixl.workshop.cassandra.model.CassandraType;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
public @interface Column_A {
	String name() default "";
	CassandraType type();

}
