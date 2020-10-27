package group.unimelb.vicmarket.retrofit;

import java.util.Map;

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
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface Api {
    @POST
    Observable<SignInBean> signIn(@Url String url, @QueryMap Map<String, Object> map);

    @POST
    Observable<SignUpBean> SignUp(@Url String url, @QueryMap Map<String, Object> map);

    @POST
    Observable<UploadPicBean> uploadAvator(@Url String url, @Body MultipartBody requestBody);

    @POST
    Observable<PostItemBean> PostItem(@Url String url, @QueryMap Map<String, Object> map);

    @GET
    Observable<MainItemListBean> getItemList(@Url String url, @QueryMap Map<String, Object> map);

    @GET
    Observable<MainItemListBean> searchItemList(@Url String url, @QueryMap Map<String, Object> map);

    @GET
    Observable<CategoriesBean> getCategories(@Url String url);

    @GET
    Observable<MainItemListBean> getNearbyItems(@Url String url, @QueryMap Map<String, Object> map);

    @GET
    Observable<MainItemListBean> getWishList(@Url String url, @QueryMap Map<String, Object> map);

    @POST
    Observable<CommonBean> addWishList(@Url String url, @QueryMap Map<String, Object> map);

    @DELETE
    Observable<CommonBean> deleteWishList(@Url String url, @QueryMap Map<String, Object> map);

    @GET
    Observable<MainItemListBean> getMyPost(@Url String url, @QueryMap Map<String, Object> map);

    @DELETE
    Observable<DeleteItemBean> deleteMyPost(@Url String url, @QueryMap Map<String, Object> map);

    @GET
    Observable<ItemDetailBean> getItemDetails(@Url String url, @QueryMap Map<String, Object> map);

    @GET
    Observable<ItemDetailBean> getRandomItem(@Url String url);
}
