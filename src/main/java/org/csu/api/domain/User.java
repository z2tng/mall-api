package org.csu.api.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
@TableName("mystore_user")
public class User {
    private Long id;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "邮箱不能为空")
    private String email;
    @NotBlank(message = "电话不能为空")
    private String phone;
    private String question;
    private String answer;
    private String role;
    private Date createAt;
    private Date updateAt;
}
