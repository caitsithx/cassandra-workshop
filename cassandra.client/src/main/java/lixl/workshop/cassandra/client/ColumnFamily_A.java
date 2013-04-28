/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
@Target (ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnFamily_A {
    String name();
    boolean isSuper() default false;
}
