package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.RegisterUserDTO;
import org.csu.api.persistence.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.csu.api.service.UserService;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public CommonResponse<User> login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            user.setPassword(StringUtils.EMPTY);
            return CommonResponse.createForSuccess("登录成功", user);
        }
        return CommonResponse.createForError("用户名或密码错误");
    }

    @Override
    public CommonResponse<String> checkField(String fieldName, String fieldValue) {
        if (StringUtils.equals(fieldName, CONSTANT.USER_FIELD.USERNAME)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", fieldValue);
            long rows = userMapper.selectCount(queryWrapper);
            if (rows > 0) {
                return  CommonResponse.createForError("用户名已存在");
            }
        } else if (StringUtils.equals(fieldName, CONSTANT.USER_FIELD.EMAIL)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", fieldValue);
            long rows = userMapper.selectCount(queryWrapper);
            if (rows > 0) {
                return  CommonResponse.createForError("邮箱已存在");
            }
        } else if (StringUtils.equals(fieldName, CONSTANT.USER_FIELD.PHONE)) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("phone", fieldValue);
            long rows = userMapper.selectCount(queryWrapper);
            if (rows > 0) {
                return  CommonResponse.createForError("电话号码已存在");
            }
        } else {
            return CommonResponse.createForError("参数错误");
        }
        return CommonResponse.createForSuccess();
    }

    @Override
    public CommonResponse<String> register(RegisterUserDTO registerUserDTO) {
        CommonResponse<String> checkResult = checkField(CONSTANT.USER_FIELD.USERNAME, registerUserDTO.getUsername());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        checkResult = checkField(CONSTANT.USER_FIELD.EMAIL, registerUserDTO.getEmail());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }
        checkResult = checkField(CONSTANT.USER_FIELD.PHONE, registerUserDTO.getPhone());
        if (!checkResult.isSuccess()) {
            return checkResult;
        }

        User user = new User();
        BeanUtils.copyProperties(registerUserDTO, user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRole(CONSTANT.ROLE.CUSTOMER);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        int rows = userMapper.insert(user);
        if (rows == 0) {
            return CommonResponse.createForError("注册用户失败");
        }
        return CommonResponse.createForSuccess("注册用户成功");
    }
}
