package group.unimelb.vicmarket.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import group.unimelb.vicmarket.R;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    public static final int REQUEST_CODE = 101;
    //    private BottomSheetListener mListener;
    Button cameraBtn;
    Button galleryBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout,container,false);
        cameraBtn = v.findViewById(R.id.cameraBtn);
        galleryBtn = v.findViewById(R.id.galleryBtn);

        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                askCameraPermission();
//                Toast.makeText(getContext(),"1111",Toast.LENGTH_SHORT).show();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"1111",Toast.LENGTH_SHORT).show();
//                Toast.makeText(,"1111",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(BottomSheetDialog,new String[] {Manifest.permission.CAMERA}, REQUEST_CODE);
        }

    }

//    public interface BottomSheetListener{
//        void onButtonClicked(String text);
//
//    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try{
//            mListener = (BottomSheetListener) context;
//        }catch (ClassCastException e){
//            throw new ClassCastException(context.toString()
//            + "must implement BottomSheetListener");
//        }
//
//
//    }
}
