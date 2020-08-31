package group.unimelb.vicmarket.retrofit;

import com.blankj.utilcode.util.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private Retrofit retrofit;
    private Api api;
    private OkHttpClient okHttpClient;

    private String token;

    private List<String> AUTH_POOL = new ArrayList<>();

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
        okHttpClient = okHttpClientBuilder.build();

        retrofit = new Retrofit.Builder().client(okHttpClient)
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
        excute(api.signIn(URL, params), observer);
    }

    private void excute(Observable observable, Observer observer) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void setToken(String token) {
        this.token = token;
    }

    private void initAuth() {
        token = SPUtils.getInstance().getString("auth", "");
    }

    private static class SingletonHolder {
        private static final RetrofitHelper INSTANCE = new RetrofitHelper();
    }
}
