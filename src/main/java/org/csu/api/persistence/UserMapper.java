package org.csu.api.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.csu.api.domain.User;
import org.springframework.stereotype.Repository;

@Repository("userMapper")
public interface UserMapper extends BaseMapper<User> {
}
