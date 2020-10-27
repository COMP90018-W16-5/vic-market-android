package group.unimelb.vicmarket.retrofit;

import android.util.Log;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import group.unimelb.vicmarket.common.MarketApplication;
import group.unimelb.vicmarket.retrofit.bean.CategoriesBean;
import group.unimelb.vicmarket.retrofit.bean.CommonBean;
import group.unimelb.vicmarket.retrofit.bean.DeleteItemBean;
import group.unimelb.vicmarket.retrofit.bean.ItemDetailBean;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;
import group.unimelb.vicmarket.retrofit.bean.PostItemBean;
import group.unimelb.vicmarket.retrofit.bean.SignInBean;
import group.unimelb.vicmarket.retrofit.bean.SignUpBean;
import group.unimelb.vicmarket.retrofit.bean.UploadPicBean;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import top.zibin.luban.Luban;

public class RetrofitHelper {
    private static final int DEFAULT_TIMEOUT = 10;
    private static String BASE_URL = "http://ss.xieyangzhe.com:9090";
    private Api api;

    private String token;

    private RetrofitHelper() {
        initAuth();
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    if (token != null && !"".equals(token)) {
                        request = request.newBuilder().addHeader("Authorization", "Bearer " + token).build();
                    }
                    return chain.proceed(request);
                })
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
                .addConverterFactory(CustomGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        api = retrofit.create(Api.class);
    }

    public static RetrofitHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void doLogin(Observer<SignInBean> observer, String username, String password) {
        String URL = BASE_URL + "/auth/signin";
        Map<String, Object> params = new HashMap<>();
        params.put("email", username);
        params.put("password", password);
        execute(api.signIn(URL, params), observer);
    }

    public void doSignUp(Observer<SignUpBean> observer, String username, String email,String phone, String password ,String picUrl) {
        String URL = BASE_URL+ "/auth/signup";
        Map<String, Object> params = new HashMap<>();
        params.put("displayName", username);
        params.put("email" , email);
        params.put("password", password);
        params.put("phone",phone);
        params.put("photo", picUrl);
        execute(api.SignUp(URL , params) , observer);
    }

    public void uploadPic(Observer<UploadPicBean> observer , String picUrl ){
        String URL = BASE_URL+ "/item/image";
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);


        File file = new File(picUrl);
        try {
            file = Luban.with(MarketApplication.getAppContext()).load(file).get().get(0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String extName = file.getName().substring(file.getName().lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extName;
        builder.addFormDataPart("images" , fileName ,
                RequestBody.create(MediaType.parse("multipart/form-data"), file));
        MultipartBody requestBody = builder.build();
        execute(api.uploadAvator(URL, requestBody) , observer);

    }

    public void PostItem(Observer<PostItemBean> observer, String title, String description,
                     int category, double price, String location, double latitude,
                     double longitude, String picUrl) {
        String URL = BASE_URL+ "/user/post";
        Map<String, Object> params = new HashMap<>();
        params.put("title", title);
        params.put("description" , description);
        params.put("category", category);
        params.put("price", price);
        params.put("address", location);
        params.put("latitude", latitude);
        params.put("longitude",longitude);
        params.put("image", picUrl);
        Log.d("DEMO", "PostItem: " + params);
        execute(api.PostItem(URL , params) , observer);
    }

    public void getItemList(Observer<MainItemListBean> observer, String page) {
        String URL = BASE_URL + "/item/list";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("pageSize", 10);
        execute(api.getItemList(URL, params), observer);
    }

    public void getItemListByCategory(Observer<MainItemListBean> observer, String page, int category) {
        String URL = BASE_URL + "/item/list";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("pageSize", 15);
        params.put("category", category);
        execute(api.getItemList(URL, params), observer);
    }

    public void getWishList(Observer<MainItemListBean> observer, String page) {
        String URL = BASE_URL + "/user/wishlist";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("pageSize", 15);
        execute(api.getWishList(URL, params), observer);
    }

    public void addWishList(Observer<CommonBean> observer, int itemId) {
        String URL = BASE_URL + "/user/wishlist";
        Map<String, Object> params = new HashMap<>();
        params.put("itemId", itemId);
        execute(api.addWishList(URL, params), observer);
    }

    public void deleteWishList(Observer<CommonBean> observer, int itemId) {
        String URL = BASE_URL + "/user/wishlist";
        Map<String, Object> params = new HashMap<>();
        params.put("itemId", itemId);
        execute(api.deleteWishList(URL, params), observer);
    }

    public void getMyPost(Observer<MainItemListBean> observer, String page) {
        String URL = BASE_URL + "/user/items";
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("pageSize", 15);
        execute(api.getMyPost(URL, params), observer);
    }

    public void deleteMyPost(Observer<DeleteItemBean> observer, int itemId) {
        String URL = BASE_URL + "/user/item";
        Map<String, Object> params = new HashMap<>();
        params.put("itemId", itemId);
        execute(api.deleteMyPost(URL, params), observer);
    }

    public void searchItemList(Observer<MainItemListBean> observer, String keyword, String page) {
        String URL = BASE_URL + "/item/search";
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("page", page);
        params.put("pageSize", 10);
        execute(api.searchItemList(URL, params), observer);
    }

    public void getNearbyItems(Observer<MainItemListBean> observer,
                               double longitude,
                               double latitude,
                               int maxDistance,
                               int page,
                               int category) {
        String URL = BASE_URL + "/item/nearby";
        Map<String, Object> params = new HashMap<>();
        params.put("longitude", longitude);
        params.put("latitude", latitude);
        params.put("maxDistance", maxDistance);
        params.put("category", category);
        params.put("page", page);
        params.put("pageSize", 15);
        execute(api.getNearbyItems(URL, params), observer);
    }

    public void getItemDetails(Observer<ItemDetailBean> observer, int id) {
        String URL = BASE_URL + "/item/detail";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        execute(api.getItemDetails(URL, params), observer);
    }

    public void getRandomItem(Observer<ItemDetailBean> observer) {
        String URL = BASE_URL + "/item/random";
        execute(api.getRandomItem(URL), observer);
    }

    public void getCategories(Observer<CategoriesBean> observer) {
        String URL = BASE_URL + "/item/categories";
        execute(api.getCategories(URL), observer);
    }

    private void execute(Observable observable, Observer observer) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void setToken(String token) {
        this.token = token;
    }

    private void initAuth() {
        token = SPUtils.getInstance().getString("token", "");
    }

    private static class SingletonHolder {
        private static final RetrofitHelper INSTANCE = new RetrofitHelper();
    }
}
