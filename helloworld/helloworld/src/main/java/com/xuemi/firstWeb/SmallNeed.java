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
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/2922:38
 **/

@WebServlet("/SmallNeed")
@MultipartConfig
public class SmallNeed extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    //支持 文章点赞 文章删除
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String need = request.getParameter("need");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用SmallNeed！但是token有问题！");
            response.getWriter().write("false");
            return;
        }
        // 确认需求并进行操作,支持 [文章点赞,文章删除,文章收藏,文章删除删除收藏,关注,取消关注]
        if("DianZang".equals(need)){
            String articleId = request.getParameter("articleId");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用SmallNeed 点赞文章"+articleId);
            DianZangData.insertDianZangData(Long.parseLong(userId),Long.parseLong(articleId));
            ArticleData.incrementDianzang(Long.parseLong(articleId));
            response.getWriter().write("true");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 成功点赞 文章"+articleId);
        } else if("DeleteArticle".equals(need)&&request.getParameter("authorId").equals(userId)){
            String articleId = request.getParameter("articleId");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用SmallNeed 删除文章"+articleId);
            ArticleData.deleteArticle(Long.parseLong(articleId));
            PingLunData.deleteCommentsByArticleId(Long.parseLong(articleId));
            ShouCangData.deleteAllShoucangDataByArticle(Long.parseLong(articleId));
            DianZangData.deleteAllDianZangDataByArticle(Long.parseLong(articleId));
            response.getWriter().write("true");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 成功删除 文章"+articleId);
        } else if("ShouCang".equals(need)){
            String articleId = request.getParameter("articleId");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用SmallNeed 想要收藏文章"+articleId);
            ShouCangData.addShoucangData(Long.parseLong(userId),Long.parseLong(articleId));
            response.getWriter().write("true");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 成功收藏 文章"+articleId);
        }else if("GuanZhu".equals(need)){
            String authorId = request.getParameter("authorId");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用SmallNeed 想要关注up主"+authorId);
            GuanZhuData.insertGuanZhuData(Long.parseLong(userId),Long.parseLong(authorId));
            response.getWriter().write("true");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 成功关注 up主"+authorId);
        } else if("DeleteGuanZhu".equals(need)){
            String guanZhuAuthorId = request.getParameter("guanZhuAuthorId");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用SmallNeed 想要取消关注up主"+guanZhuAuthorId);
            GuanZhuData.deleteGuanZhuData(Long.parseLong(userId),Long.parseLong(guanZhuAuthorId));
            response.getWriter().write("true");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 成功取消关注 up主"+guanZhuAuthorId);
        } else if("DeleteShouCang".equals(need)){
            String articleId = request.getParameter("articleId");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用SmallNeed 想要取消收藏文章"+articleId);
            ShouCangData.deleteShoucangData(Long.parseLong(userId),Long.parseLong(articleId));
            response.getWriter().write("true");
            ConnectInterface.warfare_logger.info("userId:" + userId+" 成功取消收藏文章 文章"+articleId);
        } else {
            response.getWriter().write("false");
        }
    }
}
