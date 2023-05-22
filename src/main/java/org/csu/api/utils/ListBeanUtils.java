package org.csu.api.utils;

import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Supplier;

public class ListBeanUtils extends BeanUtils {

    public static <S, T> List<T> copyProperties(List<S> sourceList, Supplier<T> target) {
        return copyProperties(sourceList, target, (ListBeanUtilsCallBack<S, T>) null);
    }

    public static <S, T> List<T> copyProperties(List<S> sourceList, Supplier<T> target, ListBeanUtilsCallBack<S, T> callBack) {
        List<T> targetList = Lists.newArrayList();
        for (S source : sourceList) {
            T t = target.get();
            copyProperties(source, t);
            if (callBack != null) {
                callBack.callback(source, t);
            }
            targetList.add(t);
        }
        return targetList;
    }
}
