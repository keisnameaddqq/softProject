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
 * Date:2023/12/3013:30
 **/

@WebServlet("/ReEditerArticle")
@MultipartConfig
public class ReEditerArticle extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String articleId = request.getParameter("articleId");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用ReEditerArticle 重新编辑文章"+articleId);
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            ConnectInterface.warfare_logger.info("userId:" + userId+" 修改文章！但是token有问题！");
            response.getWriter().write("false");
            return;
        }
        //敏感词确认
        String[] sensitiveWords = ArticleData.checkSensitiveWords(title, content);
        if(sensitiveWords.length>0){
            response.getWriter().write("你的文章中包含’敏感词‘");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 上传的重新编辑的文章中包含敏感词！");
            return;
        }
        //修改文章
        ArticleData.updateArticle(Long.parseLong(articleId),title,content);
        response.getWriter().write("true");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 成功修改文章");
    }
}
