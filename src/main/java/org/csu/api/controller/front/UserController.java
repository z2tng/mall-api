package org.csu.api.controller.front;

import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.csu.api.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/login")
    public CommonResponse<User> loginUser(@Valid @RequestBody UserDTO userDTO,
                                          HttpSession session) {
        CommonResponse<User> result = userService.getLoginUser(userDTO.getUsername(), userDTO.getPassword());
        if (result.isSuccess()) {
            session.setAttribute("loginUser", result.getData());
        }
        return result;
    }

}
