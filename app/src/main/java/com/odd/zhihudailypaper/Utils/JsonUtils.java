package com.odd.zhihudailypaper.Utils;

import android.util.Log;

import com.odd.zhihudailypaper.Bean.CommentsListBean;
import com.odd.zhihudailypaper.Bean.NewsListBean;
import com.odd.zhihudailypaper.Bean.UserBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 将json正确转义
 */
public class JsonUtils {

    public static String JsonTransform(String json){
        StringBuilder replace = new StringBuilder(
                json.replace("\\","")
                        .replace("[\"","")
                        .replace("\"]",""));

        int lastIndex = replace.lastIndexOf("/");
        replace.insert(lastIndex,"/");

        String imgUrl = replace.toString();
        return imgUrl;
    }

    /**
     * 将json数据转为NewsListBean对象传入mList集合
     * @param responseData  json返回的数据
     * @param mList NewsBean集合
     * @param jsonArrayName json数组字列表名
     */
    public static void ParseNewsListBeanJsonToList(String responseData, List<NewsListBean> mList, String jsonArrayName) {
        JSONObject jsonObject;
        JSONArray jsonArray;

        try{
            jsonObject = new JSONObject(responseData);
            jsonArray = jsonObject.getJSONArray(jsonArrayName);

            for(int i = 0;i < jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);

                NewsListBean news = new NewsListBean();
                news.setNewId(jsonObject.getString("id"));
                news.setTitle(jsonObject.getString("title"));

                news.setHint(jsonObject.getString("hint"));
                news.setUrl(jsonObject.getString("url"));
                if(jsonObject.has("images")){
                    news.setImgId(jsonObject.getString("images"));
                }else {
                    news.setImgId(jsonObject.getString("image"));
                }

                mList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将json数据转为CommentsListBean对象传入mList集合
     * @param responseData
     * @param mList
     */
    public static void ParseCommentsListBeanJsonToList(String responseData, List<CommentsListBean> mList){
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(responseData);
            jsonArray = jsonObject.getJSONArray("comments");

            if(jsonArray.length() == 0) return;

            for(int i = 0;i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);

                CommentsListBean comment = new CommentsListBean();
                comment.setAuthor(jsonObject.getString("author"));
                comment.setContent(jsonObject.getString("content"));
                comment.setAvatarId(jsonObject.getString("avatar"));
                comment.setTime(jsonObject.getString("time")+"000");
                comment.setLikes(jsonObject.getString("likes"));
                if(jsonObject.has("reply_to")){
                    jsonObject = (JSONObject)jsonObject.get("reply_to");
                    comment.setApply_to("// "+jsonObject.getString("author") +": " + jsonObject.getString("content"));
                }
                mList.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将时间戳转为yyyy-MM-dd格式
     * @param stamp 时间戳
     * @return
     */
    public static String StampToTime(String stamp){
        Date date = new Date(Long.parseLong(stamp));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    /**
     * 将自己写的服务器端代码返回的json数据解析为userbean对象
     * @param responseData 后端数据
     * @param userBean 返回的userbean对象
     */
    public static void ParsePhpDataJsonToUserBean(String responseData, UserBean userBean){
        JSONObject jsonObject;
        JSONArray jsonArray;

        try{
            jsonObject = new JSONObject(responseData);
            jsonArray = jsonObject.getJSONArray("user");

            if(jsonArray.isNull(0)) return;

            for(int i = 0;i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);

                userBean.setId(jsonObject.getInt("id"));
                userBean.setAccount(jsonObject.getString("account"));
                userBean.setPassword(jsonObject.getString("password"));
                userBean.setName(jsonObject.getString("name"));
                userBean.setTelephone(jsonObject.getString("telephone"));
                userBean.setAvatar(jsonObject.getString("avatar"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将自己写的服务器端返回的json数据转为newsListBean对象放进list
     * @param responseData
     * @param mList
     */
    public static void ParseNewsListBeanPhpDataJsonToList(String responseData, List<NewsListBean> mList) {
        JSONObject jsonObject;
        JSONArray jsonArray;

        try{
            jsonObject = new JSONObject(responseData);
            jsonArray = jsonObject.getJSONArray("news");

            if(jsonArray.isNull(0)) return;

            for(int i = 0;i<jsonArray.length();i++){
                jsonObject = jsonArray.getJSONObject(i);
                NewsListBean newsListBean = new NewsListBean();

                newsListBean.setNewId(jsonObject.getString("newsid"));
                newsListBean.setTitle(jsonObject.getString("title"));
                newsListBean.setHint(jsonObject.getString("hint"));

                newsListBean.setUrl(jsonObject.getString("url"));
                newsListBean.setImgId(JsonTransform(jsonObject.getString("image")));

                mList.add(newsListBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

