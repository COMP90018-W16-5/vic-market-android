package group.unimelb.vicmarket.retrofit.bean;

import java.util.List;

public class MainItemListBean {
    /**
     * code : 200
     * msg : success
     * data : [{"itemId":1,"title":"Demo","description":"demodemo","price":100,"latitude":123.123445,"longitude":123.123445,"urls":[{"imid":1,"url":"http://demo.demo"},{"imid":2,"url":"http://demo123.demo213"}],"status":0}]
     * page : 1
     * hasNext : false
     * hasPrevious : false
     */

    private int code;
    private String msg;
    private int page;
    private boolean hasNext;
    private boolean hasPrevious;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * itemId : 1
         * title : Demo
         * description : demodemo
         * price : 100
         * latitude : 123.123445
         * longitude : 123.123445
         * urls : [{"imid":1,"url":"http://demo.demo"},{"imid":2,"url":"http://demo123.demo213"}]
         * status : 0
         */

        private int itemId;
        private String title;
        private String description;
        private double price;
        private double latitude;
        private double longitude;
        private int status;
        private List<UrlsBean> urls;

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<UrlsBean> getUrls() {
            return urls;
        }

        public void setUrls(List<UrlsBean> urls) {
            this.urls = urls;
        }

        public static class UrlsBean {
            /**
             * imid : 1
             * url : http://demo.demo
             */

            private int imid;
            private String url;

            public int getImid() {
                return imid;
            }

            public void setImid(int imid) {
                this.imid = imid;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
