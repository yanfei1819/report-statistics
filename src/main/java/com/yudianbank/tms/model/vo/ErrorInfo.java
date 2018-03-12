package com.yudianbank.tms.model.vo;

/**
 * 异常返回的数据进行json封装
 *
 * @param <T>
 * @author Song Lea
 */
public class ErrorInfo<T> {

    private int code;
    private String message;
    private String url;
    private T data;

    public ErrorInfo() {
    }

    public ErrorInfo(int code, String message, String url) {
        this.code = code;
        this.message = message;
        this.url = url;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                ", data=" + data +
                '}';
    }
}
