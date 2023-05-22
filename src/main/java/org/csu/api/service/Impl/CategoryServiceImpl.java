package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.Category;
import org.csu.api.persistence.CategoryMapper;
import org.csu.api.service.CategoryService;
import org.csu.api.utils.ListBeanUtils;
import org.csu.api.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CommonResponse<CategoryVO> getCategory(Integer categoryId) {
        if (categoryId == null) {
            return CommonResponse.createForError("查询分类时，分类id不能为空");
        }

        if (categoryId == CONSTANT.CATEGORY_ROOT) {
            return CommonResponse.createForError("根分类无信息");
        }

        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            return CommonResponse.createForError("查询的分类不存在");
        }
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return CommonResponse.createForSuccess(categoryVO);
    }

    @Override
    public CommonResponse<List<CategoryVO>> getCategoryChildren(Integer categoryId) {
        if (categoryId == null) {
            return CommonResponse.createForError("查询分类时，分类id不能为空");
        }

        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", categoryId);
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(categoryList)) {
            return CommonResponse.createForError("未找到当前分类的子分类");
        }
        List<CategoryVO> categoryVOList = ListBeanUtils.copyProperties(categoryList, CategoryVO::new);
        return CommonResponse.createForSuccess(categoryVOList);
    }

    @Override
    public CommonResponse<List<Integer>> getCategoryAndAllChildren(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        List<Integer> categoryIdList = Lists.newArrayList();

        //校验参数
        if (categoryId == null) {
            return CommonResponse.createForSuccess(categoryIdList);
        }

        findAllChildren(categoryId, categorySet);

        for (Category categoryItem : categorySet) {
            categoryIdList.add(categoryItem.getId());
        }
        return CommonResponse.createForSuccess(categoryIdList);
    }

    private Set<Category> findAllChildren(Integer categoryId, Set<Category> categorySet) {
        Category category = categoryMapper.selectById(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", categoryId);
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);
        for (Category categoryItem : categoryList) {
            findAllChildren(categoryItem.getId(), categorySet);
        }
        return categorySet;
    }
}
