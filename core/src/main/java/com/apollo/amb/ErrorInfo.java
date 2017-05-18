package com.apollo.amb;

public class ErrorInfo<T> {
    public static final Integer OK = 0;
    public static final Integer ERROR = 100;

    private Integer code;
    private String message;
    private String url;
    private T data;
    
    public void setMessage(String message) { this.message = message; }
    public String getMessage() { return this.message; }
    
    public void setCode(int code) { this.code = code; }
    public Integer getCode() { return this.code; }
    
    public void setUrl(String url) { this.url = url; }
    public String getUrl() { return this.url; }
    
    public void setData(T data) { this.data = data; }
    public T getData() { return this.data; }
}
