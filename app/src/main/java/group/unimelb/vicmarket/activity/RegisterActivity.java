package group.unimelb.vicmarket.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ToastUtils;
import com.josephvuoto.customdialog.loading.LoadingDialog;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;

import group.unimelb.vicmarket.GifSizeFilter;
import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RegexUtils;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.SignUpBean;
import group.unimelb.vicmarket.retrofit.bean.UploadPicBean;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RegisterActivity extends AppCompatActivity {
    private final static String TAG = RegisterActivity.class.getSimpleName();

    private static final int REQUEST_CODE_CHOOSE = 23;

    private EditText textName;
    private EditText textEmail;
    private EditText textPassword;
    private EditText textConfirmPassword;
    private EditText phoneNum;
    private Button buttonRegister;
    private ImageView photo;
    private Toolbar toolbar;

    private LoadingDialog loadingDialog;

    private String picLocation;
    private String picUrl = "https://img.xieyangzhe.com/img/default.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /* Find views */
        findViews();

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        /* Initialize loading dialog */
        loadingDialog = new LoadingDialog.Builder(RegisterActivity.this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();

        photo.setOnClickListener(v -> {
            Matisse.from(RegisterActivity.this)
                    .choose(MimeType.ofImage(), false)
                    .countable(true)
                    .capture(true)
                    .captureStrategy(
                            new CaptureStrategy(true, "group.unimelb.vicmarket.activity.fileprovider", "test"))
                    .maxSelectable(1)
                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                    .gridExpectedSize(
                            getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .setOnSelectedListener((uriList, pathList) -> {
                        Log.e("onSelected", "onSelected: pathList=" + pathList);
                    })
                    .showSingleMediaType(true)
                    .originalEnable(true)
                    .maxOriginalSize(10)
                    .autoHideToolbarOnSingleTap(true)
                    .setOnCheckedListener(isChecked -> {
                        Log.e("isChecked", "onCheck: isChecked=" + isChecked);
                    })
                    .theme(R.style.Matisse_Market)
                    .forResult(REQUEST_CODE_CHOOSE);
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
    }

    private void findViews() {
        textName = findViewById(R.id.edit_name);
        textEmail = findViewById(R.id.edit_email);
        textPassword = findViewById(R.id.edit_password);
        textConfirmPassword = findViewById(R.id.edit_password_again);
        buttonRegister = findViewById(R.id.button_Sign_up);
        photo = findViewById(R.id.upload_photo);
        phoneNum = findViewById(R.id.edit_phone);
        toolbar = findViewById(R.id.register_toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            // Get picture location and upload the picture
            picLocation = Matisse.obtainPathResult(data).get(0);
            Observer<UploadPicBean> uploadPicObserver = new Observer<UploadPicBean>() {

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
            RetrofitHelper.getInstance().uploadPic(uploadPicObserver , picLocation);
        }
    }
}