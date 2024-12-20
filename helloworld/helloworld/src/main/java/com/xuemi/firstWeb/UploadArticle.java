package com.xuemi.firstWeb;

import AddDataBase.ArticleData;
import AddDataBase.SignatureData;
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
 * Date:2023/12/2414:15
 **/

@WebServlet("/uploadarticle")
@MultipartConfig
public class UploadArticle extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        String whatevercontinue = request.getParameter("whatevercontinue");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用UploadArticle 上传文章");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            ConnectInterface.warfare_logger.info("userId:" + userId+" 上传的文章的！但是token有问题！");
            response.getWriter().write("false");
            return;
        }
        //对文章是否存在的确认
        if ("false".equals(whatevercontinue)&&ArticleData.doesArticleExist(Integer.parseInt(userId),title)){
            response.getWriter().write("title is existing");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 上传的文章的标题已经存在！");
            return;
        }
        String[] sensitiveWords = ArticleData.checkSensitiveWords(title, content);
        if(sensitiveWords.length>0){
            response.getWriter().write("你的文章中包含’敏感词‘");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 上传的文章中包含敏感词！");
            return;
        }
        //添加文章
        ArticleData.addArticle(Integer.parseInt(userId),title,content);
        response.getWriter().write("true");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 成功上传文章");
    }
}
