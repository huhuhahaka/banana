package com.fruits.common;

public enum ResponseCodeEnum {

    SUCCESS("0", "请求成功"),
    ERROR("E10001", "请求失败"),
    INVALID_PARAM("E10002", "非法参数"),
    INVALID_TOKEN("E10003", "非法Token");

    private String code;
    private String description;

    ResponseCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
