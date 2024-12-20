package com.xuemi.firstWeb;

import AddDataBase.ArticleData;
import AddDataBase.PingLunData;
import Server.ConnectInterface;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Problem:
 * Author:
 * Date:2023/12/2920:46
 **/

@WebServlet("/createRemark")
@MultipartConfig
public class CreateRemark  extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String articleId = request.getParameter("articleId");
        String remarkContent = request.getParameter("remarkContent");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用CreateRemark 向文章"+articleId+" 发表评论");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            ConnectInterface.warfare_logger.info("userId:" + userId+" 上传的评论！但是token有问题！");
            response.getWriter().write("false");
            return;
        }
        String[] sensitiveWords = ArticleData.checkSensitiveWords("", remarkContent);
        if(sensitiveWords.length>0){
            response.getWriter().write("你的评论中包含’敏感词‘");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 上传的评论中包含’敏感词‘");
            return;
        }
        //添加文章
        PingLunData.insertComment(Long.parseLong(userId),remarkContent,Long.parseLong(articleId));
        ArticleData.incrementPinglun(Long.parseLong(articleId));
        response.getWriter().write("true");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 成功上传评论");
    }
}
