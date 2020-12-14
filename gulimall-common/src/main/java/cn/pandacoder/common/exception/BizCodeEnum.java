package cn.pandacoder.common.exception;

public enum BizCodeEnum {
    UNKNOWN_EXCEPTION(10000,"未知错误，请联系管理员"),
    VALID_EXCEPTION(10001,"参数格式校验失败");

    private int code;
    private String msg;

    BizCodeEnum(int code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
