package group.unimelb.vicmarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.josephvuoto.customdialog.loading.LoadingDialog;

import java.io.File;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RegexUtils;
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
    private EditText phoneNum;
    private Button buttonRegister;
    private ImageView buttonBack;
    private ImageView photo;



    private LoadingDialog loadingDialog;

    Observer<UploadPicBean> uploadPicObserver;
    private String picLocation;
    private String picUrl = "https://img.xieyangzhe.com/img/2020-10-16/7e834bebf8551bc218d66bf88f552e57.jpg";


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
                            Uri uri = Uri.fromFile(new File(picLocation));
                            photo.setImageURI(uri);
                            picUrl = uploadPicBean.getData().get(0).getUrl();
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


                });

        buttonRegister.setOnClickListener(v -> {
            /* Get content typed in */
            String name = textName.getText().toString();
            String email = textEmail.getText().toString();
            String phone = phoneNum.getText().toString();
            String password = textPassword.getText().toString();
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
                        ToastUtils.showShort("Sign Up Successfully!");
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
            if (!RegexUtils.isUsername(name)){
                ToastUtils.showShort("Please enter the Correct Form of user name!");
            }else if(!RegexUtils.isEmail(email)){
                ToastUtils.showShort("Please enter the Correct Form of email!");
            }else if (!password.equals(passwordConfirm) ){
                ToastUtils.showShort("The passwords are not consistent!");
            }else if (!RegexUtils.isPassword(password)){
                ToastUtils.showShort("Please enter the Correct Form of password!");
            }else {
                RetrofitHelper.getInstance().doSignUp(signObserver, name, email,phone, password , picUrl);
            }


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
        phoneNum = findViewById(R.id.edit_phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
           picLocation =  data.getStringExtra("respond");
            /* Perform the HTTP request */
            RetrofitHelper.getInstance().uploadPic(uploadPicObserver , picLocation);

        }



    }
}