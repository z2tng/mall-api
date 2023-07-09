package org.csu.api.controller.front;

import jakarta.validation.constraints.NotBlank;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.common.ResponseCode;
import org.csu.api.domain.User;
import org.csu.api.dto.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.csu.api.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.csu.api.service.UserService;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public CommonResponse<UserVO> loginUser(@Valid @RequestBody LoginUserDTO loginUserDTO,
                                            HttpSession session) {
        CommonResponse<UserVO> result = userService.login(loginUserDTO.getUsername(), loginUserDTO.getPassword());
        if (result.isSuccess()) {
            session.setAttribute(CONSTANT.LOGIN_USER, result.getData());
        }
        return result;
    }

    @PostMapping("check_field")
    public CommonResponse<String> checkField(@Valid @RequestBody CheckUserFieldDTO checkUserFieldDTO) {
        return userService.checkField(checkUserFieldDTO.getFieldName(), checkUserFieldDTO.getFieldValue());
    }

    @PostMapping("register")
    public CommonResponse<String> register(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        return userService.register(registerUserDTO);
    }

    @PostMapping("get_user_detail")
    public CommonResponse<Object> getUserDetail(HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser != null) {
            return CommonResponse.createForSuccess(loginUser);
        }
        return CommonResponse.createForError("用户未登录");
    }

    @PostMapping("get_forget_question")
    public CommonResponse<String> getForgetQuestion(@RequestParam @NotBlank(message = "用户名不能为空") String username) {
        return userService.getForgetQuestion(username);
    }

    @PostMapping("check_forget_answer")
    public CommonResponse<String> checkForgetAnswer(@Valid @RequestBody CheckAnswerUserDTO checkAnswerUserDTO) {
        return userService.checkForgetAnswer(checkAnswerUserDTO.getUsername(), checkAnswerUserDTO.getQuestion(), checkAnswerUserDTO.getAnswer());
    }

    @PostMapping("reset_forget_password")
    public CommonResponse<String> resetForgetPassword(@Valid @RequestBody ResetForgetPasswordDTO resetForgetPasswordDTO) {
        return userService.resetForgetPassword(resetForgetPasswordDTO.getUsername(), resetForgetPasswordDTO.getNewPassword(), resetForgetPasswordDTO.getForgetToken());
    }

    @PostMapping("reset_password")
    public CommonResponse<String> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO,
                                                HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser != null) {
            return userService.resetPassword(loginUser.getUsername(), resetPasswordDTO.getOldPassword(), resetPasswordDTO.getNewPassword());
        }
        return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
    }

    @PostMapping("update_user")
    public CommonResponse<String> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO,
                                             HttpSession session) {
        UserVO loginUser = (UserVO) session.getAttribute(CONSTANT.LOGIN_USER);
        if (loginUser != null) {
            if (StringUtils.equals(loginUser.getUsername(), updateUserDTO.getUsername())) {
                // 更新session中的用户信息
                BeanUtils.copyProperties(updateUserDTO, loginUser);

                User user = new User();
                BeanUtils.copyProperties(updateUserDTO, user);
                return userService.update(user);
            }
            return CommonResponse.createForError("用户只能修改自己账号的信息");
        }
        return CommonResponse.createForError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDescription());
    }

    @PostMapping("logout")
    public CommonResponse<String> logout(HttpSession session) {
        session.removeAttribute(CONSTANT.LOGIN_USER);
        return CommonResponse.createForSuccess();
    }

}
