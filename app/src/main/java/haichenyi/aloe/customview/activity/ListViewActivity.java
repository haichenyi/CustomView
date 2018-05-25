package haichenyi.aloe.customview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.haichenyi.aloe.interfaces.LoadMoreInterface;
import com.haichenyi.aloe.widget.CustomSwipeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import haichenyi.aloe.customview.R;

/**
 * @Title: ListViewActivity
 * @Description: RecyclerView与ListView用法一样
 * @Author: wz
 * @Date: 2018/5/25
 * @Version: V1.0
 */
public class ListViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private List<String> dataList;
    private CustomSwipeLayout refreshLayout;
    private ListView listView;
    private int num;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        dataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            num++;
            dataList.add("第" + i + "个item");
        }
        refreshLayout = findViewById(R.id.swipeRefresh);
        listView = findViewById(R.id.list_view);
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnableLoadMore(true);
        final StringAdapter adapter = new StringAdapter();
        listView.setAdapter(adapter);
        View view = LayoutInflater.from(this).inflate(R.layout.item_load_more, null);
        refreshLayout.setLoadCustomView(view);
        refreshLayout.setLoadMoreListener(new LoadMoreInterface() {
            @Override
            public void loadMore() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*for (int i = num; i < 20; i++) {
                                    num++;
                                    dataList.add("第" + i + "个item");
                                }
                                adapter.notify();*/
                                refreshLayout.onLoadMoreFinish();
//                                ToastUtils.showTipMsg("加载完成!");
                            }
                        });
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onRefresh() {
//        ToastUtils.showTipMsg("刷新");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        }, 2000);
    }

    private class StringAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(ListViewActivity.this, R.layout.item, null);
            }
            TextView tv = (TextView) convertView;
            tv.setText(dataList.get(position));
            return convertView;
        }
    }
}
