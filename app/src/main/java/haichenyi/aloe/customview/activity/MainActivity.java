package haichenyi.aloe.customview.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import haichenyi.aloe.customview.R;

/**
 * @Title:
 * @Description:
 * @Author: wz
 * @Date: 2018/6/8
 * @Version: V1.0
 */
public class MainActivity extends AppCompatActivity {

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_wave_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WaveViewActivity.class));
            }
        });
        findViewById(R.id.btn_circle_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CircleProgressActivity.class));
            }
        });
        findViewById(R.id.btn_verification_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VerifyButtonActivity.class));
            }
        });
        findViewById(R.id.btn_verify_code_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VerifyCodeViewActivity.class));
            }
        });
        findViewById(R.id.btn_number_picker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NumberPickActivity.class));
            }
        });
        findViewById(R.id.btn_list_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListViewActivity.class));
            }
        });
        findViewById(R.id.btn_edit_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddEditTextActivity.class));
            }
        });
        findViewById(R.id.btn_auto_view_pager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AutoViewPagerActivity.class));
            }
        });
    }
}
