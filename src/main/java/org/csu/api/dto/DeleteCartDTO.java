package org.csu.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteCartDTO {
    @NotNull(message = "商品id不能为空")
    private String productIds;
}
