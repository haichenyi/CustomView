package haichenyi.aloe.customview.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.haichenyi.aloe.interfaces.ViewPagerClickListener;
import com.haichenyi.aloe.widget.AutoViewPager;

import java.util.ArrayList;
import java.util.List;

import haichenyi.aloe.customview.R;

/**
 * @Title:
 * @Description:
 * @Author: wz
 * @Date: ${date}
 * @Version: V1.0
 */

public class AutoViewPagerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_view_pager);
        AutoViewPager autoViewPager = findViewById(R.id.auto_view_pager);
        autoViewPager.setAuto(true);
        autoViewPager.setAutoTime(2000);
        final List<TextView> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TextView textView = new TextView(this);
            textView.setText("this is the " + i + " item");
            list.add(textView);
        }
        PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(list.get(position));
                return list.get(position);
            }
        };
        autoViewPager.setAdapter(adapter);
        autoViewPager.addOnClickListener(new ViewPagerClickListener() {
            @Override
            public void onClick(int position) {
                Log.e("wz", position + "");
                Toast.makeText(AutoViewPagerActivity.this, position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * AutoViewPager真正的适配器
     */
    private class MyPagerAdapter extends PagerAdapter {
        private PagerAdapter pa;

        public MyPagerAdapter(PagerAdapter pa) {
            this.pa = pa;
        }

        @Override
        public int getCount() {
            return pa.getCount() > 1 ? pa.getCount() + 2 : pa.getCount();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (position == 0) {
                return pa.instantiateItem(container, pa.getCount() - 1);
            } else if (position == pa.getCount() + 1) {
                return pa.instantiateItem(container, 0);
            } else {
                return pa.instantiateItem(container, position - 1);
            }
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // TODO Auto-generated method stub
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
            return pa.isViewFromObject(arg0, arg1);
        }
    }
}
