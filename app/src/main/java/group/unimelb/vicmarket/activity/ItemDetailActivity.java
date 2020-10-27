package group.unimelb.vicmarket.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.josephvuoto.customdialog.loading.LoadingDialog;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.ItemDetailBean;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class ItemDetailActivity extends AppCompatActivity {
    private LoadingDialog loadingDialog;

    private Toolbar toolbar;
    private ImageView buttonLike;
    private ImageView imageItem;
    private TextView textTitle;
    private TextView textPrice;
    private TextView textAddress;
    private ImageView imageHead;
    private TextView textDisplayName;
    private TextView textDescription;

    private RelativeLayout buttonCall;
    private RelativeLayout buttonEmail;
    private RelativeLayout buttonDirection;

    private boolean liked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        findViews();

        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        loadingDialog = new LoadingDialog.Builder(ItemDetailActivity.this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();

        int itemId = getIntent().getIntExtra("ITEM_ID", -1);
        Observer<ItemDetailBean> observer = new Observer<ItemDetailBean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                loadingDialog.show();
            }

            @Override
            public void onNext(@NonNull ItemDetailBean itemDetailBean) {
                if (itemDetailBean.getCode() == 200) {
                    textTitle.setText(itemDetailBean.getData().getTitle());
                    textAddress.setText(itemDetailBean.getData().getAddress());
                    textDescription.setText(itemDetailBean.getData().getDescription());
                    textPrice.setText("" + itemDetailBean.getData().getPrice());
                    textDisplayName.setText(itemDetailBean.getData().getSeller().getDisplayName());
                    Glide.with(ItemDetailActivity.this)
                            .load(itemDetailBean.getData().getUrls().get(0).getUrl())
                            .into(imageItem);
                    Glide.with(ItemDetailActivity.this)
                            .load(itemDetailBean.getData().getSeller().getPhoto())
                            .into(imageHead);

                    buttonCall.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + itemDetailBean.getData().getSeller().getPhone()));
                        startActivity(intent);
                    });

                    buttonEmail.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{itemDetailBean.getData().getSeller().getEmail()});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "RE: " + itemDetailBean.getData().getTitle());
                        startActivity(Intent.createChooser(intent, ""));
                    });

                    buttonDirection.setOnClickListener(v -> {
                        Uri navigation = Uri.parse("google.navigation:q=" + itemDetailBean.getData().getLatitude() + "," + itemDetailBean.getData().getLongitude() + "");
                        Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                        navigationIntent.setPackage("com.google.android.apps.maps");
                        startActivity(navigationIntent);
                    });
                }
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

        buttonLike.setOnClickListener(v -> {

        });
    }

    private void findViews() {
        toolbar = findViewById(R.id.item_detail_toolbar);
        buttonLike = findViewById(R.id.item_like);
        imageItem = findViewById(R.id.item_image);
        textTitle = findViewById(R.id.item_title);
        textAddress = findViewById(R.id.item_address);
        textDescription = findViewById(R.id.item_description);
        imageHead = findViewById(R.id.item_user_image);
        textDisplayName = findViewById(R.id.item_user_name);
        textPrice = findViewById(R.id.item_price);
        buttonCall = findViewById(R.id.item_call);
        buttonEmail = findViewById(R.id.item_email);
        buttonDirection = findViewById(R.id.item_direction);
    }
}