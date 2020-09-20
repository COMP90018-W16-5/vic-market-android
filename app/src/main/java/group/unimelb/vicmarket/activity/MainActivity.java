package group.unimelb.vicmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.adapter.MainItemListAdapter;
import group.unimelb.vicmarket.retrofit.bean.MainItemListBean;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private ImageView imageHead;
    private RelativeLayout buttonSearch;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private MainItemListAdapter adapter;
    private List<MainItemListBean.DataBean> dataBeans = new ArrayList<>();

    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        buttonSearch.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        imageHead.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        adapter = new MainItemListAdapter(this);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
//                LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);

        initData();
//
//        refreshLayout.autoRefresh();
//
//        refreshLayout.setOnRefreshListener(refreshlayout -> {
//            page = 1;
//            loadData();
//        });
//        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
//            page++;
//            loadData();
//        });
    }

    private void findViews() {
        imageHead = findViewById(R.id.main_head);
        buttonSearch = findViewById(R.id.main_search);
        refreshLayout = findViewById(R.id.main_refresh);
        recyclerView = findViewById(R.id.main_list_recycler);
    }

    private void initData() {
        dataBeans.clear();
        for (int i = 0; i < 30; i++) {
            String json = "{\"code\":200,\"msg\":\"success\",\"data\":[{\"itemId\":1,\"title\":\"Nintendo Switch Console - Neon\",\"description\":\"Get the gaming system that lets you play the games you want, wherever you are, however you like.\",\"price\":469,\"latitude\":123.123445,\"longitude\":123.123445,\"urls\":[{\"imid\":1,\"url\":\"https://www.bigw.com.au/medias/sys_master/images/images/h89/h89/14106587693086.jpg\"},{\"imid\":2,\"url\":\"https://www.bigw.com.au/medias/sys_master/images/images/h89/h89/14106587693086.jpg\"}],\"status\":0}],\"page\":1,\"hasNext\":false,\"hasPrevious\":false}";
            MainItemListBean bean = new Gson().fromJson(json, MainItemListBean.class);
            dataBeans.addAll(bean.getData());
            adapter.setData(dataBeans);
        }
    }
}