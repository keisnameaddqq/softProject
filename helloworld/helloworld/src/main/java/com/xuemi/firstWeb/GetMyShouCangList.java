package com.xuemi.firstWeb;

import AddDataBase.ArticleData;
import AddDataBase.PingLunData;
import AddDataBase.ShouCangData;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/3022:19
 **/
@WebServlet("/GetMyShouCangList")
@MultipartConfig
public class GetMyShouCangList extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用GetMyShouCangList 获取收藏列表");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            ConnectInterface.warfare_logger.info("userId:" + userId+" 获取收藏列表！但是token有问题！");
            response.getWriter().write("false");
            return;
        }
        List<String[]> ShouCangArticleList = new ArrayList<>();
        for(String articleId : ShouCangData.getAllShoucangArticles(Long.parseLong(userId))){
            String[] article=ArticleData.getArticleById(Long.parseLong(articleId));// authorid 0 和 title 1 和 content 2 和 dianzang 3 和 pinglun 4 和 timestampinminutes 5
            if(article != null){
                String authorNickname = SignatureData.getNicknameById(Long.parseLong(article[0]));
                ShouCangArticleList.add(new String[]{articleId,article[0],authorNickname,article[1],article[3],article[4],article[5]});
            }
        }
        //articleId 0 和 authorid 1 和 authorNickname 2 和 title 3 和 dianzang 4 和 pinglun 5 和 timestampinminutes 6
        response.getWriter().write(new Gson().toJson(ShouCangArticleList));
        ConnectInterface.warfare_logger.info("userId:" + userId+" 成功获取收藏列表");
    }
}
