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

/**
 * Problem:
 * Author:
 * Date:2023/12/2521:18
 **/
@WebServlet("/showarticle")
@MultipartConfig
public class ShowArticle extends HttpServlet {
    private final ConnectInterface connectInterface=new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");resp.setCharacterEncoding("UTF-8");
        String articleId = req.getParameter("articleId");
        String authorId = req.getParameter("authorId");
        String userId = req.getParameter("userId");
        String token = req.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用ShowArticle 查询'"+authorId+"'的文章");
        String[] articleData = new String[11];
        // authorid 0 和 title 1 和 content 2 和 dianzang 3 和 authorName 4 和 authorSignatur 5 和
        // pinglun 6 和 timestampinminutes 7 和 isGuanZhu 8 和 NumberOfArticle 9 和 NumberOfFan 10
        String[]article = ArticleData.getArticleById(Integer.parseInt(articleId));
        assert article != null;
        articleData[0]=article[0];articleData[1]=article[1];articleData[2]=article[2];articleData[3]=article[3];
        articleData[4]=SignatureData.getNicknameById(Integer.parseInt(authorId));
        articleData[5]=SignatureData.getSignatureById(Integer.parseInt(authorId));
        articleData[6]=article[4];
        articleData[7]=article[5];
        articleData[8]=GuanZhuData.existsGuanZhuData(Long.parseLong(userId),Long.parseLong(authorId))?"1":"0";
        articleData[9]=String.valueOf(ArticleData.getAllArticles(Long.parseLong(authorId)).size());
        articleData[10]=String.valueOf(GuanZhuData.getGuanZhuUserIds(Long.parseLong(authorId)).size());
        String json = new Gson().toJson(articleData);
        ConnectInterface.warfare_logger.info("返回信息为:"+json);
        resp.getWriter().write(json);
    }
}
