package group.unimelb.vicmarket.retrofit.bean;

import java.util.List;

public class ItemDetailBean {

    /**
     * code : 200
     * msg : success
     * data : {"address":"","categories":[{"cid":1,"name":"Smart phone"}],"description":"description","itemId":1,"latitude":37.8136,"longitude":144.9631,"price":20.2,"seller":{"displayName":"Luke Skywalker","email":"demo@demo.com","phone":"Luke Skywalker","photo":"https://url/img.png","uid":1},"status":0,"title":"title","urls":[{"imid":1,"url":"https://url/img.png"}]}
     */

    private int code;
    private String msg;
    /**
     * address :
     * categories : [{"cid":1,"name":"Smart phone"}]
     * description : description
     * itemId : 1
     * latitude : 37.8136
     * longitude : 144.9631
     * price : 20.2
     * seller : {"displayName":"Luke Skywalker","email":"demo@demo.com","phone":"Luke Skywalker","photo":"https://url/img.png","uid":1}
     * status : 0
     * title : title
     * urls : [{"imid":1,"url":"https://url/img.png"}]
     */

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
        private String address;
        private String description;
        private int itemId;
        private double latitude;
        private double longitude;
        private double price;
        /**
         * displayName : Luke Skywalker
         * email : demo@demo.com
         * phone : Luke Skywalker
         * photo : https://url/img.png
         * uid : 1
         */

        private SellerBean seller;
        private int status;
        private String title;
        /**
         * cid : 1
         * name : Smart phone
         */

        private List<CategoriesBean> categories;
        /**
         * imid : 1
         * url : https://url/img.png
         */

        private List<UrlsBean> urls;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
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

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public SellerBean getSeller() {
            return seller;
        }

        public void setSeller(SellerBean seller) {
            this.seller = seller;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<CategoriesBean> getCategories() {
            return categories;
        }

        public void setCategories(List<CategoriesBean> categories) {
            this.categories = categories;
        }

        public List<UrlsBean> getUrls() {
            return urls;
        }

        public void setUrls(List<UrlsBean> urls) {
            this.urls = urls;
        }

        public static class SellerBean {
            private String displayName;
            private String email;
            private String phone;
            private String photo;
            private int uid;

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

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getPhoto() {
                return photo;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }
        }

        public static class CategoriesBean {
            private int cid;
            private String name;

            public int getCid() {
                return cid;
            }

            public void setCid(int cid) {
                this.cid = cid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class UrlsBean {
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
