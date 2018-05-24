package haichenyi.aloe.customview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.haichenyi.aloe.widget.CircleProgressView;

import haichenyi.aloe.customview.R;

/**
 * @Title: CircleProgressActivity
 * @Description: 圆形进度
 * @Author: wz
 * @Date: 2018/5/24
 * @Version: V1.0
 */
public class CircleProgressActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_progress);
        final CircleProgressView circleProgressView = findViewById(R.id.circle_progress);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleProgressView.setProgress(0.5f);
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleProgressView.setProgress(1f);
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleProgressView.startSuccess("绑定成功！");
            }
        });
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleProgressView.startFailed("绑定失败！");
            }
        });
    }
}
