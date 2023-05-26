package org.csu.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDTO {
    @NotNull(message = "地址id不能为空")
    private Integer addressId;
}
