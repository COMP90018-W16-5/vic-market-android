package group.unimelb.vicmarket.common;

import android.app.Application;
import android.content.Context;

public class MarketApplication extends Application {

    private static Context AppContext;

    public static Context getAppContext() {
        return AppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppContext = getApplicationContext();
    }
}
