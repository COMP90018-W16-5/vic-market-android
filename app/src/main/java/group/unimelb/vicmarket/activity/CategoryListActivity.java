package group.unimelb.vicmarket.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.josephvuoto.customdialog.loading.LoadingDialog;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.adapter.CategoryListAdapter;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.CategoriesBean;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class CategoryListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CategoryListAdapter adapter;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        findViews();

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("Categories");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new CategoryListAdapter(this);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        /* Initialize loading dialog */
        loadingDialog = new LoadingDialog.Builder(this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();
        loadData();
    }

    private void loadData() {
        RetrofitHelper.getInstance().getCategories(new Observer<CategoriesBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                loadingDialog.show();
            }

            @Override
            public void onNext(CategoriesBean categoriesBean) {
                adapter.setData(categoriesBean.getData());
            }

            @Override
            public void onError(Throwable e) {
                loadingDialog.dismiss();
                e.printStackTrace();
                ToastUtils.showLong("Network error");
            }

            @Override
            public void onComplete() {
                loadingDialog.dismiss();
            }
        });
    }

    private void findViews() {
        toolbar = findViewById(R.id.cate_list_toolbar);
        recyclerView = findViewById(R.id.cate_list_recycler);
    }
}