package haichenyi.aloe.customview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.haichenyi.aloe.widget.VerificationCodeView;

import haichenyi.aloe.customview.R;

/**
 * @Title: VerifyCodeViewActivity
 * @Description:
 * @Author: wz
 * @Date: 2018/5/24
 * @Version: V1.0
 */
public class VerifyCodeViewActivity extends AppCompatActivity {
    VerificationCodeView codeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code_view);
        codeView = findViewById(R.id.code_view);
        codeView.setOnCompleteListener(new VerificationCodeView.OnCompleteListener() {
            @Override
            public void onComplete(String content) {
                Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
                codeView.cleanTxt();
            }
        });
    }
}
