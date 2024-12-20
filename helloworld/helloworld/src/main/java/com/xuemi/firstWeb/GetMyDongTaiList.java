package com.xuemi.firstWeb;

import AddDataBase.ArticleData;
import AddDataBase.GuanZhuData;
import AddDataBase.SignatureData;
import Server.ConnectInterface;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/3116:14
 **/
@WebServlet("/GetMyDongTaiList")
@MultipartConfig
public class GetMyDongTaiList extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用GetMyDongTaiList 获取动态列表");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            ConnectInterface.warfare_logger.info("userId:" + userId+" 获取动态列表！但是token有问题！");
            response.getWriter().write("false");
            return;
        }
        //获取动态数据
        List<String[]> DongTaiList = new ArrayList<>();
        for(String[] myArticle : ArticleData.getAllArticles(Long.parseLong(userId))){
            // articleid 0 和 title 1 和 content 2 和 dianzang 3 和 pinglun 4 和 timestampinminutes 5
            DongTaiList.add(new String[]{myArticle[0],userId,SignatureData.getNicknameById(Long.parseLong(userId)),
                    myArticle[1],myArticle[3],myArticle[4],myArticle[5]});
        }
        // articleId 0 和 authorid 1 和 authorNickname 2 和 title 3 和 dianzang 4 和 pinglun 5 和 timestampinminutes 6
        response.getWriter().write(new Gson().toJson(DongTaiList));
        ConnectInterface.warfare_logger.info("userId:" + userId+" 成功获取收藏列表");
    }
}
