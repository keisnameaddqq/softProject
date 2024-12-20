package com.xuemi.firstWeb;

import AddDataBase.*;
import Server.ConnectInterface;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Problem:
 * Author:
 * Date:2023/12/3113:05
 **/
@WebServlet("/FirstHtmlNavData")
@MultipartConfig
public class FirstHtmlNavData  extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        // 获取其他表单数据
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        String canshu = request.getParameter("canshu");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用FirstHtmlNavData 获取"+canshu+"数据");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            response.getWriter().write("false");
            ConnectInterface.warfare_logger.info("userId:" + userId+"获取"+canshu+"数据!但是token错误!");
            return;
        }
        // articleId 0, authorId 1, title 2, content 3, remarkNumber 4, isDianZhang 5, agreeNumber 6,
        // isShouCang 7, timestampinminutes 8, authorNickName 9, authorSignature 10
        List<String[]> returnData = FirstHtmlNavData.GetFirstHtmlNavData(canshu,userId);
        // 将文章列表转换为 JSON 格式
        String json = new Gson().toJson(returnData);
        ConnectInterface.warfare_logger.info("返回信息为:"+json);
        response.getWriter().write(json);
    }

    // articleId 0, authorId 1, title 2, content 3, remarkNumber 4, isDianZhang 5, agreeNumber 6,
    // isShouCang 7, timestampinminutes 8, authorNickName 9, authorSignature 10
    public static List<String[]> GetFirstHtmlNavData(String canshu,String userId){
        List<String[]> realityData=new ArrayList<>();
        if("关注".equals(canshu)){
            List<String[]> returnData=new ArrayList<>();
            for(String GuanZhuAuthorId : GuanZhuData.getGuanZhuAuthorIds(Long.parseLong(userId))){
                String authorNickName = SignatureData.getNicknameById(Long.parseLong(GuanZhuAuthorId));
                String authorSignature = SignatureData.getSignatureById(Long.parseLong(GuanZhuAuthorId));
                //articleid 0 和 title 1 和 content 2 和 dianzang 3 和 pinglun 4 和 timestampinminutes 5
                List<String[]> allArticles = ArticleData.getAllArticles(Long.parseLong(GuanZhuAuthorId));
                for(String[] Article :  allArticles){
                    String isDianZhang = DianZangData.doesDianZangDataExist(Long.parseLong(userId),Long.parseLong(Article[0]))?"1":"0";
                    String isShouCang = ShouCangData.doesShoucangDataExist(Long.parseLong(userId),Long.parseLong(Article[0]))?"1":"0";
                    returnData.add(new String[]{Article[0],GuanZhuAuthorId,Article[1],Article[2], Article[4],
                            isDianZhang,Article[3],isShouCang,Article[5],authorNickName,authorSignature});
                }
            }
            realityData=getTopTenElements((ArrayList<String[]>) returnData,8);
        } else if("推荐".equals(canshu)){
            // articleid 0, authorid 1, title 2, content 3, dianzang 4, pinglun 5, timestampinminutes 6
            for(String[] Article :  ArticleData.getTop10ArticlesByTimestamp()) {
                String isDianZhang = DianZangData.doesDianZangDataExist(Long.parseLong(userId), Long.parseLong(Article[0])) ? "1" : "0";
                String isShouCang = ShouCangData.doesShoucangDataExist(Long.parseLong(userId), Long.parseLong(Article[0])) ? "1" : "0";
                String authorNickName = SignatureData.getNicknameById(Long.parseLong(Article[1]));
                String authorSignature = SignatureData.getSignatureById(Long.parseLong(Article[1]));
                realityData.add(new String[]{Article[0], Article[1], Article[2], Article[3], Article[5], isDianZhang,
                        Article[4], isShouCang, Article[6], authorNickName, authorSignature});
            }
        } else if("热榜".equals(canshu)){
            // articleid 0, authorid 1, title 2, content 3, dianzang 4, pinglun 5, timestampinminutes 6
            for(String[] Article :  ArticleData.getTop10ArticlesByDianzangAndPinglun()) {
                String isDianZhang = DianZangData.doesDianZangDataExist(Long.parseLong(userId), Long.parseLong(Article[0])) ? "1" : "0";
                String isShouCang = ShouCangData.doesShoucangDataExist(Long.parseLong(userId), Long.parseLong(Article[0])) ? "1" : "0";
                String authorNickName = SignatureData.getNicknameById(Long.parseLong(Article[1]));
                String authorSignature = SignatureData.getSignatureById(Long.parseLong(Article[1]));
                realityData.add(new String[]{Article[0], Article[1], Article[2], Article[3], Article[5], isDianZhang,
                        Article[4], isShouCang, Article[6], authorNickName, authorSignature});
            }
        } else if("视频".equals(canshu)){
            List<String[]> returnData=new ArrayList<>();
            // articleid 0 和 authorid 1 和 title 2 和 content 3 和 dianzang 4和 pinglun 5 和
            // isDianZhang 6 和 timestampinminutes 7 和 isShouCang 8 和 authorNickName 9 和 authorSignature 10
            for(String[] Article : ArticleData.searchArticlesAllAboutSearchString("</video>",Long.parseLong(userId))){
                returnData.add(new String[]{Article[0], Article[1], Article[2], Article[3], Article[5], Article[6], Article[4],
                        Article[8], Article[7], Article[9], Article[10]});
            }
            realityData=getTopTenElements((ArrayList<String[]>) returnData,8);
        }
        return realityData;
    }

    //对list以String[n]排序返回前10个,从大到小
    public static List<String[]> getTopTenElements(ArrayList<String[]> list, int n) {
        // 使用 Comparator 接口定义比较规则
        Comparator<String[]> comparator = new Comparator<String[]>() {
            @Override
            public int compare(String[] arr1, String[] arr2) {
                // 根据索引 n 进行比较
                return arr2[n].compareTo(arr1[n]);
            }
        };
        // 使用 Collections.sort 方法进行排序
        list.sort(comparator);
        // 截取前 10 个元素
        int endIndex = Math.min(10, list.size());
        return list.subList(0, endIndex);
    }
}
