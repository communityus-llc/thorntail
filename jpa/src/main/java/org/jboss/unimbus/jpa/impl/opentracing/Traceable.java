package org.jboss.unimbus.jpa.impl.opentracing;

/**
 * Created by bob on 2/27/18.
 */
public interface Traceable<T> {
    T execute();
}
