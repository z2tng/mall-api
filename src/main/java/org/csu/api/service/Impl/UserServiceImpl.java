package org.csu.api.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csu.api.common.CONSTANT;
import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.RegisterUserDTO;
import org.csu.api.dto.UpdateUserDTO;
import org.csu.api.persistence.UserMapper;
import org.csu.api.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.csu.api.service.UserService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private Cache<String, String> localCache;

    @Override
    public CommonResponse<UserVO> login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVO.setPassword(StringUtils.EMPTY);
            return CommonResponse.createForSuccess("登录成功", userVO);
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

    @Override
    public CommonResponse<String> getForgetQuestion(String username) {
        CommonResponse<String> checkResult = this.checkField(CONSTANT.USER_FIELD.USERNAME, username);
        if(checkResult.isSuccess()){
            return CommonResponse.createForError("该用户名不存在");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        String question = userMapper.selectOne(queryWrapper).getQuestion();
        if (StringUtils.isNotBlank(question)) {
            return CommonResponse.createForSuccess(question);
        }
        return CommonResponse.createForError("没有设置忘记密码问题");
    }

    @Override
    public CommonResponse<String> checkForgetAnswer(String username, String question, String answer) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("question", question).eq("answer", answer);
        long rows = userMapper.selectCount(queryWrapper);

        if (rows > 0) {
            // 使用UUID生成token
            String forgetToken = UUID.randomUUID().toString();
            // 将token放入缓存中
            localCache.put(username, forgetToken);
            // 打印日志，输出用户名、token和放入时间
            log.info("Put into LocalCache: ({},{}), {}", username, forgetToken, LocalDateTime.now());
            return CommonResponse.createForSuccess(forgetToken);
        }
        return CommonResponse.createForError("重置密码问题的回答错误");
    }

    @Override
    public CommonResponse<String> resetForgetPassword(String username, String newPassword, String forgetToken) {
        //验证用户名是否存在
        CommonResponse<String> checkResult = this.checkField(CONSTANT.USER_FIELD.USERNAME, username);
        if (checkResult.isSuccess()) {
            return CommonResponse.createForError("用户名不存在");
        }

        //取出token
        String token = localCache.getIfPresent(username);
        //输出日志记录token取出
        log.info("Get token from LocalCache: ({},{})", username, forgetToken);
        if (StringUtils.isEmpty(token)) {
            return CommonResponse.createForError("token无效或已过期");
        }

        if (StringUtils.equals(token, forgetToken)) {
            //对重置密码进行MD5加密
            String md5Password = bCryptPasswordEncoder.encode(newPassword);

            User user = new User();
            user.setUsername(username);
            user.setPassword(md5Password);

            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("username", username)
                    .set("password", md5Password);
            int rows = userMapper.update(user, updateWrapper);

            if (rows > 0) {
                return CommonResponse.createForSuccessMessage("重置密码成功");
            }
            return CommonResponse.createForError("通过问题回答，重置密码失败，请重新获取token");
        }
        return CommonResponse.createForError("token错误，请重新获取token");
    }

    @Override
    public CommonResponse<String> resetPassword(String username, String oldPassword, String newPassword) {
        //验证用户名是否存在
        CommonResponse<String> checkResult = this.checkField(CONSTANT.USER_FIELD.USERNAME, username);
        if (checkResult.isSuccess()) {
            return CommonResponse.createForError("用户名不存在");
        }

        //验证旧密码是否有效
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null && bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            String md5Password = bCryptPasswordEncoder.encode(newPassword);
            user = new User();
            user.setUsername(username);
            user.setPassword(md5Password);

            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("username", username).set("password", md5Password);
            int rows = userMapper.update(user, updateWrapper);

            if (rows > 0) {
                return CommonResponse.createForSuccessMessage("重置密码成功");
            }
        }
        return CommonResponse.createForError("重置密码失败");
    }

    @Override
    public CommonResponse<String> update(User user) {
        //1. 从数据库中查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User queryUser = userMapper.selectOne(queryWrapper);

        //2. 在更新对象user中放入密码
        if (queryUser != null) {
            user.setPassword(queryUser.getPassword());
        }

        //3. 更新数据库
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username", user.getUsername());
        int rows = userMapper.update(user, updateWrapper);
        if (rows > 0) {
            return CommonResponse.createForSuccess();
        }
        return CommonResponse.createForError("修改个人信息失败");
    }
}
