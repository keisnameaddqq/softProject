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
 * Date:2023/12/2323:16
 **/
@WebServlet("/uploadaudio")
@MultipartConfig
public class UpLoadAudio extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Part filePart = request.getPart("audio"); // 获取文件
        InputStream fileContent = filePart.getInputStream();
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // 获取文件名
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")); // 获取文件后缀
        String Pii= LoadPicture.generateRandomNumericString(20) + fileExtension;
        String audioPath = "C:\\Users\\86198\\Desktop\\大作业\\helloworld\\helloworld\\web\\vidio\\" + Pii;
        Files.copy(fileContent, Paths.get(audioPath), StandardCopyOption.REPLACE_EXISTING);
        ConnectInterface.warfare_logger.info(fileName+"音频文件被上传了，保存在./vidio/"+Pii+"下");
        response.getWriter().write("./vidio/"+Pii);
    }
}
