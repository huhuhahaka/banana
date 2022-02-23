package com.fruits.common;

import static com.fruits.common.ResponseCodeEnum.ERROR;
import static com.fruits.common.ResponseCodeEnum.SUCCESS;

/**
 * @author: ggl
 * @createDate: 2021/11/05
 * @description: 统一相应类
 */
public class Response<T> {

    /**
     * 请求ID
     */
    private String qid;

    /**
     * 响应状态码
     */
    private String code;

    /**
     * 响应信息，用于给用户展示
     */
    private String msg;

    /**
     * 调试信息，开发人员调试使用
     */
    private String debug;

    /**
     * 响应数据
     */
    private T data;

    public String getQid() {
        return qid;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getDebug() {
        return debug;
    }

    public T getData() {
        return data;
    }

    public static Builder successBuilder() {
        return new Response.Builder().code(SUCCESS.getCode()).msg(SUCCESS.getDescription());
    }

    public static Response success() {
        return successBuilder().build();
    }

    public static  <T> Response<T> success(T data) {
        return successBuilder().data(data).build();
    }

    public static  <T> Response<T> success(String qid, T data) {
        return successBuilder().qid(qid).data(data).build();
    }

    public static Builder errorBuilder() {
        return new Response.Builder().code(ERROR.getCode()).msg(ERROR.getDescription());
    }

    public static Response error() {
        return errorBuilder().build();
    }

    public static Response error(String debug) {
        return errorBuilder().debug(debug).build();
    }

    public static  <T> Response<T> error(String qid, T data) {
        return errorBuilder().qid(qid).data(data).build();
    }


    private Response(Builder builder) {
        this.qid = builder.qid;
        this.code = builder.code;
        this.msg = builder.msg;
        this.debug = builder.debug;
        this.data = (T) builder.data;
    }

    public static class Builder<T> {
        private String qid;
        private String code;
        private String msg;
        private String debug;
        private T data;

        public Builder() {
        }

        public Builder qid(String qid) {
            this.qid = qid;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder debug(String debug) {
            this.debug = debug;
            return this;
        }

        public Builder data(T data) {
            this.data = data;
            return this;
        }

        public Response build(){
            return new Response(this);
        }
    }

}
