package com.xuemi.firstWeb;


import Server.ConnectInterface;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Problem:
 * Author:
 * Date:2023/12/1316:12
 **/
//表示该类处理 /login 请求：http://localhost:8080/helloworld/game,即响应login.html中的表单请求
@WebServlet("/game")
public class Game extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String zhiValue= req.getParameter("zhi");
        int zhi=Integer.parseInt(zhiValue);
        ConnectInterface.warfare_logger.info("石头剪刀布——接收的用户出拳：" +  zhi);
        String ret=String.valueOf(jieguo(zhi));
        ConnectInterface.warfare_logger.info("石头剪刀布——人机算法回出拳：" +  ret);
        resp.getWriter().write(ret);
    }

    private double[]quanzhong={0.33333,0.33333,0.33334};
    private int jieguo(int shuru){
        ArrayList<Integer> get_data=get(shuru);
        double jieguo[]=new double[]{0,0,0};
        for(int kkptemp=0;kkptemp<3;kkptemp++){
            jieguo[get_data.get(kkptemp)]+=quanzhong[kkptemp];
        }
        int maxp=jieguo[0]>jieguo[1]?0:1;maxp=jieguo[maxp]>jieguo[2]?maxp:2;
        for(int tempi=0;tempi<3;tempi++){
            if(get_data.get(tempi)==shuru){
                quanzhong[tempi]-=0.02;
            } else if((get_data.get(tempi)+1)%3==shuru){
                quanzhong[tempi]-=0.08;
            }else if((get_data.get(tempi)-1+3)%3==shuru){
                quanzhong[tempi]+=0.10;
            }
            quanzhong[tempi]=quanzhong[tempi]<=0?0.02:quanzhong[tempi];
        }
        double zong=quanzhong[0]+quanzhong[1]+quanzhong[2];
        quanzhong[0]=quanzhong[0]/zong;quanzhong[1]=quanzhong[1]/zong;quanzhong[2]=quanzhong[2]/zong;
        return maxp;
    }

    private ArrayList<Integer> dataLog=new ArrayList<>();//0剪刀、1石头、2布
    private SuanFa1 s1=new SuanFa1();
    private SuanFa2 s2=new SuanFa2();
    private SuanFa3 s3=new SuanFa3();
    //0剪刀、1石头、2布,返回三个算法的结果
    private ArrayList<Integer> get(int shuru){
        ArrayList<Integer> ret = new ArrayList<>();
        FutureTask<Integer> futureTask1 = new FutureTask<Integer>(s1);
        new Thread(futureTask1).start();
        FutureTask<Integer> futureTask2 = new FutureTask<Integer>(s2);
        new Thread(futureTask2).start();
        FutureTask<Integer> futureTask3 = new FutureTask<Integer>(s3);
        new Thread(futureTask3).start();
        try {
            ret.add(futureTask1.get());ret.add(futureTask2.get());ret.add(futureTask3.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        s2.setShangci(shuru);s3.setShangCi(shuru);
        dataLog.add(shuru);
        return ret;
    }
    //1/3算法
    class SuanFa1 implements Callable<Integer>{
        @Override
        public Integer call() throws Exception {
            int randomNum = (int)(Math.random() * 9999);
            return randomNum%3;
        }
    }
    //上次用户选择石头，本次系统出布，类似出其他的
    class SuanFa2 implements Callable<Integer>{
        int shangci=-1;
        @Override
        public Integer call() throws Exception {
            if (shangci == -1) {
                int randomNum = (int)(Math.random() * 9999);
                return randomNum%3;
            }
            else {
                return (shangci+1)%3;
            }
        }
        public void setShangci(int s){
            shangci=s;
        }
    }
    //依据用户选择历史，如果他上次出石头，下次出那种的概率高，就选择获胜的那个出发；
    class SuanFa3 implements Callable<Integer>{
        double[][]data=new double[3][3];
        int[]shangci=new int[2];
        SuanFa3(){
            for(int i=0;i<3;i++){
                for (int j=0;j<3;j++){
                    data[i][j]=3.33333;
                }
            }
            shangci[0]=shangci[1]=-1;
        }
        @Override
        public Integer call() throws Exception {
            if(shangci[1]==-1||(data[shangci[1]][0]==data[shangci[1]][1]&&data[shangci[1]][2]==data[shangci[1]][1])){
                int randomNum = (int)(Math.random() * 9999);
                return randomNum%3;
            }
            else{
                int max=data[shangci[1]][1]>data[shangci[1]][0]?1:0;
                max=data[shangci[1]][2]>data[shangci[1]][max]?2:max;
                return (max+1)%3;
            }
        }
        public void setShangCi(int s){
            shangci[0]=shangci[1];
            shangci[1]=s;
            if(shangci[0]==-1){
                return;
            }
            double zong=data[shangci[0]][0]+data[shangci[0]][1]+data[shangci[0]][2]+0.2;
            for(int i=0;i<3;i++){
                if(i==s){
                    data[shangci[0]][i]=(data[shangci[0]][i]+0.2)/zong;
                }
                else{
                    data[shangci[0]][i]=(data[shangci[0]][i])/zong;
                }
            }
        }
    }

}