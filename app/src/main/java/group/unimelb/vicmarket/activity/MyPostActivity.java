package group.unimelb.vicmarket.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.josephvuoto.customdialog.alert.CustomDialog;
import com.josephvuoto.customdialog.common.OnOkClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.adapter.CommonItemListAdapter;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.DeleteItemBean;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;
import group.unimelb.vicmarket.util.LocationUtil;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MyPostActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout emptyView;

    /* Adapter for RecyclerView */
    private CommonItemListAdapter adapter;
    /* Deserialized data from the server */
    private List<MainItemListBean.DataBean> dataBeans = new ArrayList<>();

    private int page = 1;

    private double longitude = 144.9628;
    private double latitude = -37.8102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        findViews();
        initLocation();

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("My Posts");
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

                    adapter.setOnListItemLongClickListener(index -> {
                        new CustomDialog.Builder(MyPostActivity.this)
                                .setTitle("Message")
                                .setMessage("Delete this item?")
                                .setOkButton("Confirm", dialog -> {
                                    RetrofitHelper.getInstance().deleteMyPost(new Observer<DeleteItemBean>() {
                                        @Override
                                        public void onSubscribe(@NonNull Disposable d) {

                                        }

                                        @Override
                                        public void onNext(@NonNull DeleteItemBean deleteItemBean) {
                                            ToastUtils.showShort("Deleted");
                                            dataBeans.remove(index);
                                            adapter.setData(dataBeans);
                                            adapter.notifyDataSetChanged();
                                            if (dataBeans.isEmpty()) {
                                                emptyView.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            ToastUtils.showShort("Error");
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onComplete() {

                                        }
                                    }, dataBeans.get(index).getItemId());
                                    dialog.dismiss();
                                }).setCancelButton("Cancel", Dialog::dismiss)
                                .build().show();
                    });

                    /* Start refreshing when starting the page */
                    refreshLayout.autoRefresh();
                });
    }

    private void loadData() {
        /* Send HTTP request to get data */
        RetrofitHelper.getInstance().getMyPost(new Observer<MainItemListBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MainItemListBean mainItemListBean) {
                if (refreshLayout.getState() == RefreshState.Refreshing) {
                    dataBeans.clear();
                }
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
        }, String.valueOf(page));
    }

    private void findViews() {
        toolbar = findViewById(R.id.my_detail_toolbar);
        refreshLayout = findViewById(R.id.my_detail_refresh);
        recyclerView = findViewById(R.id.my_detail_list_recycler);
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