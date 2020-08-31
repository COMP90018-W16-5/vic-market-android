package group.unimelb.vicmarket.retrofit;

import java.util.Map;

import group.unimelb.vicmarket.retrofit.bean.SignInBean;
import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface Api {
    @POST
    Observable<SignInBean> signIn(@Url String url, @QueryMap Map<String, Object> map);
}
