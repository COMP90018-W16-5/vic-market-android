package group.unimelb.vicmarket.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import group.unimelb.vicmarket.R;

public class AccountActivity extends AppCompatActivity {

    public ImageView userImage;
    public TextView userName;
    public TextView userPhone;
    public TextView userEmail;
    public TextView usrWishlist;
    public TextView usrMyposts;

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
    }

    private void findViews() {
        userImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_Name);
        userPhone = findViewById(R.id.user_phone);
        userEmail = findViewById(R.id.user_Email);
    }
}
