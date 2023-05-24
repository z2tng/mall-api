package org.csu.api.common;

import lombok.Getter;

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

    @Getter
    public enum ProductStatus {
        ON_SALE(1, "on_sale"),
        TAKE_DOWN(2, "take_down"),
        DELETE(3, "delete");

        private final int code;
        private final String description;

        ProductStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }
    }

    public static final String PRODUCT_ORDER_BY_PRICE_ASC = "price_asc";
    public static final String PRODUCT_ORDER_BY_PRICE_DESC = "price_desc";

    public interface CART_ITEM_STATUS {
        int UNCHECKED = 0;
        int CHECKED = 1;
    }
}
