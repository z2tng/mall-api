package org.csu.api.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.NotBlank;
import org.csu.api.common.CommonResponse;
import org.csu.api.service.ProductService;
import org.csu.api.vo.ProductDetailVO;
import org.csu.api.vo.ProductListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("detail")
    public CommonResponse<ProductDetailVO> getProductDetail(@NotBlank(message = "商品ID不能为空") @RequestParam String productId) {
        return productService.getProductDetail(productId);
    }

    @GetMapping("list")
    public CommonResponse<Page<ProductListVO>> getProductList(@RequestParam(required = false) Integer categoryId,
                                                              @RequestParam(required = false) String keyword,
                                                              @RequestParam(defaultValue = "") String orderBy,
                                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return productService.getProductList(categoryId, keyword, orderBy, pageNum, pageSize);
    }


}
