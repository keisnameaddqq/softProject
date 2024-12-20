package com.xuemi.firstWeb;

/**
 * Problem:
 * Author:
 * Date:2023/12/2314:07
 **/
import Server.ConnectInterface;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/loadpicture")
@MultipartConfig
public class LoadPicture extends HttpServlet {
    private static final long serialVersionUID = 1L;
    //处理当个图片上传到piture文件夹下然后返回一个串数字用于访问其中的图片
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 获取上传的文件
        Part filePart = request.getPart("picture");
        // 使用 try-with-resources 确保文件流正确关闭
        try (InputStream fileContent = filePart.getInputStream()) {
            // 获取文件名
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            // 获取文件后缀
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            // 获取保存的随机地址
            String p = generateRandomNumericString(20);
            ConnectInterface.warfare_logger.info("上传了一个图片保存在./picture/" + p + fileExtension + "下");
            // 将文件保存到指定路径
            String touxiangPath = "C:\\Users\\86198\\Desktop\\大作业\\helloworld\\helloworld\\web\\picture\\" + p + fileExtension;
            Files.copy(fileContent, Paths.get(touxiangPath), StandardCopyOption.REPLACE_EXISTING);
            // 成功上传的日志
            ConnectInterface.warfare_logger.info("成功上传");
            // 返回文件路径
            response.getWriter().write("./picture/" + p + fileExtension);
        } catch (IOException e) {
            // 错误处理，返回错误信息
            ConnectInterface.warfare_logger.info("文件上传失败: " + e.getMessage());
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
    public static String generateRandomNumericString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10); // 生成 0 到 9 的随机数字
            sb.append(randomNumber);
        }
        return sb.toString();
    }
}

