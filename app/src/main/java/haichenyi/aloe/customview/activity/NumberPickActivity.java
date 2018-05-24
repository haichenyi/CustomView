package haichenyi.aloe.customview.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haichenyi.aloe.widget.PickTimeView;
import com.haichenyi.aloe.widget.PickValueView;

import java.text.SimpleDateFormat;

import haichenyi.aloe.customview.R;

/**
 * @Title: NumberPickActivity
 * @Description:
 * @Author: wz
 * @Date: 2018/5/24
 * @Version: V1.0
 */
public class NumberPickActivity extends AppCompatActivity implements PickTimeView.onSelectedChangeListener, PickValueView.onSelectedChangeListener {
    private LinearLayout pvLayout;
    private PickTimeView pickTime;
    private PickTimeView pickDate;
    private PickValueView pickValue;
    private PickValueView pickValues;
    private PickValueView pickString;
    private TextView tvSelected;
    private SimpleDateFormat sdfTime;
    private SimpleDateFormat sdfDate;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_pick);
        pvLayout = findViewById(R.id.Main_pvLayout);
        pickTime = findViewById(R.id.pickTime);
        pickDate = findViewById(R.id.pickDate);
        pickValue = findViewById(R.id.pickValue);
        pickValues = findViewById(R.id.pickValues);
        pickString = findViewById(R.id.pickString);

        tvSelected = findViewById(R.id.Main_tvSelected);

        pickTime.setViewType(PickTimeView.TYPE_PICK_TIME);
        pickDate.setViewType(PickTimeView.TYPE_PICK_DATE);

        sdfTime = new SimpleDateFormat("MM-dd EEE HH:mm");
        sdfDate = new SimpleDateFormat("yyyy-MM-dd");

        pickTime.setOnSelectedChangeListener(this);
        pickDate.setOnSelectedChangeListener(this);
        pickValue.setOnSelectedChangeListener(this);
        pickValues.setOnSelectedChangeListener(this);
        pickString.setOnSelectedChangeListener(this);

        initData();
    }

    private void initData() {
        pickTime.setViewType(PickTimeView.TYPE_PICK_TIME);
        pickDate.setViewType(PickTimeView.TYPE_PICK_DATE);

        Integer value[] = new Integer[20];
        for (int i = 0; i < value.length; i++) {
            value[i] = i + 1;
        }
        Integer middle[] = new Integer[15];
        for (int i = 0; i < middle.length; i++) {
            middle[i] = i + 1;
        }
        Integer right[] = new Integer[10];
        for (int i = 0; i < right.length; i++) {
            right[i] = i;
        }
        String[] valueStr = new String[]{"跑步", "散步", "打篮球", "游泳", "广场舞", "太极拳"};

        pickValue.setValueData(value, value[4]);
        pickValues.setValueData(value, value[0], middle, middle[0], right, right[0]);
        pickString.setValueData(valueStr, valueStr[1]);
    }

    private void showView(View view) {
        for (int i = 0; i < pvLayout.getChildCount(); i++) {
            pvLayout.getChildAt(i).setVisibility(View.GONE);
        }
        view.setVisibility(View.VISIBLE);
    }


    @Override
    public void onSelected(PickTimeView view, long timeMillis) {
        if (view == pickTime) {
            String str = sdfTime.format(timeMillis);
            tvSelected.setText(str);
        } else if (view == pickDate) {
            String str = sdfDate.format(timeMillis);
            tvSelected.setText(str);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSelected(PickValueView view, Object leftValue, Object middleValue, Object rightValue) {
        if (view == pickValue) {
            int left = (int) leftValue;
            tvSelected.setText("selected:" + left);
        } else if (view == pickValues) {
            int left = (int) leftValue;
            int middle = (int) middleValue;
            int right = (int) rightValue;
            tvSelected.setText("selected: left:" + left + "  middle:" + middle + "  right:" + right);
        } else {
            String selectedStr = (String) leftValue;
            tvSelected.setText(selectedStr);
        }
    }

    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.Main_btnTime:
                showView(pickTime);
                break;
            case R.id.Main_btnDat:
                showView(pickDate);
                break;
            case R.id.Main_btnValue:
                showView(pickValue);
                break;
            case R.id.Main_btnValues:
                showView(pickValues);
                break;
            case R.id.Main_btnType:
                showView(pickString);
                break;
        }
    }
}
