package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.Address;
import org.csu.api.dto.PostAddressDTO;
import org.csu.api.dto.UpdateAddressDTO;
import org.csu.api.persistence.AddressMapper;
import org.csu.api.service.AddressService;
import org.csu.api.utils.ListBeanUtils;
import org.csu.api.vo.AddressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public CommonResponse<AddressVO> addAddress(Integer userId, PostAddressDTO postAddressDTO) {
        Address address = new Address();
        BeanUtils.copyProperties(postAddressDTO, address);
        address.setUserId(userId);
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());

        int result = addressMapper.insert(address);
        if (result == 0) {
            CommonResponse.createForError("服务器异常，添加地址失败");
        }

        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address, addressVO);
        return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), addressVO);
    }

    @Override
    public CommonResponse<String> deleteAddress(Integer userId, Integer addressId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", addressId)
                .eq("user_id", userId);
        int result = addressMapper.delete(queryWrapper);
        if (result == 0) {
            return CommonResponse.createForError("该地址不存在");
        }
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<AddressVO> updateAddress(Integer userId, UpdateAddressDTO updateAddressDTO) {
        // 校验地址是否存在
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", updateAddressDTO.getId())
                .eq("user_id", userId);
        Address address = addressMapper.selectOne(queryWrapper);

        if (address != null) {
            // 若存在则更新地址
            BeanUtils.copyProperties(updateAddressDTO, address);
            UpdateWrapper<Address> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", updateAddressDTO.getId())
                    .eq("user_id", userId)
                    .set("update_time", LocalDateTime.now());
            int result = addressMapper.update(address, updateWrapper);
            if (result == 0) {
                return CommonResponse.createForError("服务器异常，更新地址失败");
            }

            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(address, addressVO);
            return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), addressVO);
        }
        return CommonResponse.createForError("该地址不存在");
    }

    @Override
    public CommonResponse<AddressVO> findAddress(Integer userId, Integer addressId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", addressId)
                .eq("user_id", userId);
        Address address = addressMapper.selectOne(queryWrapper);
        if (address != null) {
            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(address, addressVO);
            return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), addressVO);
        }
        return CommonResponse.createForError("该地址不存在");
    }

    @Override
    public CommonResponse<List<AddressVO>> listAddress(Integer userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Address> addressList = addressMapper.selectList(queryWrapper);
        List<AddressVO> addressVOList = ListBeanUtils.copyProperties(addressList, AddressVO::new);
        return CommonResponse.createForSuccess(ResponseCode.SUCCESS.getDescription(), addressVOList);
    }
}
