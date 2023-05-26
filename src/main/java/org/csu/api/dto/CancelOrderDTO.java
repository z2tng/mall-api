package org.csu.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelOrderDTO {
    @NotNull(message = "订单号不能为空")
    private Long orderNo;
}
