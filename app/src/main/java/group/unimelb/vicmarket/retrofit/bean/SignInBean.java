package group.unimelb.vicmarket.retrofit.bean;

import java.io.Serializable;

public class SignInBean implements Serializable {
    /**
     * code : 200
     * msg : success
     * data : {"uid":1,"displayName":"Joseph","email":"demo@demo.com","token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZW1vQGRlbW8uY29tIiwiaXNzIjoibmZhdyIsImV4cCI6MTU5ODg5NzE0OCwiaWF0IjoxNTk4ODc1NTQ4LCJyb2wiOiJVc2VyIn0.Jg1SApNm5JfXFJ0CNKX8SgtKPwHP73ELKnimcV6NDQnAucIeWkkQkif8oSry-Ea1-P8Dj_p5lSLUWeCRCsqlWg"}
     */

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uid : 1
         * displayName : Joseph
         * email : demo@demo.com
         * token : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZW1vQGRlbW8uY29tIiwiaXNzIjoibmZhdyIsImV4cCI6MTU5ODg5NzE0OCwiaWF0IjoxNTk4ODc1NTQ4LCJyb2wiOiJVc2VyIn0.Jg1SApNm5JfXFJ0CNKX8SgtKPwHP73ELKnimcV6NDQnAucIeWkkQkif8oSry-Ea1-P8Dj_p5lSLUWeCRCsqlWg
         */

        private int uid;
        private String displayName;
        private String email;
        private String token;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
