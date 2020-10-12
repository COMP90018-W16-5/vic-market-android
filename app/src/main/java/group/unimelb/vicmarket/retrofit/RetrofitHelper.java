package group.unimelb.vicmarket.retrofit;

import com.blankj.utilcode.util.SPUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import group.unimelb.vicmarket.retrofit.bean.CategoriesBean;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;
import group.unimelb.vicmarket.retrofit.bean.SignInBean;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

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
                    request = request.newBuilder().addHeader("Authorization", "Bearer " + token).build();
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

    public void searchItemList(Observer<MainItemListBean> observer, String keyword, String page) {
        String URL = BASE_URL + "/item/search";
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("page", page);
        params.put("pageSize", 10);
        execute(api.searchItemList(URL, params), observer);
    }

    public void getNearbyItems(Observer<MainItemListBean> observer,
                               BigDecimal longitude,
                               BigDecimal latitude,
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
        execute(api.searchItemList(URL, params), observer);
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
