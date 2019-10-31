package com.offcn.entity;

import java.io.Serializable;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description： 增删改操作的返回值，如果成功就返回success，如果失败就返回message
 * @date ：2019/10/23 17:31
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 4589242862769041149L;
    private boolean success;
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
