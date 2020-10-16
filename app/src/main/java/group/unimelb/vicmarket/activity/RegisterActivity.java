package group.unimelb.vicmarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.josephvuoto.customdialog.loading.LoadingDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.SignUpBean;
import group.unimelb.vicmarket.retrofit.bean.UploadPicBean;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = RegisterActivity.class.getSimpleName();

    public final int REQUEST_SELECTPIC = 1;

    private EditText textName;
    private EditText textEmail;
    private EditText textPassword;
    private EditText textConfirmPassword;
    private Button buttonRegister;
    private ImageView buttonBack;
    private ImageView photo;



    private LoadingDialog loadingDialog;

    Observer<UploadPicBean> uploadPicObserver;
    private String picUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* Find views */
        findViews();

        /* Initialize loading dialog */
        loadingDialog = new LoadingDialog.Builder(RegisterActivity.this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();

        photo.setOnClickListener(v -> {

                    Intent intent = new Intent(RegisterActivity.this, UploadPicActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent,REQUEST_SELECTPIC);
                    uploadPicObserver = new Observer<UploadPicBean>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            loadingDialog.show();
                        }

                        @Override
                        public void onNext(UploadPicBean uploadPicBean) {
                            Uri uri = Uri.fromFile(new File(picUrl));
                            photo.setImageURI(uri);
                        }

                        @Override
                        public void onError(Throwable e) {

                            /* Error(HTTP error or JSON format error) */
                            e.printStackTrace();
                            /* Hide the loading dialog */
                            loadingDialog.dismiss();
                            ToastUtils.showShort("Unknown error");
                        }

                        @Override
                        public void onComplete() {
                            /* Hide the loading dialog */
                            loadingDialog.dismiss();
                        }
                    };


                }
        );

        buttonRegister.setOnClickListener(v -> {
            /* Get content typed in */
            String name = textName.getText().toString();
            String email = textEmail.getText().toString();
            String password = textPassword.getText().toString();
            String photoUrl;
            String passwordConfirm = textConfirmPassword.getText().toString();


            Observer<SignUpBean> signObserver = new Observer<SignUpBean>() {

                @Override
                public void onSubscribe(Disposable d) {
                    loadingDialog.show();
                }

                @Override
                public void onNext(SignUpBean signUpBean) {
                    /* Received HTTP response and the JSON has been converted to Java object */
                    if (signUpBean.getCode() == 200) {
                        /* SignUp succeed, save the param for future requests */
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        ToastUtils.showShort(signUpBean.getMsg());
                        startActivity(intent);
                    } else {
                        ToastUtils.showShort(signUpBean.getMsg());
                    }

                }

                @Override
                public void onError(Throwable e) {
                    /* Error(HTTP error or JSON format error) */
                    e.printStackTrace();
                    /* Hide the loading dialog */
                    loadingDialog.dismiss();
                    ToastUtils.showShort("Unknown error");
                }

                @Override
                public void onComplete() {
                    /* Hide the loading dialog */
                    loadingDialog.dismiss();
                }
            };
            /* Perform the HTTP request */
            RetrofitHelper.getInstance().doSignUp(signObserver, name, email, password);

        });
        buttonBack.setOnClickListener(v -> finish());

    }

    private void findViews() {
        textName = findViewById(R.id.edit_name);
        textEmail = findViewById(R.id.edit_email);
        textPassword = findViewById(R.id.edit_password);
        textConfirmPassword = findViewById(R.id.edit_password_again);
        buttonRegister = findViewById(R.id.button_Sign_up);
        buttonBack = findViewById(R.id.button_back);
        photo = findViewById(R.id.upload_photo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
           picUrl =  data.getStringExtra("respond");
            /* Perform the HTTP request */
            RetrofitHelper.getInstance().uploadPic(uploadPicObserver , picUrl);

        }



    }
}