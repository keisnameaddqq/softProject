package com.xuemi.firstWeb;

import AddDataBase.SignatureData;
import Server.ConnectInterface;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Problem:
 * Author:
 * Date:2023/12/2212:08
 **/
@WebServlet("/updataSignature")
@MultipartConfig
public class updataSignature extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");response.setCharacterEncoding("UTF-8");
        String  Signature= request.getParameter("data");
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId+" 调用updataSignature 获取或修改个性签名");
        // 确认用户信息
        if (!connectInterface.login_token(token)) {
            response.getWriter().write("false");
            return;
        }
        if("".equals(Signature)){
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用updataSignature 获取个性签名成功");
            response.getWriter().write(SignatureData.getSignatureById(Integer.parseInt(userId)));
        } else{
            SignatureData.updateSignatureById(Integer.parseInt(userId),Signature);
            ConnectInterface.warfare_logger.info("userId:" + userId+" 调用updataSignature 修改个性签名成功");
            response.getWriter().write(SignatureData.getSignatureById(Integer.parseInt(userId)));
        }
    }
}