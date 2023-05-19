package org.csu.api.controller.front;

import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.CheckUserFieldDTO;
import org.csu.api.dto.LoginUserDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.csu.api.dto.RegisterUserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.csu.api.service.UserService;
import org.springframework.web.service.annotation.PostExchange;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/login")
    public CommonResponse<User> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO,
                                          HttpSession session) {
        CommonResponse<User> result = userService.login(loginUserDTO.getUsername(), loginUserDTO.getPassword());
        if (result.isSuccess()) {
            session.setAttribute("loginUser", result.getData());
        }
        return result;
    }

    @PostMapping("/user/check_field")
    public CommonResponse<String> checkField(@Valid @RequestBody CheckUserFieldDTO checkUserFieldDTO) {
        return userService.checkField(checkUserFieldDTO.getFieldName(), checkUserFieldDTO.getFieldValue());
    }

    @PostMapping("/register")
    public CommonResponse<String> register(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        return userService.register(registerUserDTO);
    }

}
