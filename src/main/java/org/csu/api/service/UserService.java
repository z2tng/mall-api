package org.csu.api.service;

import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.CheckAnswerUserDTO;
import org.csu.api.dto.RegisterUserDTO;
import org.csu.api.vo.UserVO;

public interface UserService {

    //用户登录
    public CommonResponse<UserVO> login(String username, String password);

    //校验用户相关字段
    public CommonResponse<String> checkField(String fieldName, String fieldValue);

    //用户注册
    public CommonResponse<String> register(RegisterUserDTO registerUserDTO);

    //获取用户设置的问题（用于重置密码）
    public CommonResponse<String> getForgetQuestion(String username);

    //检验回答是否正确
    public CommonResponse<String> checkForgetAnswer(String username, String question, String answer);

    //重置忘记密码
    public CommonResponse<String> resetForgetPassword(String username, String newPassword, String forgetToken);

    //登录状态下重置密码
    public CommonResponse<String> resetPassword(String username, String oldPassword, String newPassword);

    //更新用户信息
    public CommonResponse<String> update(User user);
}
