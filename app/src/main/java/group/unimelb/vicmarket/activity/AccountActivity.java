package group.unimelb.vicmarket.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import group.unimelb.vicmarket.R;

public class AccountActivity extends AppCompatActivity {

    private ImageView userImage;
    private TextView userName;
    private TextView userPhone;
    private TextView userEmail;
    private Toolbar toolbar;
    private RelativeLayout layoutWishList;
    private RelativeLayout layoutMyPosts;


    @Override
    protected void onCreate(Bundle savedInstanceState){
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

        layoutWishList.setOnClickListener(v -> {
            // TODO: open wishlist page
        });

        layoutMyPosts.setOnClickListener(v -> {
            // TODO: open my post page
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
    }
}