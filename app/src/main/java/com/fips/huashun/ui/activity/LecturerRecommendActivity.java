package com.fips.huashun.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fips.huashun.R;
import com.fips.huashun.common.Constants;
import com.fips.huashun.modle.bean.TeacherCourse;
import com.fips.huashun.net.HttpUtil;
import com.fips.huashun.net.LoadDatahandler;
import com.fips.huashun.net.LoadJsonHttpResponseHandler;
import com.fips.huashun.ui.adapter.LecturerResultAdapter;
import com.fips.huashun.ui.utils.ToastUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 功能：推荐讲师
 * Created by Administrator on 2016/8/19.
 *
 * @author 张柳 时间：2016年8月19日16:50:01
 */
public class LecturerRecommendActivity extends BaseActivity implements OnClickListener
{
    // 返回
    private LinearLayout backLl;
    //
    private PullToRefreshListView pullToRefreshListView;
    // 列表
    private ListView mListView;
    // 适配器
    private LecturerResultAdapter lecturerResultAdapter;
    // 数据集合
    private List<TeacherCourse> list;
    // 通知
    private ToastUtil toastUtil;
    // json解析
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_recommend);
        gson = new Gson();
        toastUtil = ToastUtil.getInstant();
        initView();
        showLoadingDialog();
        initData();
    }

    @Override
    protected void initView()
    {
        super.initView();

        backLl = (LinearLayout) findViewById(R.id.ll_back);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_lecturer_recommend);
        mListView = pullToRefreshListView.getRefreshableView();
        // 两端刷新
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        lecturerResultAdapter = new LecturerResultAdapter(LecturerRecommendActivity.this);
        mListView.setAdapter(lecturerResultAdapter);
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intentToDetail = new Intent(LecturerRecommendActivity.this, LecturerDetailActivity.class);
                intentToDetail.putExtra("teacherId", list.get(position-1).getTeacherId());
                startActivity(intentToDetail);
            }
        });
        backLl.setOnClickListener(this);
    }

    /**
     * 功能：获取推荐讲师列表信息
     */
    private void initData()
    {
        RequestParams requestParams = new RequestParams();
        HttpUtil.post(Constants.LECTURER_ALL_URL, requestParams, new LoadJsonHttpResponseHandler(this, new LoadDatahandler()
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
                    Log.e("data", "data=" + data.toString());
                    if ("y".equals(suc))
                    {
                        // 获取数据
                        list = gson.fromJson(data.getString("teacherList"), new TypeToken<List<TeacherCourse>>(){}.getType());
                        lecturerResultAdapter.setListItems(list);
                        lecturerResultAdapter.notifyDataSetChanged();
                    } else
                    {
                        toastUtil.show(msg);
                    }
                    // 结束刷新
                    pullToRefreshListView.onRefreshComplete();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String error, String message)
            {
                super.onFailure(error, message);
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
    public void onClick(View v)
    {
        if (v.getId() == R.id.ll_back)
        {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LecturerRecommendActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LecturerRecommendActivity");
    }
}
