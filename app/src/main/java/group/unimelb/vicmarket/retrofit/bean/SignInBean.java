package group.unimelb.vicmarket.retrofit.bean;

import java.io.Serializable;

public class SignInBean implements Serializable {

    /**
     * code : 200
     * msg : success
     * data : {"uid":1,"phone":"123","displayName":"Joseph","photo":"https://img.xieyangzhe.com/img/2020-09-20/a80f883c54ed350d143c7fda3eb59596.jpg","email":"demo@demo.com","token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZW1vQGRlbW8uY29tIiwiaXNzIjoibmZhdyIsImV4cCI6MTYwMDYxMzEzNywiaWF0IjoxNjAwNTkxNTM3LCJyb2wiOiJVc2VyIn0.0LJp9izy_JaBmvp8np205fObqC4gGP826bQA9WGoH8c-iPqiOVlnE-qdqe4CqZapAnKsMR3dLbjE3qi_Gwge2A"}
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
         * phone : 123
         * displayName : Joseph
         * photo : https://img.xieyangzhe.com/img/2020-09-20/a80f883c54ed350d143c7fda3eb59596.jpg
         * email : demo@demo.com
         * token : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkZW1vQGRlbW8uY29tIiwiaXNzIjoibmZhdyIsImV4cCI6MTYwMDYxMzEzNywiaWF0IjoxNjAwNTkxNTM3LCJyb2wiOiJVc2VyIn0.0LJp9izy_JaBmvp8np205fObqC4gGP826bQA9WGoH8c-iPqiOVlnE-qdqe4CqZapAnKsMR3dLbjE3qi_Gwge2A
         */

        private int uid;
        private String phone;
        private String displayName;
        private String photo;
        private String email;
        private String token;

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
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
