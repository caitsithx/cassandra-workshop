/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.client.jpalike;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:caitsithx@live.cn">lixl </a>
 *
 */
@Target (ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnFamily_A {
    String name();
    boolean isSuper() default false;
}
