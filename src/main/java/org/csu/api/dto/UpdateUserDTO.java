package org.csu.api.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private String question;
    private String answer;
}
