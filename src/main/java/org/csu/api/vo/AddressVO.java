package org.csu.api.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressVO {
    private Integer id;
    private Integer userId;
    private String addressName;
    private String addressPhone;
    private String addressMobile;
    private String addressProvince;
    private String addressCity;
    private String addressDistrict;
    private String addressDetail;
    private String addressZip;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
