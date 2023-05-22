package org.csu.api.service;

import org.csu.api.common.CommonResponse;
import org.csu.api.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    //获取单个分类的详情
    CommonResponse<CategoryVO> getCategory(Integer categoryId);

    //获取一个分类信息的一级子分类信息列表，不递归
    CommonResponse<List<CategoryVO>> getCategoryChildren(Integer categoryId);

    //获取一个分类信息的所有子分类信息列表，递归
    CommonResponse<List<Integer>> getCategoryAndAllChildren(Integer categoryId);

}
