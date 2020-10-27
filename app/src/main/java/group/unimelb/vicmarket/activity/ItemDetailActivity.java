package group.unimelb.vicmarket.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.josephvuoto.customdialog.alert.CustomDialog;
import com.josephvuoto.customdialog.loading.LoadingDialog;

import java.util.Locale;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.CommonBean;
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
    private LinearLayout buttonLocation;
    private RelativeLayout buttonCall;
    private RelativeLayout buttonEmail;
    private RelativeLayout buttonDirection;

    private boolean liked = false;

    private int itemId = -1;

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

        itemId = getIntent().getIntExtra("ITEM_ID", -1);
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
                    itemId = itemDetailBean.getData().getItemId();

                    if (itemDetailBean.getData().isLiked()) {
                        liked = true;
                        buttonLike.setImageResource(R.drawable.ic_like_black);
                    } else {
                        liked = false;
                        buttonLike.setImageResource(R.drawable.ic_like);
                    }

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

                    buttonLocation.setOnClickListener(v -> {
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", itemDetailBean.getData().getLatitude(), itemDetailBean.getData().getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
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
            if (!SPUtils.getInstance().getBoolean("login")) {
                new CustomDialog.Builder(ItemDetailActivity.this)
                        .setTitle("Message")
                        .setMessage("Login to create wish list.")
                        .setOkButton("Login", dialog -> startActivity(new Intent(ItemDetailActivity.this, LoginActivity.class)))
                        .setCancelButton("Cancel", Dialog::dismiss)
                        .build()
                        .show();
                return;
            }
            if (liked) {
                RetrofitHelper.getInstance().deleteWishList(new Observer<CommonBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        loadingDialog.show();
                    }

                    @Override
                    public void onNext(@NonNull CommonBean commonBean) {
                        if (commonBean.getCode() == 200) {
                            buttonLike.setImageResource(R.drawable.ic_like);
                            liked = false;
                        } else {
                            ToastUtils.showShort("Network Error!");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        loadingDialog.dismiss();
                        ToastUtils.showShort("Network Error!");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }, itemId);
            } else {
                RetrofitHelper.getInstance().deleteWishList(new Observer<CommonBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        loadingDialog.show();
                    }

                    @Override
                    public void onNext(@NonNull CommonBean commonBean) {
                        if (commonBean.getCode() == 200) {
                            buttonLike.setImageResource(R.drawable.ic_like_black);
                            liked = true;
                        } else {
                            ToastUtils.showShort("Network Error!");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        loadingDialog.dismiss();
                        ToastUtils.showShort("Network Error!");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        loadingDialog.dismiss();
                    }
                }, itemId);
            }
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
        buttonLocation = findViewById(R.id.item_button_location);
    }
}