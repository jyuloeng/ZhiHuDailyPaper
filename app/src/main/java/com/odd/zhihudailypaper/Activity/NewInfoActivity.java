package com.odd.zhihudailypaper.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.odd.zhihudailypaper.Bean.NewsListBean;
import com.odd.zhihudailypaper.Bean.UserBean;
import com.odd.zhihudailypaper.Controls.BottomButton;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.BaseUtils;
import com.odd.zhihudailypaper.Utils.HttpUtils;
import com.odd.zhihudailypaper.Utils.JsonUtils;
import com.odd.zhihudailypaper.Utils.NetUtils;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private String URL_DATAS = "https://news-at.zhihu.com/api/4/story-extra/";
    private String URL_COLLECT = "http://test.xkspbz.com/odd.zhihudaily/collectNews.php?account=";
    private String URL_UNCOLLECT = "http://test.xkspbz.com/odd.zhihudaily/unCollectNews.php?account=";
    private String URL_ISCOLLECT = "http://test.xkspbz.com/odd.zhihudaily/isCollect.php?account=";

    private Intent mIntent;

    private String new_id = "";
    private String new_url = "";
    private String comments = "";
    private String popularity = "";

    private Activity mActivity;
    private WebView mWebView;
    private Window mWindow;
    private Button mButton;

    private BottomButton bar_pinglun,bar_dianzan,bar_shoucang,bar_fenxiang,bar_quxiaoshoucang;

    private final int TAG_SHARE_WECHAT_FRIENDS = 0;
    private final int TAG_SHARE_WECHAR_MOMENTS = 1;
    private final int TAG_SHARE_SINA = 2;
    private final int TAG_SHARE_QQ = 3;
    private final int TAG_SHARE_COPY = 4;
    private final int TAG_SHARE_MORE = 5;

    private boolean isLogin = false;

    private UserBean userBean;
    private NewsListBean newsListBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_info);
        mActivity = this;

        //  拿用户信息
        userBean = BaseUtils.getUserData(this);
        isLogin = BaseUtils.isLogin(userBean);

        initWindow();
        initData();
        initView();

        isCollect(userBean.getAccount(),new_id);
    }

    /**
     * 初始化要加载的数据
     */
    private void initData() {
        mIntent = getIntent();

        newsListBean = (NewsListBean)mIntent.getSerializableExtra("newsBean");

        new_id = newsListBean.getNewId();
        new_url = newsListBean.getUrl();

        URL_DATAS += new_id;

        if(NetUtils.isNetworkAvailable(this)) {

            //  开一个新线程加载网络操作
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String responseData = HttpUtils.GetOkHttpResponseData(URL_DATAS);
                    parseJsonWithJsonObject(responseData);

                    //  开Ui线程更新Ui
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_pinglun.setText(comments);
                            bar_dianzan.setText(popularity);
                        }
                    });
                }
            }).start();

            initWebView();

        }else{
            //  弹出没有网络的提醒
            BaseUtils.showAlertDialog(this,
                    SweetAlertDialog.WARNING_TYPE,
                    "Sorry，您没有网络~",
                    "没有网络还看什么新闻",
                    "好吧");
        }
    }

    /**
     * 初始化底部控件
     */
    private void initView() {
        mButton = findViewById(R.id.btn_back);
        bar_pinglun = findViewById(R.id.bar_pinglun);
        bar_dianzan = findViewById(R.id.bar_dianzan);
        bar_shoucang = findViewById(R.id.bar_shoucang);
        bar_fenxiang = findViewById(R.id.bar_fenxiang);
        bar_quxiaoshoucang = findViewById(R.id.bar_quxiaoshoucang);

        bar_pinglun.setImage(R.mipmap.ic_pinglun);
        bar_dianzan.setImage(R.mipmap.ic_dianzan);
        bar_shoucang.setImage(R.mipmap.ic_shoucang);
        bar_fenxiang.setImage(R.mipmap.ic_fenxiang);
        bar_quxiaoshoucang.setImage(R.mipmap.ic_quxiaoshoucang);

        initClickEvent();
    }

    /**
     * 初始化要加载的webview页面
     */
    private void initWebView() {
        mWebView = findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        //设置自适应屏幕，两者合用
        mWebView.getSettings().setUseWideViewPort(true); // 将图片调整到适合webview的大小
        mWebView.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);// 解决图片不显示
        mWebView.setWebViewClient(new WebViewClient());

        Log.d("new_url", new_url);
        mWebView.loadUrl(new_url);
    }

    /**
     * 修改页面主题样式
     */
    private void initWindow() {
        //  qmui实现沉浸式
        QMUIStatusBarHelper.translucent(this);

        //  将状态栏字体变为深色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        mWindow = getWindow();
        mWindow.setStatusBarColor(0xFFffffff);  //  设置状态栏颜色
        mWindow.setNavigationBarColor(0xFFFAFAFA);  //  设置导航栏颜色
    }

    /**
     * 设置所有的点击事件
     */
    private void initClickEvent() {
        mButton.setOnClickListener(this);
        bar_pinglun.setOnClickListener(this);
        bar_dianzan.setOnClickListener(this);
        bar_shoucang.setOnClickListener(this);
        bar_fenxiang.setOnClickListener(this);
        bar_quxiaoshoucang.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_back:
                finish();
                break;
            case R.id.bar_pinglun:
                CommentBarEvent();
                break;
            case R.id.bar_dianzan:
                BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,"Sorry，您没点赞权限~",
                        "因为你不是vip用户！","好吧");
                break;
            case R.id.bar_shoucang:
                if(isLogin == false){
                    BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,"请先登录才可以收藏哦~",
                            " ","好吧");
                    return;
                }
                collectNews();
                break;
            case R.id.bar_quxiaoshoucang:
                unCollectNews();
                break;
            case R.id.bar_fenxiang:
                ShareBarEvent();
                break;
        }
    }



    /**
     * 评论操作
     */
    private void CommentBarEvent() {
        Intent intent = new Intent(NewInfoActivity.this,CommentsActivity.class);
        intent.putExtra("new_id",new_id);
        startActivity(intent);
    }

    /**
     * 取消收藏
     */
    private void unCollectNews() {
        URL_UNCOLLECT += userBean.getAccount()+
                "&newsid="+new_id;
        if(NetUtils.isNetworkAvailable(this)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String responseData = HttpUtils.GetOkHttpResponseData(URL_UNCOLLECT);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parsePhpData(responseData);
                        }
                    });
                }
            }).start();
        }else{
            NetUtils.isNetworkAvailable(this);
        }

        bar_shoucang.setVisibility(View.VISIBLE);
        bar_quxiaoshoucang.setVisibility(View.GONE);
    }

    /**
     * 收藏操作
     */
    private void collectNews() {
        String imgUrl = JsonUtils.JsonTransform(newsListBean.getImgId());
        URL_COLLECT += userBean.getAccount()+
                "&newsid="+newsListBean.getNewId() +
                "&title="+newsListBean.getTitle()+
                "&hint="+newsListBean.getHint()+
                "&url="+newsListBean.getUrl()+
                "&image="+imgUrl;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String responseData = HttpUtils.GetOkHttpResponseData(URL_COLLECT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parsePhpData(responseData);
                    }
                });
            }
        }).start();
        bar_shoucang.setVisibility(View.GONE);
        bar_quxiaoshoucang.setVisibility(View.VISIBLE);
    }

    /**
     * 处理Php服务器端返回的数据,返回1代表操作成功，返回0表示操作失败
     * @param responseData
     */
    private void parsePhpData(String responseData) {
        if(responseData.equals("1")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE,
                    "操作成功~","  ","嘻嘻");
        }else if(responseData.equals("0")){
            BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                    "操作失败~","  ","好吧");
            return;
        }
    }

    /**
     * 分享操作
     */
    private void ShareBarEvent() {
        QMUIBottomSheet.BottomGridSheetBuilder builder = new QMUIBottomSheet.BottomGridSheetBuilder(mActivity);
        builder.addItem(R.mipmap.ic_share_wechat,
                "微信好友", TAG_SHARE_WECHAT_FRIENDS, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.ic_share_friends,
                        "朋友圈", TAG_SHARE_WECHAR_MOMENTS, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.ic_share_sina,
                        "新浪微博", TAG_SHARE_SINA, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.ic_share_qq,
                        "QQ", TAG_SHARE_QQ, QMUIBottomSheet.BottomGridSheetBuilder.FIRST_LINE)
                .addItem(R.mipmap.ic_copy_url,
                        "复制链接", TAG_SHARE_COPY, QMUIBottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                .addItem(R.mipmap.ic_share_more,
                        "分享更多", TAG_SHARE_MORE, QMUIBottomSheet.BottomGridSheetBuilder.SECOND_LINE)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomGridSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView) {
                        dialog.dismiss();
                        int tag = (int) itemView.getTag();
                        switch (tag) {
                            case TAG_SHARE_WECHAT_FRIENDS:
                                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("我不会打开微信噢~")
                                        .setContentText("打不开你要人家怎么分享嘛")
                                        .setConfirmText("好吧")
                                        .show();
                                break;
                            case TAG_SHARE_WECHAR_MOMENTS:
                                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("我不会打开朋友圈噢~")
                                        .setContentText("微信都打不开怎么打开朋友圈嘛")
                                        .setConfirmText("好吧")
                                        .show();
                                break;
                            case TAG_SHARE_SINA:
                                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("我不会打开新浪微博的啦~")
                                        .setContentText("我没有新浪微博的分享key嘛")
                                        .setConfirmText("好吧")
                                        .show();
                                break;
                            case TAG_SHARE_QQ:
                                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("我不会打开QQ的啦~")
                                        .setContentText("打不开你要人家怎么分享嘛")
                                        .setConfirmText("好吧")
                                        .show();
                                break;
                            case TAG_SHARE_COPY:
                                Toast.makeText(mActivity, "链接已复制", Toast.LENGTH_SHORT).show();
                                break;
                            case TAG_SHARE_MORE:
                                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("更多分享给谁你告诉我~")
                                        .setContentText("前面的我都分享不了你还要分享更多？")
                                        .setConfirmText("好吧")
                                        .show();
                                break;
                        }
                    }
                }).setIsShowButton(false).build().show();
    }

    private void parseJsonWithJsonObject(String responseData) {
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(responseData);
            comments = jsonObject.getString("comments");
            popularity = jsonObject.getString("popularity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void isCollect(String account,String news_id){
        URL_ISCOLLECT += account+
                "&newsid="+news_id;

        new Thread(new Runnable() {
            @Override
            public void run() {
                String responseData = HttpUtils.GetOkHttpResponseData(URL_ISCOLLECT);
                if(responseData.equals("1")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_shoucang.setVisibility(View.GONE);
                            bar_quxiaoshoucang.setVisibility(View.VISIBLE);
                        }
                    });
                }else if(responseData.equals("0")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bar_shoucang.setVisibility(View.VISIBLE);
                            bar_quxiaoshoucang.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
    }
}
