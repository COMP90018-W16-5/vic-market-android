package group.unimelb.vicmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.adapter.MainItemListAdapter;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    //TAG is "MainActivity"
    private final static String TAG = MainActivity.class.getSimpleName();

    //A picture at the left and top corner of the screen
    private ImageView imageHead;

    //Search box  why is it a "RelativeLayout" not a button?
    private RelativeLayout buttonSearch;

    //???
    private SmartRefreshLayout refreshLayout;

    //Setting an object from RecyclerView
    private RecyclerView recyclerView;

    /* Adapter for RecyclerView */
    private MainItemListAdapter adapter;

    /* Deserialized data from the server */
    // A list for receiving several sets of data from server
    private List<MainItemListBean.DataBean> dataBeans = new ArrayList<>();

    // Showing initial value of page
    private int page = 1;

    // Setting the initial status of login as false
    private boolean login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bonding variables with buttons, pictures and textView.
        findViews();

        //Jumping from activity_main to activity_search
        buttonSearch.setOnClickListener(v -> {
            // TODO: Implement the activity
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        //If login is false, jumping from activity_main to activity_login
        //If login is true, jumping to another xml
        imageHead.setOnClickListener(v -> {
            if (!login) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                // TODO: start another activity
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
            }
        });

        /* Initialize the adapter and add to RecyclerView */
        adapter = new MainItemListAdapter(this);
        recyclerView.setAdapter(adapter);
        /* Use GridLayout to display two columns */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        //Setting the number of items in each row
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });

        //Applying grid format in recyclerView
        recyclerView.setLayoutManager(gridLayoutManager);

        //???
        /* Init data, see if there is cache */
        initData();

        //???
        /* Start refreshing when starting the page */
        refreshLayout.autoRefresh();

        //refresh the interface
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            /* Refresh */
            page = 1;
            loadData();
        });

        //loading more items
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            /* Load more */
            page++;
            loadData();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtils.getInstance().getBoolean("login")) {
            login = true;
            String photo = SPUtils.getInstance().getString("photo");
            Glide.with(this).load(photo).into(imageHead);
        }
    }

    //Matching button with variables
    private void findViews() {
        imageHead = findViewById(R.id.main_head);
        buttonSearch = findViewById(R.id.main_search);
        refreshLayout = findViewById(R.id.main_refresh);
        recyclerView = findViewById(R.id.main_list_recycler);
    }

    //???
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

    //???
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

    //stopping refresh and load data
    private void endLoading() {
        if (refreshLayout.getState() == RefreshState.Refreshing) {
            refreshLayout.finishRefresh();
        } else if (refreshLayout.getState() == RefreshState.Loading) {
            refreshLayout.finishLoadMore();
        }
    }
}