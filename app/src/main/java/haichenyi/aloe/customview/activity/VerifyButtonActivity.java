package haichenyi.aloe.customview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.haichenyi.aloe.widget.VerificationButton;

import haichenyi.aloe.customview.R;

/**
 * @Title: VerifyButtonActivity
 * @Description:
 * @Author: wz
 * @Date: 2018/5/24
 * @Version: V1.0
 */
public class VerifyButtonActivity extends AppCompatActivity {
    private VerificationButton vbCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_button);
        vbCode = findViewById(R.id.vb_code);
        vbCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vbCode.onStart(6000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vbCode.onStop();
    }
}
