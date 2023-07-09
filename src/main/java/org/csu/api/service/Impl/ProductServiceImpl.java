package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.Category;
import org.csu.api.domain.Product;
import org.csu.api.persistence.CategoryMapper;
import org.csu.api.persistence.ProductMapper;
import org.csu.api.service.CategoryService;
import org.csu.api.service.ProductService;
import org.csu.api.utils.ImageServerConfig;
import org.csu.api.utils.ListBeanUtilsForPage;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productService")
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Resource
    private ImageServerConfig imageServerConfig;

    @Override
    public CommonResponse<ProductDetailVO> getProductDetail(String productId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", productId);
        Product product = productMapper.selectOne(queryWrapper);
        if (product == null) {
            return CommonResponse.createForError("产品不存在或已删除");
        }

        if (product.getStatus() != CONSTANT.ProductStatus.ON_SALE.getCode()) {
            return CommonResponse.createForError("产品不在售，下架或其他情况");
        }

        ProductDetailVO productDetailVO = this.productToDetailVO(product);
        return CommonResponse.createForSuccess(productDetailVO);
    }

    @Override
    public CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId, String keyword, String orderBy, Integer pageNum, Integer pageSize) {
        //1. 校验字段
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return CommonResponse.createForError(ResponseCode.ARGUMENT_INVALID.getCode(), ResponseCode.ARGUMENT_INVALID.getDescription());
        }
        if (categoryId != null) {
            Category category = categoryMapper.selectById(categoryId);
            //没有该分类，且没有关键字，这时候输出日志，并返回一个空的结果集，不报错
            if (category == null && StringUtils.isBlank(keyword)) {
                log.info("没有该分类，且没有关键字，categoryId = {}", categoryId);
                return CommonResponse.createForSuccessMessage("没有查到商品信息");
            }

        }

        Page<Product> result = new Page<>();
        result.setCurrent(pageNum);
        result.setSize(pageSize);

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();

        //2. 拼接查询条件
        List<Integer> categoryIdList = categoryService.getCategoryAndAllChildren(categoryId).getData();
        if (categoryIdList.size() != 0) {
            queryWrapper.in("category_id", categoryIdList);
        }
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("name", "%" + keyword + "%");
        }

        //3. 添加排序条件
        if (StringUtils.isNotBlank(orderBy)) {
            if (StringUtils.equals(orderBy, CONSTANT.PRODUCT_ORDER_BY_PRICE_ASC)) {
                queryWrapper.orderByAsc("price");
            } else if (StringUtils.equals(orderBy, CONSTANT.PRODUCT_ORDER_BY_PRICE_DESC)) {
                queryWrapper.orderByDesc("price");
            }
        }

        //4. 执行查询
        result = productMapper.selectPage(result, queryWrapper);

        //5. 封装结果集
        Page<ProductListVO> productListVOPage = ListBeanUtilsForPage.copyPageList(result, ProductListVO::new, (product, productListVO) -> productListVO.setImageServer(imageServerConfig.getUrl()));
        return CommonResponse.createForSuccess(productListVOPage);
    }

    private ProductDetailVO productToDetailVO(Product product) {
        //1. 复制product对象属性
        ProductDetailVO productDetailVO = new ProductDetailVO();
        BeanUtils.copyProperties(product, productDetailVO);

        //2. 搜索父节点并放入到VO对象中
        Category category = categoryMapper.selectById(product.getCategoryId());
        productDetailVO.setParentCategoryId(category.getParentId());

        //3. 将ImageServer地址放入到VO对象中
        productDetailVO.setImageServer(imageServerConfig.getUrl());
        return productDetailVO;
    }
}
