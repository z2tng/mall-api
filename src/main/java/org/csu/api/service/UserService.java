package org.csu.api.service;

import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;

public interface UserService {

    public CommonResponse<User> getLoginUser(String username, String password);
}
