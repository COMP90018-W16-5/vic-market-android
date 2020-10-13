package group.unimelb.vicmarket.common;

import android.app.Application;
import android.content.Context;

import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.ui.CustomFooter;

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
    }
}
