package group.unimelb.vicmarket.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.adapter.CommonItemListAdapter;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;
import group.unimelb.vicmarket.util.LocationUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class SearchActivity extends AppCompatActivity {
    private ImageView buttonBack;
    private EditText textSearch;
    private ImageView buttonSearch;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout emptyView;

    private int page = 1;
    private String keyword = null;

    /* Adapter for RecyclerView */
    private CommonItemListAdapter adapter;
    /* Deserialized data from the server */
    private List<MainItemListBean.DataBean> dataBeans = new ArrayList<>();

    private double longitude = 144.9628;
    private double latitude = -37.8102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViews();

        initLocation();

        buttonBack.setOnClickListener(v -> finish());

        textSearch.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(textSearch, 0);
            }
        }, 500);
        textSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyword = textSearch.getText().toString();
                page = 1;
                if ("".equals(keyword)) {
                    ToastUtils.showShort("Keyword cannot be empty");
                } else {
                    refreshLayout.setVisibility(View.VISIBLE);
                    refreshLayout.autoRefresh();
                }
            }
            return false;
        });

        buttonSearch.setOnClickListener(v -> {
            keyword = textSearch.getText().toString();
            page = 1;
            if ("".equals(keyword)) {
                ToastUtils.showShort("Keyword cannot be empty");
                return;
            }
            refreshLayout.setVisibility(View.VISIBLE);
            refreshLayout.autoRefresh();
        });

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            page = 1;
            loadData();
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            page++;
            loadData();
        });
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
                    adapter = new CommonItemListAdapter(this, longitude, latitude);
                    recyclerView.setAdapter(adapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(layoutManager);
                });
    }

    private void loadData() {
        /* Send HTTP request to get data */
        RetrofitHelper.getInstance().searchItemList(new Observer<MainItemListBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MainItemListBean mainItemListBean) {
                if (refreshLayout.getState() == RefreshState.Refreshing) {
                    dataBeans.clear();
                }
                /* Add data */
                dataBeans.addAll(mainItemListBean.getData());
                if (dataBeans.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
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
        }, keyword, String.valueOf(page));
    }

    private void findViews() {
        buttonBack = findViewById(R.id.search_back);
        textSearch = findViewById(R.id.search_text);
        refreshLayout = findViewById(R.id.search_refresh);
        recyclerView = findViewById(R.id.search_list_recycler);
        buttonSearch = findViewById(R.id.search_action);
        emptyView = findViewById(R.id.empty_view);
    }

    private void endLoading() {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        } else if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore();
        }
    }
}