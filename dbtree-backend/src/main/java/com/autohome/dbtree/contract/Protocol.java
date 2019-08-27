package com.autohome.dbtree.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class Protocol<T extends Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "返回码")
    private int returncode;

    @ApiModelProperty(value = "说明消息")
    private String message;

    @ApiModelProperty(value = "返回结果")
    private T result;

    public int getReturncode() {
        return returncode;
    }

    public void setReturncode(int returncode) {
        this.returncode = returncode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Protocol() {
    }

    public Protocol(int returncode, String message, T result) {
        this.returncode = returncode;
        this.message = message;
        this.result = result;
    }

    public Protocol(int returncode, String message) {
        this(returncode, message, null);
    }

    public Protocol(T result) {
        this(0, "ok", result);
    }
}
