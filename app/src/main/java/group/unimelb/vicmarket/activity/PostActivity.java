package group.unimelb.vicmarket.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ToastUtils;
import com.josephvuoto.customdialog.loading.LoadingDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;


import group.unimelb.vicmarket.GifSizeFilter;
import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.LocationUtil;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.UploadPicBean;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private EditText locationField;
    private ImageView imagePicker;

    private static final int REQUEST_CODE_CHOOSE = 23;
    private String picLocation;
    private String picUrl = "https://img.xieyangzhe.com/img/default.jpg";
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        findViews();

        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("Post Item");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());



        getCurrentLocation();


        loadingDialog = new LoadingDialog.Builder(PostActivity.this)
                .setLoadingText("Loading...")
                .setCanceledOnTouchOutside(false)
                .build();


        Spinner spinnerCate = (Spinner) findViewById(R.id.spin_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCate.setAdapter(adapter);
        spinnerCate.setOnItemSelectedListener(this);

        Spinner spinnerPrice = (Spinner) findViewById(R.id.spin_price);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.array_price, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrice.setAdapter(adapter1);
        spinnerPrice.setOnItemSelectedListener(this);

        imagePicker.setOnClickListener(v -> {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(aBoolean -> {
                        if (aBoolean) {
                            Matisse.from(PostActivity.this)
                                    .choose(MimeType.ofImage(), false)
                                    .countable(false)
                                    .capture(false)
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
                        } else {
                            Toast.makeText(PostActivity.this, R.string.permission_request_denied, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }, Throwable::printStackTrace);

        });
    }

    public void findViews(){
        locationField = findViewById(R.id.text_location);
        imagePicker = findViewById(R.id.image_picker);
        toolbar = findViewById(R.id.post_toolbar);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.spin_category:
                Toast.makeText(parent.getContext(), "Category:" + text, Toast.LENGTH_SHORT).show();
                break;
            case R.id.spin_price:
                Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void getCurrentLocation(){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        LocationUtil locationUtil = LocationUtil.getInstance(this);
                        Log.i("location", locationUtil.getAddressLine());
                    }
                    else {

                    }
                });
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
                    imagePicker.setImageURI(uri);
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
