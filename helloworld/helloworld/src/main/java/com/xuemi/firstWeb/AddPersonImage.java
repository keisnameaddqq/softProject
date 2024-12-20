package com.xuemi.firstWeb;

import AddDataBase.ArticleData;
import AddDataBase.PersonImagesData;
import AddDataBase.SignatureData;
import Server.ConnectInterface;
import com.google.gson.Gson;

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
 * Date:2023/12/2613:01
 **/

@WebServlet("/addpersonimage")
@MultipartConfig
public class AddPersonImage extends HttpServlet {
    private final ConnectInterface connectInterface = new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String need = req.getParameter("need");
        String userId = req.getParameter("userId");
        String token = req.getParameter("token");
        if (!connectInterface.login_token(token)) {
            return;
        }
        System.out.println("need=" + need);
        if ("add".equals(need)) {
            ConnectInterface.warfare_logger.info("userId:" + userId + " 调用AddPersonImage 添加图片到个人相册");
            Part filePart = req.getPart("Image");
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // 获取文件名
            System.out.println("fileName=" + fileName);
            String fileExtension = fileName.substring(fileName.lastIndexOf(".")); // 获取文件后缀
            String Pii = LoadPicture.generateRandomNumericString(25) + fileExtension;
            String picturePath = "C:\\Users\\86198\\Desktop\\大作业\\helloworld\\helloworld\\web\\personImage\\" + Pii;
            try (InputStream fileContent = filePart.getInputStream()) {
                Files.copy(fileContent, Paths.get(picturePath), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
                resp.getWriter().write("Error saving the file.");
                return;
            }
            PersonImagesData.insertData(Long.parseLong(userId), "./personImage/" + Pii);
        } else if ("getall".equals(need)) {
            ConnectInterface.warfare_logger.info("userId:" + userId + " 调用AddPersonImage 获取个人相册");
        } else if ("delete".equals(need)) {
            String deleteUrl = req.getParameter("deleteurl");
            ConnectInterface.warfare_logger.info("userId:" + userId + " 调用AddPersonImage 删除" + deleteUrl + "个人相册");
            PersonImagesData.deleteData(Long.parseLong(userId), deleteUrl);
        } else {
            return;
        }
        String json = new Gson().toJson(PersonImagesData.getAllPictureUrls(Long.parseLong(userId)));
        ConnectInterface.warfare_logger.info("返回信息为:" + json);
        resp.getWriter().write(json);
    }
}
