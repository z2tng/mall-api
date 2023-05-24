package org.csu.api.controller.front;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.dto.DeleteCartDTO;
import org.csu.api.dto.PostCartDTO;
import org.csu.api.service.CartService;
import org.csu.api.vo.CartVO;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("add")
    public CommonResponse<CartVO> addCart(@Valid @RequestBody PostCartDTO postCartDTO,
                                              HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.addCart(loginUser.getId(), postCartDTO.getProductId(), postCartDTO.getQuantity());
    }

    @PostMapping("update")
    public CommonResponse<CartVO> updateCart(@Valid @RequestBody PostCartDTO postCartDTO,
                                         HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.updateCart(loginUser.getId(), postCartDTO.getProductId(), postCartDTO.getQuantity());
    }

    @PostMapping("delete")
    public CommonResponse<CartVO> deleteCart(@Valid @RequestBody DeleteCartDTO deleteCartDTO,
                                         HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.deleteCart(loginUser.getId(), deleteCartDTO.getProductIds());
    }

    @PostMapping("list")
    public CommonResponse<CartVO> list(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.listCart(loginUser.getId());
    }

    @PostMapping("set_all_checked")
    public CommonResponse<CartVO> setAllChecked(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.setAllChecked(loginUser.getId());
    }

    @PostMapping("set_all_unchecked")
    public CommonResponse<CartVO> setAllUnchecked(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.setAllUnchecked(loginUser.getId());
    }

    @PostMapping("set_cart_item_checked")
    public CommonResponse<CartVO> setCartItemChecked(@Valid @RequestBody PostCartDTO postCartDTO,
                                                     HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.setCartItemChecked(loginUser.getId(), postCartDTO.getProductId());
    }

    @PostMapping("set_cart_item_unchecked")
    public CommonResponse<CartVO> setCartItemUnchecked(@Valid @RequestBody PostCartDTO postCartDTO,
                                                       HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.setCartItemUnchecked(loginUser.getId(), postCartDTO.getProductId());
    }

    @PostMapping("get_cart_count")
    public CommonResponse<Integer> getCartCount(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return cartService.getCartCount(loginUser.getId());
    }


}
