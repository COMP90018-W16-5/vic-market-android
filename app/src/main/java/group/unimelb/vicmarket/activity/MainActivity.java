package group.unimelb.vicmarket.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.josephvuoto.customdialog.alert.CustomDialog;
import com.josephvuoto.customdialog.common.OnCancelClickListener;
import com.josephvuoto.customdialog.common.OnOkClickListener;
import com.josephvuoto.customdialog.custom.CustomViewDialog;
import com.josephvuoto.customdialog.loading.LoadingDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.adapter.LocationListAdapter;
import group.unimelb.vicmarket.adapter.MainItemListAdapter;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;
import group.unimelb.vicmarket.util.LocationUtil;
import group.unimelb.vicmarket.util.SensorManagerHelper;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private ImageView imageHead;
    private RelativeLayout buttonSearch;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private LinearLayout buttonCategoryCars;
    private LinearLayout buttonCategoryHome;
    private LinearLayout buttonCategoryPhone;
    private LinearLayout buttonCategoryNearby;
    private LinearLayout buttonCategoryMore;
    private FloatingActionButton buttonPostNew;
    private FloatingActionButton buttonRandom;
    private FloatingActionsMenu actionsMenu;

    /* Adapter for RecyclerView */
    private MainItemListAdapter adapter;
    /* Deserialized data from the server */
    private List<MainItemListBean.DataBean> dataBeans = new ArrayList<>();

    private int page = 1;
    private boolean login = false;

    private double longitude = 144.9628;
    private double latitude = -37.8102;

    private SensorManagerHelper sensorManagerHelper;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        buttonSearch.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        imageHead.setOnClickListener(v -> {
            if (!login) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
            }
        });

        initLocation();

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            /* Refresh */
            page = 1;
            loadData();
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            /* Load more */
            page++;
            loadData();
        });

        loadingDialog = new LoadingDialog.Builder(MainActivity.this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();
        sensorManagerHelper = new SensorManagerHelper(this);
        sensorManagerHelper.setOnShakeListener(() -> {
            loadingDialog.show();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    loadingDialog.dismiss();
                    // TODO
                }
            }, 1500);
        });


        buttonCategoryCars.setOnClickListener(this);
        buttonCategoryHome.setOnClickListener(this);
        buttonCategoryPhone.setOnClickListener(this);
        buttonCategoryNearby.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NearbyItemsActivity.class)));
        buttonCategoryMore.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CategoryListActivity.class)));
        buttonPostNew.setOnClickListener(v -> {
            if (!login) {
                new CustomDialog.Builder(MainActivity.this)
                        .setTitle("Message")
                        .setMessage("Login to post items.")
                        .setOkButton("Login", dialog -> startActivity(new Intent(MainActivity.this, LoginActivity.class)))
                        .setCancelButton("Cancel", Dialog::dismiss)
                        .build()
                        .show();
            } else {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
            actionsMenu.collapse();
        });

        buttonRandom.setOnClickListener(v -> {

            sensorManagerHelper.start();
            new CustomDialog.Builder(MainActivity.this)
                    .setTitle("I'm feeling lucky")
                    .setMessage("Shake your phone to see a random item!")
                    .setCancelButton("Cancel", dialog -> {
                        sensorManagerHelper.stop();
                        dialog.dismiss();
                    }).build().show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtils.getInstance().getBoolean("login")) {
            login = true;
            String photo = SPUtils.getInstance().getString("photo");
            if (!"".equals(photo)) {
                Glide.with(this).load(photo).placeholder(R.drawable.ic_account).into(imageHead);
            } else {
                Glide.with(this).load(R.drawable.ic_account);
            }
        }
    }

    private void findViews() {
        imageHead = findViewById(R.id.main_head);
        buttonSearch = findViewById(R.id.main_search);
        refreshLayout = findViewById(R.id.main_refresh);
        recyclerView = findViewById(R.id.main_list_recycler);
        buttonCategoryCars = findViewById(R.id.cate_cars);
        buttonCategoryHome = findViewById(R.id.cate_home);
        buttonCategoryPhone = findViewById(R.id.cate_phone);
        buttonCategoryNearby = findViewById(R.id.cate_nearby);
        buttonCategoryMore = findViewById(R.id.cate_more);
        buttonPostNew = findViewById(R.id.action_a);
        actionsMenu = findViewById(R.id.multiple_actions);
        buttonRandom = findViewById(R.id.action_b);
    }

    @SuppressLint("CheckResult")
    private void initLocation() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        LocationUtil.LocationInfo locationInfo = LocationUtil.getInstance().getLocationInfo();
                        if (locationInfo != null) {
                            latitude = locationInfo.getLatitude();
                            longitude = locationInfo.getLongitude();
                        }
                    }

                    /* Initialize the adapter and add to RecyclerView */
                    adapter = new MainItemListAdapter(this, longitude, latitude);
                    recyclerView.setAdapter(adapter);
                    /* Use GridLayout to display two columns */
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return 1;
                        }
                    });

                    recyclerView.setLayoutManager(gridLayoutManager);
                    /* Init data, see if there is cache */
                    initData();

                    /* Start refreshing when starting the page */
                    refreshLayout.autoRefresh();
                });
    }

    private void loadData() {
        /* Send HTTP request to get data */
        RetrofitHelper.getInstance().getItemList(new Observer<MainItemListBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MainItemListBean mainItemListBean) {
                if (refreshLayout.getState() == RefreshState.Refreshing) {
                    /* Refreshing, clear the data list */
                    dataBeans.clear();
                    /* Save cache */
                    SPUtils.getInstance().put("home_page_cache", new Gson().toJson(mainItemListBean.getData()));
                }
                /* Add data */
                dataBeans.addAll(mainItemListBean.getData());
                adapter.setData(dataBeans);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                endLoading();
            }

            @Override
            public void onComplete() {
                endLoading();
            }
        }, String.valueOf(page));
    }

    private void initData() {
        dataBeans.clear();
        /* Load data from SharedPreference */
        String cachedJson = SPUtils.getInstance().getString("home_page_cache");
        if (cachedJson != null && !cachedJson.isEmpty()) {
            Type listType = new TypeToken<ArrayList<MainItemListBean.DataBean>>() {
            }.getType();
            dataBeans = new Gson().fromJson(cachedJson, listType);
            adapter.setData(dataBeans);
        }
    }

    private void endLoading() {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        } else if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, CategoryDetailActivity.class);
        switch (v.getId()) {
            case R.id.cate_cars:
                intent.putExtra("category_id", 1);
                intent.putExtra("category_name", getString(R.string.category_cars_and_vehicles));
                break;
            case R.id.cate_home:
                intent.putExtra("category_id", 2);
                intent.putExtra("category_name", getString(R.string.category_home_and_garden));
                break;
            case R.id.cate_phone:
                intent.putExtra("category_id", 3);
                intent.putExtra("category_name", getString(R.string.category_electronics));
                break;
        }
        startActivity(intent);
    }
}