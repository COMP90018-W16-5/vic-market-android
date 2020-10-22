package group.unimelb.vicmarket.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.josephvuoto.customdialog.loading.LoadingDialog;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.ItemDetailBean;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class ItemDetailActivity extends AppCompatActivity {
    private int itemId;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        findViews();
        loadingDialog = new LoadingDialog.Builder(ItemDetailActivity.this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();

        itemId = getIntent().getIntExtra("ITEM_ID", -1);
        Observer<ItemDetailBean> observer = new Observer<ItemDetailBean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                loadingDialog.show();
            }

            @Override
            public void onNext(@NonNull ItemDetailBean itemDetailBean) {
                // TODO: finish page layout
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                loadingDialog.dismiss();
            }

            @Override
            public void onComplete() {
                loadingDialog.dismiss();
            }
        };
        if (itemId == -1) {
            // Random item
            RetrofitHelper.getInstance().getRandomItem(observer);
        } else {
            RetrofitHelper.getInstance().getItemDetails(observer, itemId);
        }
    }

    private void findViews() {
        // TODO
    }
}