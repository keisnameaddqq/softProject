package com.xuemi.firstWeb;

import AddDataBase.SignatureData;
import Server.ConnectInterface;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Problem:
 * Author:
 * Date:2023/12/2211:59
 **/

@WebServlet("/updataNickname")
@MultipartConfig
public class updataNickname extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String Nickname = request.getParameter("data");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用updataNickname 获取或修改昵称");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            response.getWriter().write("false");
            return;
        }
        if("".equals(Nickname)){
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用updataNickname 获取昵称成功");
            response.getWriter().write(SignatureData.getNicknameById(Integer.parseInt(userId)));
        } else{
            SignatureData.updateNicknameById(Integer.parseInt(userId),Nickname);
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用updataNickname 修改昵称成功");
            response.getWriter().write(SignatureData.getNicknameById(Integer.parseInt(userId)));
        }
    }
}