package org.csu.api.service;

import org.csu.api.common.CommonResponse;
import org.csu.api.domain.User;
import org.csu.api.dto.RegisterUserDTO;

public interface UserService {

    public CommonResponse<User> login(String username, String password);

    public CommonResponse<String> checkField(String fieldName, String fieldValue);

    public CommonResponse<String> register(RegisterUserDTO registerUserDTO);
}
