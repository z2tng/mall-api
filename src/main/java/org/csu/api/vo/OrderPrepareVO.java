package org.csu.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderPrepareVO {
    private List<OrderItemVO> orderItemVOList;
    private BigDecimal paymentPrice;
    private String imageServer;
}
