package group.unimelb.vicmarket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.josephvuoto.customdialog.loading.LoadingDialog;

import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.SignInBean;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();

    private EditText textEmail;
    private EditText textPassword;
    private Button buttonLogin;
    private TextView viewRegister;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        loadingDialog = new LoadingDialog.Builder(LoginActivity.this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();

        buttonLogin.setOnClickListener(v -> {
            String email = textEmail.getText().toString();
            String password = textPassword.getText().toString();

            Observer<SignInBean> observer = new Observer<SignInBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                    loadingDialog.show();
                }

                @Override
                public void onNext(SignInBean signInBean) {
                    if (signInBean.getCode() == 200) {
                        RetrofitHelper.getInstance().setToken(signInBean.getData().getToken());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("email", signInBean.getData().getEmail());
                        intent.putExtra("displayName", signInBean.getData().getDisplayName());
                        startActivity(intent);
                    } else {
                        ToastUtils.showShort(signInBean.getMsg());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    loadingDialog.dismiss();
                    ToastUtils.showShort("Unknown error");
                }

                @Override
                public void onComplete() {
                    loadingDialog.dismiss();
                }
            };
            RetrofitHelper.getInstance().doLogin(observer, email, password);
        });

        viewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });
    }

    private void findViews() {
        textEmail = findViewById(R.id.edit_email);
        textPassword = findViewById(R.id.edit_password);
        buttonLogin = findViewById(R.id.button_register);
        viewRegister = findViewById(R.id.tv_register);
    }
}