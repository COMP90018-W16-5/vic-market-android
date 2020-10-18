package group.unimelb.vicmarket.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.josephvuoto.customdialog.loading.LoadingDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;


import group.unimelb.vicmarket.GifSizeFilter;
import group.unimelb.vicmarket.R;
import group.unimelb.vicmarket.retrofit.LocationUtil;
import group.unimelb.vicmarket.retrofit.RegexUtils;
import group.unimelb.vicmarket.retrofit.RetrofitHelper;
import group.unimelb.vicmarket.retrofit.bean.PostItemBean;
import group.unimelb.vicmarket.retrofit.bean.UploadPicBean;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Toolbar toolbar;
    private EditText text_title;
    private  EditText text_description;
    private ImageView imagePicker;
    private Spinner spinnerCategory;
    private EditText text_price;
    private EditText text_location;
    private Button postButton;

    private double latitude;
    private double longitude;

    private static final int REQUEST_CODE_CHOOSE = 23;
    private String picLocation;
    private String picUrl = "https://img.xieyangzhe.com/img/default.jpg";
    private LoadingDialog loadingDialog;


    @SuppressLint("CheckResult")
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


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        spinnerCategory.setOnItemSelectedListener(this);


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

        postButton.setOnClickListener(v -> {
            /* Get content typed in */
            String title = text_title.getText().toString();
            String description = text_description.getText().toString();
            int category = 1;
            String price = text_price.getText().toString();
            String location = text_location.getText().toString();

            Observer<PostItemBean> postBeanObserver = new Observer<PostItemBean>() {

                @Override
                public void onSubscribe(Disposable d) {
                    loadingDialog.show();
                }

                @Override
                public void onNext(PostItemBean postBean) {
                    /* Received HTTP response and the JSON has been converted to Java object */
                    if (postBean.getCode() == 200) {
                        /* SignUp succeed, save the param for future requests */
                        finish();
                        ToastUtils.showShort("Post Successfully!");
                    } else {
                        ToastUtils.showShort(postBean.getMsg());
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
            if (title == null || title.equals("")){
                ToastUtils.showShort("Please enter the correct form of user name!");
            }
            else if (description == null || description.equals("")){
                ToastUtils.showShort("Please enter the correct form of description!");
            }
            else if (price == null || price.equals("")){
                ToastUtils.showShort("Please enter the correct form of price!");
            }
            else if (location == null || location.equals("")){
                ToastUtils.showShort("Please enter the correct form of location!");
            }
            else {
                RetrofitHelper.getInstance().PostItem(postBeanObserver,title, description,
                category, Double.parseDouble(price), location,latitude,longitude, picUrl);
            }


        });

    }

    public void findViews(){
        text_location = findViewById(R.id.text_location);
        imagePicker = findViewById(R.id.image_picker);
        toolbar = findViewById(R.id.post_toolbar);
        postButton = findViewById(R.id.post_button);
        text_title = findViewById(R.id.text_title);
        text_description = findViewById(R.id.text_description);
        spinnerCategory = findViewById(R.id.spin_category);
        text_price = findViewById(R.id.text_price);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.spin_category:
                Toast.makeText(parent.getContext(), "Category:" + text, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @SuppressLint("CheckResult")
    public void getCurrentLocation(){
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        LocationUtil locationUtil = LocationUtil.getInstance(this);
                        String addressline = locationUtil.getAddressLine();
                        latitude = locationUtil.getLatitude();
                        longitude = locationUtil.getLongitude();
                        Log.i("location", addressline);
                        text_location.setText(addressline);
                    }
                    else {
                        Toast.makeText(PostActivity.this, R.string.permission_request_denied, Toast.LENGTH_LONG)
                                .show();
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
//                    Uri uri = Uri.fromFile(new File(picLocation));
//                    imagePicker.setImageURI(uri);
//                    Log.i("image uri", String.valueOf(uri));
                    if (uploadPicBean.getCode()!=200||
                            uploadPicBean.getData()== null||
                            uploadPicBean.getData().isEmpty()){
                        ToastUtils.showShort("Unknown error");
                    }
                    else {
                        picUrl = uploadPicBean.getData().get(0).getUrl();
                        Glide.with(PostActivity.this).load(picUrl).into(imagePicker);
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
            RetrofitHelper.getInstance().uploadPic(uploadPicObserver , picLocation);
        }
    }

}
