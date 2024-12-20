package com.xuemi.firstWeb;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import AddDataBase.SignatureData;
import Server.ConnectInterface;

/**
 * Problem:
 * Author:
 * Date:2023/12/131:29
 **/
//表示该类处理 /login 请求：http://localhost:8080/helloworld/register,即响应login.html中的表单请求
@WebServlet("/register")
public class Register extends HttpServlet {
    private final ConnectInterface connectInterface=new ConnectInterface();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");resp.setCharacterEncoding("UTF-8");
        String userName = req.getParameter("userName");
        String password = req.getParameter("password");
        String tel=req.getParameter("telephone");
        ConnectInterface.warfare_logger.info("注册——接收到的用户名：" + userName + ", 密码为：" + password+", 电话号码为："+tel );
        String jieguo=connectInterface.register(userName, password,tel);
        ConnectInterface.warfare_logger.info("注册结果:"+jieguo);
        if("false".equals(jieguo)){
            resp.getWriter().write("false");
        }else {
            setTouXiang(jieguo);
            SignatureData.insertSignatureAndNickname(Integer.parseInt(jieguo),"11111","1111");
            resp.getWriter().write("true");
        }
    }
    //为新用户设置默认的头像
    private void setTouXiang(String id){
        // 源图片路径
        Path sourceImagePath = Paths.get("C:\\Users\\86198\\Desktop\\Web编程\\大作业\\helloworld\\helloworld\\web\\images\\admin.png");
        // 目标图片路径（带新的文件名）
        Path targetImagePath = Paths.get("C:\\Users\\86198\\Desktop\\Web编程\\大作业\\helloworld\\helloworld\\web\\userTouXiang\\"+id+".png");
        try {
            // 复制图片文件
            Files.copy(sourceImagePath, targetImagePath);
            System.out.println("图片复制成功！");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("图片复制失败：" + e.getMessage());
        }
    }
}
