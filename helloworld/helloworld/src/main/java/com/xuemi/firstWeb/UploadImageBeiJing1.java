package com.xuemi.firstWeb;

import Server.ConnectInterface;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Problem:
 * Author:
 * Date:2023/12/2116:18
 **/

@WebServlet("/uploadCoverImage")
@MultipartConfig
public class UploadImageBeiJing1 extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取上传的文件
        Part filePart = request.getPart("coverImage");

        // 获取其他表单数据
        String userId = request.getParameter("userId");
        String token = request.getParameter("token");
        ConnectInterface.warfare_logger.info("userId:" + userId + " 调用了UploadImageBeiJing1 上传了图片");

        if (connectInterface.login_token(token) && filePart.getSize() != 0) {
            // 将文件保存到指定路径
            String touxiangPath = "C:\\Users\\86198\\Desktop\\大作业\\helloworld\\helloworld\\web\\userBeiJing\\" + userId + ".png";

            // 使用 try-with-resources 来确保输入流被正确关闭
            try (InputStream fileContent = filePart.getInputStream()) {
                // 将上传的文件保存到指定路径
                Files.copy(fileContent, Paths.get(touxiangPath), StandardCopyOption.REPLACE_EXISTING);
                ConnectInterface.warfare_logger.info("userId:" + userId + " 调用了UploadImageBeiJing1的结果 true");
                response.getWriter().write("true");
            } catch (IOException e) {
                // 如果发生异常，记录错误并返回 false
                e.printStackTrace();
                ConnectInterface.warfare_logger.info("userId:" + userId + " 调用了UploadImageBeiJing1的结果 false (异常)");
                response.getWriter().write("false");
            }
        } else {
            ConnectInterface.warfare_logger.info("userId:" + userId + " 调用了UploadImageBeiJing1的结果 false");
            response.getWriter().write("false");
        }
    }
}

