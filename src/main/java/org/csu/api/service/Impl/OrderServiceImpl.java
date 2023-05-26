package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.*;
import org.csu.api.persistence.*;
import org.csu.api.service.CartService;
import org.csu.api.service.OrderService;
import org.csu.api.utils.ImageServerConfig;
import org.csu.api.utils.ListBeanUtils;
import org.csu.api.utils.ListBeanUtilsForPage;
import org.csu.api.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CartService cartService;

    @Resource
    private ImageServerConfig imageServerConfig;

    @Override
    public CommonResponse<OrderVO> createOrder(Integer userId, Integer addressId) {
        //1. 校验地址
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            return CommonResponse.createForError("地址不存在，创建订单失败");
        }

        //2. 校验购物车中选中的商品列表
        CartVO cartVO = cartService.listCart(userId).getData();
        cartVO.getCartItemVOList().removeIf(cartItemVO -> cartItemVO.getChecked() == CONSTANT.CART_ITEM_STATUS.UNCHECKED);
        if (CollectionUtils.isEmpty(cartVO.getCartItemVOList())) {
            return CommonResponse.createForError("购物车为空");
        }

        //3. 商品库存校验
        if (!hasStock(cartVO.getCartItemVOList())) {
            return CommonResponse.createForError("商品库存不足，创建订单失败");
        }

        //4. 创建订单
        Order order = new Order();
        int orderNo = UUID.randomUUID().hashCode();
        orderNo = orderNo < 0 ? -orderNo : orderNo;
        order.setOrderNo((long) orderNo);
        order.setUserId(userId);
        order.setAddressId(address.getId());
        order.setPaymentPrice(cartVO.getCartTotalPrice());
        order.setPaymentType(CONSTANT.PAYMENT_TYPE.ALIPAY);
        order.setPostage(0);
        order.setStatus(CONSTANT.OrderStatus.NOT_PAID.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        int result = orderMapper.insert(order);
        if (result == 0) {
            return CommonResponse.createForError("服务器异常，创建订单失败");
        }

        for (CartItemVO cartItemVO : cartVO.getCartItemVOList()) {
            // 去除库存
            Product product = productMapper.selectById(cartItemVO.getProductId());
            product.setStock(product.getStock() - cartItemVO.getQuantity());
            result = productMapper.updateById(product);
            if (result == 0) {
                return CommonResponse.createForError("服务器异常，创建订单失败");
            }

            // 生成订单项数据
            OrderItem orderItem = new OrderItem();
            BeanUtils.copyProperties(cartItemVO, orderItem);
            orderItem.setOrderNo(order.getOrderNo());
            orderItem.setUserId(userId);
            orderItem.setProductImage(cartItemVO.getProductMainImage());
            orderItem.setCurrentPrice(cartItemVO.getProductPrice());
            orderItem.setTotalPrice(cartItemVO.getProductTotalPrice());
            orderItem.setCreateTime(LocalDateTime.now());
            orderItem.setUpdateTime(LocalDateTime.now());
            result = orderItemMapper.insert(orderItem);
            if (result == 0) {
                return CommonResponse.createForError("服务器异常，创建订单失败");
            }

            // 删除购物车项
            cartItemMapper.deleteById(cartItemVO.getId());
        }

        OrderVO orderVO = this.getOrderVO(order.getOrderNo(), userId);
        return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), orderVO);
    }

    @Override
    public CommonResponse<OrderPrepareVO> listCartItem(Integer userId) {
        //校验购物车中选中的商品列表
        CartVO cartVO = cartService.listCart(userId).getData();
        cartVO.getCartItemVOList().removeIf(cartItemVO -> cartItemVO.getChecked() == CONSTANT.CART_ITEM_STATUS.UNCHECKED);
        if (CollectionUtils.isEmpty(cartVO.getCartItemVOList())) {
            return CommonResponse.createForError("购物车为空");
        }

        OrderPrepareVO orderPrepareVO = new OrderPrepareVO();
        List<OrderItemVO> orderItemVOList = ListBeanUtils.copyProperties(cartVO.getCartItemVOList(), OrderItemVO::new, (cartItemVO, orderItemVO) -> {
            BeanUtils.copyProperties(cartItemVO, orderItemVO);
            orderItemVO.setProductImage(cartItemVO.getProductMainImage());
            orderItemVO.setCurrentPrice(cartItemVO.getProductPrice());
            orderItemVO.setTotalPrice(cartItemVO.getProductTotalPrice());
        });
        orderPrepareVO.setOrderItemVOList(orderItemVOList);
        orderPrepareVO.setImageServer(imageServerConfig.getUrl());
        orderPrepareVO.setPaymentPrice(cartVO.getCartTotalPrice());

        return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), orderPrepareVO);
    }

    @Override
    public CommonResponse<OrderVO> getOrderDetail(Long orderNo, Integer userId) {
        OrderVO orderVO = this.getOrderVO(orderNo, userId);
        return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), orderVO);
    }

    @Override
    public CommonResponse<Page<OrderVO>> listOrder(Integer userId, Integer pageNum, Integer pageSize) {
        Page<Order> result = new Page<>(pageNum, pageSize);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time");
        result = orderMapper.selectPage(result, queryWrapper);

        Page<OrderVO> orderVOPage = ListBeanUtilsForPage.copyProperties(result, OrderVO::new, (order, orderVO) -> {
            BeanUtils.copyProperties(order, orderVO);
            // 地址信息
            Address address = addressMapper.selectById(order.getAddressId());
            if (address != null) {
                AddressVO addressVO = new AddressVO();
                BeanUtils.copyProperties(address, addressVO);
            }
            // 订单项信息
            QueryWrapper<OrderItem> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("order_no", order.getOrderNo());
            List<OrderItem> orderItemList = orderItemMapper.selectList(queryWrapper2);
            if (CollectionUtils.isNotEmpty(orderItemList)) {
                List<OrderItemVO> orderItemVOList = ListBeanUtils.copyProperties(orderItemList, OrderItemVO::new);
                orderVO.setOrderItemVOList(orderItemVOList);
            }

            orderVO.setImageServer(imageServerConfig.getUrl());
        });
        return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), orderVOPage);
    }

    @Override
    public CommonResponse<String> cancelOrder(Long orderNo, Integer userId) {
        //1. 校验订单是否存在
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo)
                .eq("user_id", userId);
        Order order = orderMapper.selectOne(queryWrapper);
        if (order == null) {
            return CommonResponse.createForError("订单不存在");
        }

        //2. 校验订单状态
        if (order.getStatus() != CONSTANT.OrderStatus.NOT_PAID.getCode()) {
            return CommonResponse.createForError("订单不是未支付状态，不能取消");
        }

        //3. 取消订单
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_no", orderNo)
                .set("status", CONSTANT.OrderStatus.CANCELED.getCode())
                .set("update_time", LocalDateTime.now());
        int result = orderMapper.update(order, updateWrapper);
        if (result == 0) {
            return CommonResponse.createForError("服务器异常，取消订单失败");
        }
        return CommonResponse.createForSuccess();
    }

    private boolean hasStock(List<CartItemVO> cartItemVOList) {
        for (CartItemVO cartItemVO : cartItemVOList) {
            if (cartItemVO.getQuantity() > productMapper.selectById(cartItemVO.getProductId()).getStock()) {
                return false;
            }
        }
        return true;
    }

    private OrderVO getOrderVO(Long orderNo, Integer userId) {
        OrderVO orderVO = new OrderVO();

        // 订单信息
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo)
                .eq("user_id", userId);
        Order order = orderMapper.selectOne(queryWrapper);
        if (order != null) {
            BeanUtils.copyProperties(order, orderVO);

            // 地址信息
            Address address = addressMapper.selectById(order.getAddressId());
            if (address != null) {
                AddressVO addressVO = new AddressVO();
                BeanUtils.copyProperties(address, addressVO);
                orderVO.setAddressVO(addressVO);
            }

            QueryWrapper<OrderItem> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("order_no", orderNo);
            List<OrderItem> orderItemList = orderItemMapper.selectList(queryWrapper2);
            if (CollectionUtils.isNotEmpty(orderItemList)) {
                List<OrderItemVO> orderItemVOList = ListBeanUtils.copyProperties(orderItemList, OrderItemVO::new);
                orderVO.setOrderItemVOList(orderItemVOList);
            }
        }

        orderVO.setImageServer(imageServerConfig.getUrl());
        return orderVO;
    }
}
