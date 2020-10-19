package group.unimelb.vicmarket.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baiiu.filter.DropDownMenu;
import com.baiiu.filter.interfaces.OnFilterDoneListener;
import com.blankj.utilcode.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.adapter.CommonItemListAdapter;
import group.unimelb.vicmarket.adapter.DropMenuAdapter;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;
import group.unimelb.vicmarket.util.LocationUtil;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class NearbyItemsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private DropDownMenu dropdownMenu;

    private double longitude = 0;
    private double latitude = 0;

    /* Adapter for RecyclerView */
    private CommonItemListAdapter adapter;
    /* Deserialized data from the server */
    private List<MainItemListBean.DataBean> dataBeans = new ArrayList<>();

    private int page = 1;
    private int maxDistance = 20;
    private int category = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_item);

        findViews();

        initLocation();

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("Nearby");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

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

        String[] titleList = new String[] { "Distance", "Category"};
        DropMenuAdapter dropMenuAdapter = new DropMenuAdapter(this, titleList);
        dropMenuAdapter.setOnItemSelectedListener((type, code, value) -> {
            dropdownMenu.close();
            if (type == 0) {
                maxDistance = code;
            } else if (type == 1) {
                category = code;
            }
            refreshLayout.autoRefresh();
        });
        dropdownMenu.setMenuAdapter(dropMenuAdapter);
    }

    @SuppressLint("CheckResult")
    private void initLocation() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        LocationUtil.LocationInfo locationInfo = LocationUtil.getInstance().getLocationInfo();
                        latitude = locationInfo.getLatitude();
                        longitude = locationInfo.getLongitude();
                    }

                    /* Initialize the adapter and add to RecyclerView */
                    adapter = new CommonItemListAdapter(this, longitude, latitude);
                    recyclerView.setAdapter(adapter);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                            LinearLayoutManager.VERTICAL, false);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(layoutManager);

                    /* Start refreshing when starting the page */
                    refreshLayout.autoRefresh();
                });
    }

    private void loadData() {
        /* Send HTTP request to get data */
        RetrofitHelper.getInstance().getNearbyItems(new Observer<MainItemListBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MainItemListBean mainItemListBean) {
                if (refreshLayout.getState() == RefreshState.Refreshing) {
                    dataBeans.clear();
                }
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
        }, longitude, latitude, maxDistance, page, category);
    }

    private void findViews() {
        toolbar = findViewById(R.id.cate_detail_toolbar);
        refreshLayout = findViewById(R.id.cate_detail_refresh);
        recyclerView = findViewById(R.id.cate_detail_list_recycler);
        dropdownMenu = findViewById(R.id.filterDropDownView);
    }

    private void endLoading() {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        } else if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore();
        }
    }
}