package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.persistence.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.csu.api.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public CommonResponse<User> getLoginUser(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("password", password);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return CommonResponse.createForErrorMessage("用户名或密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return CommonResponse.createForSuccess(user);
    }
}
