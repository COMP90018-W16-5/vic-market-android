package group.unimelb.vicmarket.retrofit.bean;

import java.util.List;

public class SignUpBean {
    /**{
        "code": 200,
            "msg": "success",
            "data": {}
    }
    */
    private int code;
    private String msg;
    private List<CategoriesBean.DataBean> data;

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

    public List<CategoriesBean.DataBean> getData() {
        return data;
    }

    public void setData(List<CategoriesBean.DataBean> data) {
        this.data = data;
    }


}
