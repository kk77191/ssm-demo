package com.lab.cms.common;
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    /** 服务端响应时间戳（毫秒），便于前端排查问题与日志对账 */
    private Long timestamp;

    public static <T> Result<T> ok(T data){
        Result<T> r = new Result<>();
        r.code=200;
        r.msg="成功";
        r.data=data;
        r.timestamp=System.currentTimeMillis();
        return r;
    }
    public static <T> Result<T> ok(){
        return ok(null);
    }
    public static <T> Result<T> fail(String msg){
        return fail(500, msg);
    }
    /**
     * 指定状态码的失败响应，供业务层区分参数错误(400)、未登录(401)、无权限(403)等情况。
     */
    public static <T> Result<T> fail(int code, String msg){
        Result<T> r = new Result<>();
        r.code=code;
        r.msg=msg;
        r.timestamp=System.currentTimeMillis();
        return r;
    }
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
