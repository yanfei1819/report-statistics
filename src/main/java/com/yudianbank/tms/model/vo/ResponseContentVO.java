package com.yudianbank.tms.model.vo;

import java.io.Serializable;

/**
 * 短信返回的对象映射
 *
 * @author Song Lea
 */
public class ResponseContentVO implements Serializable {

    private static final long serialVersionUID = 4817958190108869339L;

    private int code;
    private String msg;
    private String content;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
