package org.csu.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csu.api.common.CommonResponse;
import org.csu.api.vo.OrderPrepareVO;
import org.csu.api.vo.OrderVO;

import java.math.BigInteger;

public interface OrderService {

    //创建订单
    CommonResponse<OrderVO> createOrder(Integer userId, Integer addressId);

    //获取购物车中选中的商品列表
    CommonResponse<OrderPrepareVO> listCartItem(Integer userId);

    //获取订单详情
    CommonResponse<OrderVO> getOrderDetail(BigInteger orderNo, Integer userId);

    //获取订单列表
    CommonResponse<Page<OrderVO>> listOrder(Integer userId, Integer pageNum, Integer pageSize);

    //取消订单
    CommonResponse<String> cancelOrder(Long orderNo, Integer userId);
}
