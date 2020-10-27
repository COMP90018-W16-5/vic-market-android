package group.unimelb.vicmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.josephvuoto.customdialog.loading.LoadingDialog;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.SignInBean;
import group.unimelb.vicmarket.util.RegexUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();

    private EditText textEmail;
    private EditText textPassword;
    private Button buttonLogin;
    private TextView buttonRegister;
    private Toolbar toolbar;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* Find views */
        findViews();

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        /* Initialize loading dialog */
        loadingDialog = new LoadingDialog.Builder(LoginActivity.this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();

        /* Click event of login button */
        buttonLogin.setOnClickListener(v -> {
            /* Get email and password user typed in */
            String email = textEmail.getText().toString();
            String password = textPassword.getText().toString();

            /* Initialize an observer */
            Observer<SignInBean> observer = new Observer<SignInBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                    loadingDialog.show();
                }

                @Override
                public void onNext(SignInBean signInBean) {
                    /* Received HTTP response and the JSON has been converted to Java object */
                    if (signInBean.getCode() == 200) {
                        /* Login succeed, save the token for future requests */
                        RetrofitHelper.getInstance().setToken(signInBean.getData().getToken());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        SPUtils.getInstance().put("login", true);
                        SPUtils.getInstance().put("name", signInBean.getData().getDisplayName());
                        SPUtils.getInstance().put("email", signInBean.getData().getEmail());
                        SPUtils.getInstance().put("phone", signInBean.getData().getPhone());
                        SPUtils.getInstance().put("photo", signInBean.getData().getPhoto());
                        SPUtils.getInstance().put("token", signInBean.getData().getToken());
                        SPUtils.getInstance().put("password", password);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        /* Login failed. Shows message from the server */
                        ToastUtils.showShort(signInBean.getMsg());
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
            if (!RegexUtils.isEmail(email)) {
                ToastUtils.showShort("Please enter the Correct Form of email!");
            } else {
                RetrofitHelper.getInstance().doLogin(observer, email, password);
            }
        });

        buttonRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,
                RegisterActivity.class)));
    }

    private void findViews() {
        textEmail = findViewById(R.id.edit_email);
        textPassword = findViewById(R.id.edit_password);
        buttonLogin = findViewById(R.id.button_login);
        buttonRegister = findViewById(R.id.button_register);
        toolbar = findViewById(R.id.login_toolbar);
    }
}