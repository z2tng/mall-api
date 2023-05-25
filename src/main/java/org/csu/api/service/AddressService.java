package org.csu.api.service;

import org.csu.api.common.CommonResponse;
import org.csu.api.dto.PostAddressDTO;
import org.csu.api.dto.UpdateAddressDTO;
import org.csu.api.vo.AddressVO;

import java.util.List;

public interface AddressService {

    // 新增地址
    CommonResponse<AddressVO> addAddress(Integer userId, PostAddressDTO postAddressDTO);

    // 删除地址
    CommonResponse<String> deleteAddress(Integer userId, Integer addressId);

    // 修改地址
    CommonResponse<AddressVO> updateAddress(Integer userId, UpdateAddressDTO updateAddressDTO);

    // 查询单个地址
    CommonResponse<AddressVO> findAddress(Integer userId, Integer addressId);

    // 查询用户地址列表
    CommonResponse<List<AddressVO>> listAddress(Integer userId);
}
