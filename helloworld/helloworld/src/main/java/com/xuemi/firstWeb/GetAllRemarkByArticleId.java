package com.xuemi.firstWeb;

import AddDataBase.ArticleData;
import AddDataBase.PingLunData;
import Server.ConnectInterface;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/2921:06
 **/
@WebServlet("/GetAllRemarkByArticleId")
@MultipartConfig
public class GetAllRemarkByArticleId extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String articleId = request.getParameter("articleId");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用GetAllRemarkByArticleId 获取文章"+articleId+"的所有评论");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            ConnectInterface.warfare_logger.info("userId:" + userId+" 获取评论！但是token有问题！");
            response.getWriter().write("false");
            return;
        }
        //pinglunid 0 和 pinglunauthorid 1 和 pingluncontent 2 和 pinglunauthorname 3
        List<String[]> commentsByArticleId = PingLunData.getCommentsByArticleId(Long.parseLong(articleId));
        response.getWriter().write(new Gson().toJson(commentsByArticleId));
        ConnectInterface.warfare_logger.info("userId:" + userId+" 成功获取评论");
    }
}
