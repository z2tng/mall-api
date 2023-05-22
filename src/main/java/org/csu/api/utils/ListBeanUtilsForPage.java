package org.csu.api.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Supplier;

public class ListBeanUtilsForPage extends BeanUtils {

    public static <S, T> Page<T> copyProperties(Page<S> sourcePage, Supplier<T> target) {
        return copyProperties(sourcePage, target, (ListBeanUtilsCallBack<S, T>) null);
    }

    public static <S, T> Page<T> copyProperties(Page<S> sourcePage, Supplier<T> target, ListBeanUtilsCallBack<S, T> callBack) {
        Page<T> targetPage = new Page<>();
        targetPage.setCurrent(sourcePage.getCurrent());
        targetPage.setSize(sourcePage.getSize());
        targetPage.setTotal(sourcePage.getTotal());
        List<S> sourceList = sourcePage.getRecords();
        List<T> targetList = ListBeanUtils.copyProperties(sourceList, target, callBack);

        targetPage.setRecords(targetList);
        return targetPage;
    }
}
