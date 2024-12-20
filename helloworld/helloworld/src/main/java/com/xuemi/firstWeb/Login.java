package com.xuemi.firstWeb;

import Server.ConnectInterface;
import Server.StoreData.TokenAndBasicData.BasicDataInterface;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Problem:
 * Author:
 * Date:2023/12/1222:48
 */
//表示该类处理 /login 请求：http://localhost:8080/helloworld/login,即响应login.html中的表单请求
@WebServlet("/login")
public class Login extends HttpServlet {
    private final ConnectInterface connectInterface=new ConnectInterface();
    private HashMap<String,Integer> dongjie=new HashMap<>();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loginThought = req.getParameter("loginThought");
        boolean okLogin =true;
        String token = null;
        String userId = null;
        String falseReason = null;
        if("password".equals(loginThought)){
            String userName = req.getParameter("userName");
            String password = req.getParameter("password");
            ConnectInterface.warfare_logger.info("登录——接收到的用户名：" + userName + ", 密码为：" + password);
            token=connectInterface.login(userName,password);
            String pkey= userName+LocalDate.now().toString();//冻结关键字
            if("false".equals(token)){
                okLogin=false;
                //冻结需求
                int pvalue=1;
                if(dongjie.containsKey(pkey)) {
                    pvalue = dongjie.get(pkey) + 1;
                }
                dongjie.put(pkey,pvalue);
                if(pvalue>10) {
                    falseReason="账号已被冻结！";
                } else {
                    falseReason="Account does not exist or password is incorrect!";
                }
            } else {
                userId=String.valueOf(connectInterface.getIdByAny(userName));
            }
        } else if("token".equals(loginThought)){
            token = req.getParameter("token");
            ConnectInterface.warfare_logger.info("登录——接收到的token：" +token);
            if(!connectInterface.login_token(token)){
                falseReason="token错误 或 已经过期 ！";
                okLogin=false;
            } else {
                userId=BasicDataInterface.brokenToke(token).get(0);
            }
        } else {
            okLogin=false;
            falseReason="请求错误！\n检测到违规发送请求操作！";
        }
        //返回结果
        String[]ret;
        if(!okLogin){
            ret=new String[]{"false",falseReason};
        }else {
            ret=new String[]{"true",userId,token};
        }
        resp.getWriter().write(new Gson().toJson(ret));
        ConnectInterface.warfare_logger.info("登录结果:"+okLogin+"\n返回数据为："+ Arrays.toString(ret));
    }
}


