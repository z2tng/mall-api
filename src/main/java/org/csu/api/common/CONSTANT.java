package org.csu.api.common;

public class CONSTANT {

    public static final String LOGIN_USER = "loginUser";
    public static final int CATEGORY_ROOT = 0;

    public interface ROLE {
        int ADMIN = 0;
        int CUSTOMER = 1;
    }

    public interface USER_FIELD {
        String USERNAME = "username";
        String PHONE = "phone";
        String EMAIL = "email";
    }
}
