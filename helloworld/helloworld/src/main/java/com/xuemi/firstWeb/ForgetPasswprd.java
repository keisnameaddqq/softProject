package com.xuemi.firstWeb;

import AddDataBase.PersonImagesData;
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
 * Date:2023/12/2716:58
 **/
@WebServlet("/forgetpasswprd")
@MultipartConfig
public class ForgetPasswprd extends HttpServlet {
    private final ConnectInterface connectInterface=new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");resp.setCharacterEncoding("UTF-8");
        String telephone = req.getParameter("telephone");
        String userId = req.getParameter("userName");
        String newpassword = req.getParameter("newpassword");
        boolean jieguo=connectInterface.reset_secret(userId,telephone,newpassword);
        if(!jieguo){
            resp.getWriter().write("false");
        } else {
            resp.getWriter().write("true");
        }
    }
}
