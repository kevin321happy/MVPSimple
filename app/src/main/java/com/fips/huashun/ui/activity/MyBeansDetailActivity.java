package com.fips.huashun.ui.activity;


import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.fips.huashun.R;
import com.fips.huashun.common.Constants;
import com.fips.huashun.modle.bean.BeansInfo;
import com.fips.huashun.net.HttpUtil;
import com.fips.huashun.net.LoadDatahandler;
import com.fips.huashun.net.LoadJsonHttpResponseHandler;
import com.fips.huashun.ui.adapter.MyBeansDetailAdapter;
import com.fips.huashun.ui.utils.NavigationBar;
import com.fips.huashun.ui.utils.PreferenceUtils;
import com.fips.huashun.ui.utils.ToastUtil;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 功能：我的魔豆明细
 * Created by Administrator on 2016/9/28.
 *
 * @author 张柳 时间：2016年9月28日09:17:46
 */
public class MyBeansDetailActivity extends BaseActivity
{
    private NavigationBar navigationBar;
    // 上下拉的控件
    private PullToRefreshListView pullToRefreshListView;
    // 列表
    private ListView mListView;
    // 适配器
    private MyBeansDetailAdapter myBeansDetailAdapter;
    // 数据保存
    private List<BeansInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybeans_detail);
        initView();
        showLoadingDialog();
        initData();
    }

    @Override
    protected void initView()
    {
        super.initView();
        navigationBar = (NavigationBar) findViewById(R.id.nb_mybeans_detail);
        navigationBar.setTitle("魔豆明细");
        navigationBar.setLeftImage(R.drawable.fanhui);
        navigationBar.setListener(new NavigationBar.NavigationListener()
        {
            @Override
            public void onButtonClick(int button)
            {
                if (button == NavigationBar.LEFT_VIEW)
                {
                    finish();
                }
            }
        });
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_mybeans_detail);
        // 获取listview
        mListView = pullToRefreshListView.getRefreshableView();
        // 两端刷新
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        myBeansDetailAdapter = new MyBeansDetailAdapter(MyBeansDetailActivity.this);
        // 设置适配器
        mListView.setAdapter(myBeansDetailAdapter);

        // 设置上下拉事件
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                initData();
            }
        });
    }

    /**
     * 功能：获取所有的魔豆明细
     */
    private void initData()
    {
        RequestParams requestParams = new RequestParams();
        requestParams.put("userid", PreferenceUtils.getUserId());
        HttpUtil.post(Constants.MY_BEANS_DETAIL_URL, requestParams, new LoadJsonHttpResponseHandler(MyBeansDetailActivity.this, new LoadDatahandler()
        {
            @Override
            public void onStart()
            {
                super.onStart();
            }

            @Override
            public void onSuccess(JSONObject data)
            {
                super.onSuccess(data);
                dimissLoadingDialog();
                try
                {
                    String suc = data.get("suc").toString();
                    String msg = data.get("msg").toString();
                    if ("y".equals(suc))
                    {
                        list = gson.fromJson(data.getString("data"), new TypeToken<List<BeansInfo>>(){}.getType());
                        myBeansDetailAdapter.setListItems(list);
                        myBeansDetailAdapter.notifyDataSetChanged();

                    } else
                    {
                        ToastUtil.getInstant().show(msg);
                    }
                    // 结束刷新
                    pullToRefreshListView.onRefreshComplete();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.e("JSONException", "JSONException");
                    dimissLoadingDialog();
                }
            }

            @Override
            public void onFailure(String error, String message)
            {
                super.onFailure(error, message);
                Log.e("onFailure", "onFailure");
                dimissLoadingDialog();
                // 结束刷新
                pullToRefreshListView.onRefreshComplete();
            }
        }));
    }

    @Override
    public boolean isSystemBarTranclucent()
    {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyBeansDetailActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MyBeansDetailActivity");
    }
}
