package org.csu.api.controller.front;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.Address;
import org.csu.api.dto.PostAddressIdDTO;
import org.csu.api.dto.PostAddressDTO;
import org.csu.api.dto.UpdateAddressDTO;
import org.csu.api.service.AddressService;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address/")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping("add")
    public CommonResponse<Address> addAddress(@Valid @RequestBody PostAddressDTO postAddressDTO,
                                              HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return addressService.addAddress(loginUser.getId(), postAddressDTO);
    }

    @PostMapping("delete")
    public CommonResponse<String> deleteAddress(@Valid @RequestBody PostAddressIdDTO postAddressIdDTO,
                                              HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return addressService.deleteAddress(loginUser.getId(), postAddressIdDTO.getAddressId());
    }

    @PostMapping("update")
    public CommonResponse<Address> updateAddress(@Valid @RequestBody UpdateAddressDTO updateAddressDTO,
                                                HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return addressService.updateAddress(loginUser.getId(), updateAddressDTO);
    }

    @PostMapping("find")
    public CommonResponse<Address> findAddress(@Valid @RequestBody PostAddressIdDTO postAddressIdDTO,
                                                HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return addressService.findAddress(loginUser.getId(), postAddressIdDTO.getAddressId());
    }

    @PostMapping("list")
    public CommonResponse<List<Address>> deleteAddress(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return addressService.listAddress(loginUser.getId());
    }
}
