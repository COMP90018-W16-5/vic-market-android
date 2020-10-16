package group.unimelb.vicmarket.retrofit.bean;

import java.util.List;

public class UploadPicBean {
    /**
     * {
     * "code": 200,
     * "msg": "success",
     * "data": [
     * {
     * "seq": 1,
     * "url": "https://url/img.png"
     * }
     * ]
     * }
     */
    private int code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * "seq": 1,
         * "url": "https://url/img.png"
         */
        private int seq;
        private String url;

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }


}
