package org.csu.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostAddressDTO {
    @NotBlank(message = "收件人不能为空")
    private String addressName;
    private String addressPhone;
    @NotBlank(message = "手机号码不能为空")
    private String addressMobile;
    @NotBlank(message = "省份不能为空")
    private String addressProvince;
    @NotBlank(message = "城市不能为空")
    private String addressCity;
    @NotBlank(message = "区县不能为空")
    private String addressDistrict;
    @NotBlank(message = "详细地址不能为空")
    private String addressDetail;
    private String addressZip;
}
