package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.CartItem;
import org.csu.api.domain.Product;
import org.csu.api.persistence.CartItemMapper;
import org.csu.api.persistence.ProductMapper;
import org.csu.api.service.CartService;
import org.csu.api.utils.BigDecimalUtil;
import org.csu.api.utils.ImageServerConfig;
import org.csu.api.utils.ListBeanUtils;
import org.csu.api.vo.CartItemVO;
import org.csu.api.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Resource
    private ImageServerConfig imageServerConfig;


    @Override
    public CommonResponse<CartVO> addCart(Integer userId, Integer productId, Integer quantity) {
        //1. 校验商品是否存在和是否在售
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", productId).eq("status", CONSTANT.ProductStatus.ON_SALE.getCode());
        Product product = productMapper.selectOne(queryWrapper);
        if (product == null) {
            return CommonResponse.createForError("商品不存在或已下架");
        }

        //2. 查询该商品是否已经在购物车项中
        QueryWrapper<CartItem> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("user_id", userId).eq("product_id", productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper2);

        if (cartItem == null) {
            //若不存在则新增购物车项
            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setChecked(CONSTANT.CART_ITEM_STATUS.CHECKED);
            cartItem.setCreateTime(LocalDateTime.now());
            cartItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.insert(cartItem);
        } else {
            //若存在则更新数量
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", cartItem.getId())
                    .set("quantity", quantity)
                    .set("update_time", LocalDateTime.now());
            cartItemMapper.update(cartItem, updateWrapper);
        }

        CartVO cartVO = this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<CartVO> updateCart(Integer userId, Integer productId, Integer quantity) {
        //查询该商品是否已经在购物车项中
        QueryWrapper<CartItem> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("user_id", userId).eq("product_id", productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper2);

        if (cartItem != null) {
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", cartItem.getId())
                    .set("quantity", quantity)
                    .set("update_time", LocalDateTime.now());
            cartItemMapper.update(cartItem, updateWrapper);
            CartVO cartVO = this.getCartVOAndCheckStock(userId);
            return CommonResponse.createForSuccess(cartVO);
        }
        return CommonResponse.createForError(ResponseCode.ARGUMENT_INVALID.getCode(), ResponseCode.ARGUMENT_INVALID.getDescription());
    }

    @Override
    public CommonResponse<CartVO> deleteCart(Integer userId, String productIds) {
        List<String> productIdList = Lists.newArrayList(StringUtils.split(productIds, ","));

        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        for (String productId : productIdList) {
            queryWrapper.clear();
            queryWrapper.eq("user_id", userId).eq("product_id", Integer.parseInt(productId));
            cartItemMapper.delete(queryWrapper);
        }
        CartVO cartVO = this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<CartVO> listCart(Integer userId) {
        CartVO cartVO = this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<CartVO> setAllChecked(Integer userId) {
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .set("checked", CONSTANT.CART_ITEM_STATUS.CHECKED)
                .set("update_time", LocalDateTime.now());
        cartItemMapper.update(null, updateWrapper);
        CartVO cartVO = this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<CartVO> setAllUnchecked(Integer userId) {
        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId)
                .set("checked", CONSTANT.CART_ITEM_STATUS.UNCHECKED)
                .set("update_time", LocalDateTime.now());
        cartItemMapper.update(null, updateWrapper);
        CartVO cartVO = this.getCartVOAndCheckStock(userId);
        return CommonResponse.createForSuccess(cartVO);
    }

    @Override
    public CommonResponse<CartVO> setCartItemChecked(Integer userId, Integer productId) {
        //查询该商品是否已经在购物车项中
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("product_id", productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);

        if (cartItem != null) {
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", cartItem.getId())
                    .set("checked", CONSTANT.CART_ITEM_STATUS.CHECKED)
                    .set("update_time", LocalDateTime.now());
            cartItemMapper.update(cartItem, updateWrapper);
            CartVO cartVO = this.getCartVOAndCheckStock(userId);
            return CommonResponse.createForSuccess(cartVO);
        }
        return CommonResponse.createForError(ResponseCode.ARGUMENT_INVALID.getCode(), ResponseCode.ARGUMENT_INVALID.getDescription());
    }

    @Override
    public CommonResponse<CartVO> setCartItemUnchecked(Integer userId, Integer productId) {
        //查询该商品是否已经在购物车项中
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("product_id", productId);
        CartItem cartItem = cartItemMapper.selectOne(queryWrapper);

        if (cartItem != null) {
            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", cartItem.getId())
                    .set("checked", CONSTANT.CART_ITEM_STATUS.UNCHECKED)
                    .set("update_time", LocalDateTime.now());
            cartItemMapper.update(cartItem, updateWrapper);
            CartVO cartVO = this.getCartVOAndCheckStock(userId);
            return CommonResponse.createForSuccess(cartVO);
        }
        return CommonResponse.createForError(ResponseCode.ARGUMENT_INVALID.getCode(), ResponseCode.ARGUMENT_INVALID.getDescription());
    }

    @Override
    public CommonResponse<Integer> getCartCount(Integer userId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Long count = cartItemMapper.selectCount(queryWrapper);
        return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), count.intValue());
    }


    private CartVO getCartVOAndCheckStock(Integer userId) {
        CartVO cartVO = new CartVO();

        //1. 查询购物车项列表
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<CartItem> cartItemList = cartItemMapper.selectList(queryWrapper);

        List<CartItemVO> cartItemVOList = Lists.newArrayList();
        AtomicReference<BigDecimal> cartTotalPrice = new AtomicReference<>(new BigDecimal("0"));
        AtomicReference<Boolean> allChecked = new AtomicReference<>(true);

        if (CollectionUtils.isNotEmpty(cartItemList)) {
            cartItemVOList = ListBeanUtils.copyProperties(cartItemList, CartItemVO::new, (cartItem, cartItemVO) -> {
                Product product = productMapper.selectById(cartItem.getProductId());
                if (product != null) {
                    cartItemVO.setProductName(product.getName());
                    cartItemVO.setProductSubtitle(product.getSubtitle());
                    cartItemVO.setProductPrice(product.getPrice());
                    cartItemVO.setProductStock(product.getStock());
                    cartItemVO.setProductMainImage(product.getMainImage());

                    //判断库存
                    if (cartItem.getQuantity() <= product.getStock()) {
                        cartItemVO.setQuantity(cartItem.getQuantity());
                        cartItemVO.setCheckStock(true);
                    } else {
                        cartItemVO.setQuantity(product.getStock());
                        CartItem updateCartItem = new CartItem();
                        UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id", cartItem.getId())
                                .set("quantity", cartItemVO.getQuantity())
                                .set("update_time", LocalDateTime.now());
                        cartItemMapper.update(updateCartItem, updateWrapper);

                        cartItemVO.setCheckStock(false);
                    }

                    cartItemVO.setProductTotalPrice(BigDecimalUtil.multiply(cartItemVO.getProductPrice().doubleValue(), cartItemVO.getQuantity()));
                }

                if (cartItem.getChecked() == CONSTANT.CART_ITEM_STATUS.CHECKED) {
                    cartTotalPrice.set(BigDecimalUtil.add(cartTotalPrice.get().doubleValue(), cartItemVO.getProductTotalPrice().doubleValue()));
                } else {
                    allChecked.set(false);
                }
            });
        }

        cartVO.setCartItemVOList(cartItemVOList);
        cartVO.setCartTotalPrice(cartTotalPrice.get());
        cartVO.setAllChecked(allChecked.get());
        cartVO.setProductImageServer(imageServerConfig.getUrl());
        return cartVO;
    }
}
