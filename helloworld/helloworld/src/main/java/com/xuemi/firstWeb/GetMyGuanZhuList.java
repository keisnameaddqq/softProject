package com.xuemi.firstWeb;

import AddDataBase.ArticleData;
import AddDataBase.GuanZhuData;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/3022:18
 **/
@WebServlet("/GetMyGuanZhuList")
@MultipartConfig
public class GetMyGuanZhuList extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用GetMyGuanZhuList 获取关注列表");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            ConnectInterface.warfare_logger.info("userId:" + userId+" 获取关注列表！但是token有问题！");
            response.getWriter().write("false");
            return;
        }
        List<String[]> GuanZhuAuthorList = new ArrayList<>();
        for(String authorId: GuanZhuData.getGuanZhuAuthorIds(Long.parseLong(userId))){
            String guanZhuAuthorNickname=SignatureData.getNicknameById(Long.parseLong(authorId));
            String guanZhuAuthorSignature=SignatureData.getSignatureById(Long.parseLong(authorId));
            String guanZhuAuthorArticleNumber=String.valueOf(ArticleData.getAllArticles(Long.parseLong(authorId)).size());
            String guanZhuAuthorFanNumber=String.valueOf(GuanZhuData.getGuanZhuUserIds(Long.parseLong(authorId)).size());
            GuanZhuAuthorList.add(new String[]{authorId,guanZhuAuthorNickname, guanZhuAuthorSignature, guanZhuAuthorArticleNumber, guanZhuAuthorFanNumber});
        }
        //authorid 0 和 authorNickname 1 和 authorSignature 2 和 articleNumber 3 和 fanNumber 4
        response.getWriter().write(new Gson().toJson(GuanZhuAuthorList));
        ConnectInterface.warfare_logger.info("userId:" + userId+" 成功获取收藏列表");
    }
}
