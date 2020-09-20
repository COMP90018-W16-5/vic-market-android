package group.unimelb.vicmarket.common;

import android.app.Application;
import android.content.Context;

import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;

import org.jetbrains.annotations.NotNull;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.ui.CustomFooter;

public class MarketApplication extends Application {

    private static Context AppContext;

    public static Context getAppContext() {
        return AppContext;
    }

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
            return new MaterialHeader(context);
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new CustomFooter(context));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
    }
}
