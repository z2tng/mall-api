package org.csu.api.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.dto.CancelOrderDTO;
import org.csu.api.dto.CreateOrderDTO;
import org.csu.api.service.OrderService;
import org.csu.api.vo.OrderPrepareVO;
import org.csu.api.vo.OrderVO;
import org.csu.api.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("create")
    public CommonResponse<OrderVO> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO,
                                               HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return orderService.createOrder(loginUser.getId(), createOrderDTO.getAddressId());
    }

    @GetMapping("cart_item_list")
    public CommonResponse<OrderPrepareVO> listCartItem(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return orderService.listCartItem(loginUser.getId());
    }

    @GetMapping("detail")
    public CommonResponse<OrderVO> getOrderDetail(@RequestParam @NotBlank(message = "订单号不能为空") Long orderNo,
                                                         HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return orderService.getOrderDetail(orderNo, loginUser.getId());
    }

    @GetMapping("list")
    public CommonResponse<Page<OrderVO>> listOrder(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return orderService.listOrder(loginUser.getId(), pageNum, pageSize);
    }

    @PostMapping("cancel")
    public CommonResponse<String> cancelOrder(@Valid @RequestBody CancelOrderDTO cancelOrderDTO,
                                              HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser == null) {
            return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
        }

        return orderService.cancelOrder(cancelOrderDTO.getOrderNo(), loginUser.getId());
    }



}
