package group.unimelb.vicmarket.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.josephvuoto.customdialog.alert.CustomDialog;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;

public class AccountActivity extends AppCompatActivity {

    private ImageView userImage;
    private TextView userName;
    private TextView userPhone;
    private TextView userEmail;
    private Toolbar toolbar;
    private RelativeLayout layoutWishList;
    private RelativeLayout layoutMyPosts;
    private RelativeLayout layoutLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        findViews();
        SPUtils spUtils = SPUtils.getInstance();
        //Log.i(spUtils.getString("name"),"test");

        Glide.with(this).load(spUtils.getString("photo")).into(userImage);
        userName.setText(spUtils.getString("name"));
        userPhone.setText(spUtils.getString("phone"));
        userEmail.setText(spUtils.getString("email"));

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("User Account");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        layoutWishList.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, WishlistActivity.class)));

        layoutMyPosts.setOnClickListener(v -> startActivity(new Intent(AccountActivity.this, MyPostActivity.class)));

        layoutLogout.setOnClickListener(v -> {
            new CustomDialog.Builder(AccountActivity.this)
                    .setTitle("Logout")
                    .setMessage("Ready to logout?")
                    .setCancelButton("Cancel", Dialog::dismiss)
                    .setOkButton("Confirm", dialog -> {
                        SPUtils.getInstance().put("login", false);
                        SPUtils.getInstance().put("name", "");
                        SPUtils.getInstance().put("email", "");
                        SPUtils.getInstance().put("phone", "");
                        SPUtils.getInstance().put("photo", "");
                        SPUtils.getInstance().put("token", "");
                        RetrofitHelper.getInstance().setToken("");
                        ToastUtils.showShort("Logged out");
                        Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }).build().show();
        });
    }

    private void findViews() {
        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_Name);
        userPhone = findViewById(R.id.user_phone);
        userEmail = findViewById(R.id.user_email);
        toolbar = findViewById(R.id.account_toolbar);
        layoutMyPosts = findViewById(R.id.myposts_layout);
        layoutWishList = findViewById(R.id.wishlist_layout);
        layoutLogout = findViewById(R.id.logout_layout);
    }
}