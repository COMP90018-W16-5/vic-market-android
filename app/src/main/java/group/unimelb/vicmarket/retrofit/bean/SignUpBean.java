package group.unimelb.vicmarket.retrofit.bean;

public class SignUpBean {
    /**
     * {
     * "code": 200,
     * "msg": "success",
     * "data": {}
     * }
     */
    private int code;
    private String msg;

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
}
