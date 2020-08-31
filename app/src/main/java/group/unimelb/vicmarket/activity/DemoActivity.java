package group.unimelb.vicmarket.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import group.unimelb.vicmarket.R;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        String email = getIntent().getStringExtra("email");
        String displayName = getIntent().getStringExtra("displayName");

        ((TextView) findViewById(R.id.text_demo)).setText("Hello, " + displayName);
    }
}