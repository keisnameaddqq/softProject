package com.xuemi.firstWeb;

import AddDataBase.ArticleData;
import AddDataBase.DianZangData;
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
 * Date:2023/12/2415:20
 **/
@WebServlet("/findarticle")
@MultipartConfig
public class FindArticle extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        // 获取其他表单数据
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        String findcontent = request.getParameter("findcontent");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用FindArticle 查询'"+findcontent+"'文章");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            response.getWriter().write("false");
            return;
        }
        // 查询与搜索关键字相关的文章
        List<String[]> articlesList = ArticleData.searchArticlesAllAboutSearchString(findcontent, Long.parseLong(userId));
        // articleid 0 和 authorid 1 和 title 2 和 content 3 和 dianzang 4 和 pinglun 5 和
        // isDianZhang 6 和 timestampinminutes 7 和 isShouCang 8 和  authorNickName 9 和 authorSignature 10
        for(String[] a : articlesList){
            ConnectInterface.warfare_logger.info("articlesList="+ Arrays.toString(a));
        }
        // 将文章列表转换为 JSON 格式
        String json = new Gson().toJson(articlesList);
        ConnectInterface.warfare_logger.info("返回信息为:"+json);
        response.getWriter().write(json);
    }
}
