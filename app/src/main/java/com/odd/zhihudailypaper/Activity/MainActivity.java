package com.odd.zhihudailypaper.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.lxj.xpopup.XPopup;
import com.odd.zhihudailypaper.Bean.NewsListBean;
import com.odd.zhihudailypaper.Bean.UserBean;
import com.odd.zhihudailypaper.Controls.NavBar;
import com.odd.zhihudailypaper.Controls.NavBottomButton;
import com.odd.zhihudailypaper.Controls.NavButton;
import com.odd.zhihudailypaper.Controls.ZhuanlanPopup;
import com.odd.zhihudailypaper.R;
import com.odd.zhihudailypaper.Utils.BaseUtils;
import com.odd.zhihudailypaper.Utils.NetUtils;
import com.odd.zhihudailypaper.Thread.NewsBannerAsyncTask;
import com.odd.zhihudailypaper.Thread.NewsListAsyncTask;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String URL_DATAS = "https://news-at.zhihu.com/api/4/news/latest";
    private static String URL_BEFORE = "https://news-at.zhihu.com/api/4/news/before/";
    private int beforeDay = 0;

    private TextView tv_day;
    private TextView tv_month;

    private ListView mListView;
    private ListView lv_onload;
    private List<NewsListBean> mList;
    private List<NewsListBean> list_onload;

    private NewsListAsyncTask mAsyncTask;
    private NewsBannerAsyncTask mBannerTask;
    private Button mButton;
    private View mView;
    private Banner banner;

    private DrawerLayout drawerLayout;
    private NavigationView mNavigationView;

    private NavBar mNavBar;
    private NavButton btn_star,btn_news,btn_ctrl;
    private NavBottomButton btn_night,btn_download;
    private ImageView mNavBarImg;

    private RefreshLayout refreshLayout;

    private boolean isLogin = false;

    private UserBean userBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  qmui实现沉浸式
        QMUIStatusBarHelper.translucent(this);
        //  将状态栏字体变为深色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        getWindow().setNavigationBarColor(0xFFFAFAFA);  //  设置导航栏颜色

        //  拿用户信息
        userBean = BaseUtils.getUserData(this);
        isLogin = BaseUtils.isLogin(userBean);

        mList = new ArrayList<>();
        list_onload = new ArrayList<>();
        initBanner();
        initDarerLayout();
        initView();
        initRefreshLayout();
    }

    /**
     * 该页面回来时还要拿一次用户的信息
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        userBean = BaseUtils.getUserData(this);
        if(isLogin == true && NetUtils.isNetworkAvailable(this)){
            Glide.with(this)
                    .load(userBean.getAvatar())
                    .into(mNavBarImg);
            mNavBar.setClickable(false);
            mNavBar.setText(userBean.getName());
        }
    }

    /**
     * 初始化加载控件
     */
    private void initRefreshLayout() {
        refreshLayout  = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        refreshLayout.setFooterHeight(50);

        refreshLayout.setDisableContentWhenRefresh(false);//是否在刷新的时候禁止列表的操作
        refreshLayout.setDisableContentWhenLoading(false);//是否在加载的时候禁止列表的操作

        final Activity activity = this;
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(1000/*,false*/);//传入false表示刷新失败
                refreshData();
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(500/*,false*/);//传入false表示加载失败
                loadMoreData();
            }
        });
    }

    /**
     * 下拉刷新数据
     */
    private void refreshData() {
        mList = new ArrayList<>();
        if(NetUtils.isNetworkAvailable(this)){
            mBannerTask = new NewsBannerAsyncTask(this,banner);
            mBannerTask.execute(URL_DATAS);

            mAsyncTask = new NewsListAsyncTask(this,mListView,mList);
            mAsyncTask.execute(URL_DATAS);
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
     * 上拉加载数据
     */
    private void loadMoreData(){
        if(NetUtils.isNetworkAvailable(this)){
            mAsyncTask = new NewsListAsyncTask(this,mListView,mList);
            mAsyncTask.execute(URL_BEFORE+BaseUtils.getOldDate(beforeDay));

            beforeDay++;
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
     * 初始化控件
     */
    private void initView() {
        mListView = findViewById(R.id.lv_main);
        tv_day = findViewById(R.id.tv_day);
        tv_month = findViewById(R.id.tv_month);
        mButton = findViewById(R.id.btn_toLeft);

        lv_onload = findViewById(R.id.lv_onload);

        //  侧边栏
        drawerLayout = findViewById(R.id.drawerlayout_drawer);

        Calendar calendar = Calendar.getInstance();
        long nowTime = calendar.getTimeInMillis();
        tv_day.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
        tv_month.setText((calendar.get(Calendar.MONTH)+1) + "月");

        //  为listview添加头部文件
        mListView.addHeaderView(mView);

        if(NetUtils.isNetworkAvailable(this)){
            mAsyncTask = new NewsListAsyncTask(this,mListView,mList);
            mAsyncTask.execute(URL_DATAS);
        }else{
            //  弹出没有网络的提醒
            BaseUtils.showAlertDialog(this,
                    SweetAlertDialog.WARNING_TYPE,
                    "Sorry，您没有网络~",
                    "没有网络还看什么新闻",
                    "好吧");
        }

        //  设置toolbar的导航按钮监听事件
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //显示侧滑菜单
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 初始化Banner
     */
    private void initBanner() {
        mView = View.inflate(this,R.layout.layout_banner,null);

        banner = mView.findViewById(R.id.banner);

        if(NetUtils.isNetworkAvailable(this)){
            mBannerTask = new NewsBannerAsyncTask(this,banner);
            mBannerTask.execute(URL_DATAS);
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
     * 初始化侧边栏
     */
    private void initDarerLayout() {
        mNavigationView = findViewById(R.id.layout_drawerLayout);

        initHeaderView();
        initNavigationViewMenu();
        initNavigationBottom();
    }

    /**
     * 初始化侧边栏底部
     */
    private void initNavigationBottom() {
        btn_night = findViewById(R.id.btn_night);
        btn_download = findViewById(R.id.btn_download);

        btn_night.setIconAndText(R.mipmap.ic_night,"夜间模式");
        btn_download.setIconAndText(R.mipmap.ic_download,"离线缓存");

        btn_night.setOnClickListener(this);
        btn_download.setOnClickListener(this);
    }

    /**
     * 初始化侧边栏头部
     */
    private void initHeaderView() {
        View headerView = mNavigationView.getHeaderView(0);
        mNavBar = headerView.findViewById(R.id.nav_bar);
        mNavBarImg = mNavBar.findViewById(R.id.iv_user_avatar);

        btn_star = headerView.findViewById(R.id.btn_star);
        btn_ctrl = headerView.findViewById(R.id.btn_ctrl);
        btn_news = headerView.findViewById(R.id.btn_news);

        btn_star.setImageAndText(R.mipmap.ic_star,"收藏");
        btn_ctrl.setImageAndText(R.mipmap.ic_ctrl,"设置");
        btn_news.setImageAndText(R.mipmap.ic_news,"消息");

        btn_star.setOnClickListener(this);
        btn_ctrl.setOnClickListener(this);
        btn_news.setOnClickListener(this);

        if(isLogin == false ||!NetUtils.isNetworkAvailable(this)){
            mNavBar.setOnClickListener(this);
        }else{
            Glide.with(this)
                    .load(userBean.getAvatar())
                    .into(mNavBarImg);
            mNavBar.setText(userBean.getName());
        }
    }

    /**
     * 初始化侧边栏的菜单
     */
    private void initNavigationViewMenu() {
        mNavigationView.setNavigationItemSelectedListener(this);
        Resources resource = this.getResources();
        ColorStateList csl= resource.getColorStateList(R.color.selector_navigation_menu);
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationView.setItemTextColor(csl);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setCheckable(true);//设置选项可选
        menuItem.setChecked(true);//设置选型被选中、
        drawerLayout.closeDrawers();//关闭侧边菜单栏
        int id = menuItem.getItemId();
        switch(id){
            case R.id.item1:
                break;
            case R.id.item2:
                new XPopup.Builder(this)
                        .asCustom(new ZhuanlanPopup(this))
                        .show();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btn_star:
                if(isLogin == true){
                    Intent starIntent = new Intent(MainActivity.this,CollectionsActivity.class);
                    startActivity(starIntent);
                }else {
                    BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                            "Sorry,请您先登录", "没登陆看什么收藏鸭你还想","好吧");
                }
                break;
            case R.id.btn_ctrl:
                if(isLogin == true){
                    Intent ctrlIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(ctrlIntent);
                }else {
                    BaseUtils.showAlertDialog(this,SweetAlertDialog.WARNING_TYPE,
                            "Sorry,请您先登录", "没登陆设置什么鸭你还想","好吧");
                }
                break;
            case R.id.btn_news:
                Intent newsIntent = new Intent(MainActivity.this,NewsActivity.class);
                startActivity(newsIntent);
                break;
            case R.id.btn_night:
                BaseUtils.showAlertDialog(this,SweetAlertDialog.NORMAL_TYPE,
                        "夜间模式正在开发中~", "敬请期待哦","好吧");
                break;
            case R.id.btn_download:
                BaseUtils.showAlertDialog(this,SweetAlertDialog.NORMAL_TYPE,
                        "离线缓存正在开发中~", "敬请期待哦","好吧");
                break;
            case R.id.nav_bar:
                Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                break;
        }
    }
}
