package org.csu.api.utils;

@FunctionalInterface
public interface ListBeanUtilsCallBack<S, T> {
    void callback(S s, T t);
}
