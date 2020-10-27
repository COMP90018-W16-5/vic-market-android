package group.unimelb.vicmarket.common;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.SPUtils;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.SignInBean;
import group.unimelb.vicmarket.ui.CustomFooter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MarketApplication extends Application {

    private static Context AppContext;

    static {
        /* Initialize SmartRefreshLayout (header and footer) */
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            return new MaterialHeader(context);
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new CustomFooter(context));
    }

    public static Context getAppContext() {
        return AppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
        doLogin();
    }

    private void doLogin() {
        if (SPUtils.getInstance().getBoolean("login")) {
            String email = SPUtils.getInstance().getString("email");
            String password = SPUtils.getInstance().getString("password");
            /* Initialize an observer */
            Observer<SignInBean> observer = new Observer<SignInBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(SignInBean signInBean) {
                    if (signInBean.getCode() == 200) {
                        RetrofitHelper.getInstance().setToken(signInBean.getData().getToken());
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            };
            RetrofitHelper.getInstance().doLogin(observer, email, password);
        }
    }
}
