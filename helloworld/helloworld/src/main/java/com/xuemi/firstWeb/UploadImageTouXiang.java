package com.xuemi.firstWeb;

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
 * Date:2023/12/2015:45
 **/
@WebServlet("/uploadProfilePicture")
@MultipartConfig
public class UploadImageTouXiang extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取上传的文件
        Part filePart = request.getPart("profilePicture");

        // 获取其他表单数据
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");

        ConnectInterface.warfare_logger.info("userId:" + userId + " 调用UploadImageTouXiang 更换头像");

        // 确认用户信息
        if (filePart.getSize() == 0 || !connectInterface.login_token(token)) {
            response.getWriter().write("false");
            return;
        }

        // 将文件保存到用户头像路径下
        String touxiangPath = "C:\\Users\\86198\\Desktop\\大作业\\helloworld\\helloworld\\web\\userTouXiang\\" + userId + ".png";

        // 使用 try-with-resources 自动关闭流
        try (InputStream fileContent = filePart.getInputStream()) {
            Files.copy(fileContent, Paths.get(touxiangPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // 处理文件保存过程中的异常
            e.printStackTrace();
            response.getWriter().write("false");
            return;
        }

        // 返回响应
        response.getWriter().write("true");
    }
}







