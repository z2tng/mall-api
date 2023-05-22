package org.csu.api.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.api.common.CommonResponse;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;

public interface ProductService {

    CommonResponse<ProductDetailVO> getProductDetail(String productId);

    CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId, String keyword, String orderBy, Integer pageNum, Integer pageSize);
}
