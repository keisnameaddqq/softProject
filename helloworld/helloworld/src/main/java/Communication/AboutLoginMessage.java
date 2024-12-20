package Communication;

import java.io.Serializable;

/**
 * Problem:
 * Author:
 * Date:2023/11/2817:27
 **/



//与登录的必要操作相关
public class AboutLoginMessage implements Serializable {
    public final static int AboutLogin_isTelephoneRegular = 0,AboutLogin_regular = 1,AboutLogin_register = 2,
            AboutLogin_reset_tel = 3,AboutLogin_reset_secret = 4, AboutLogin_login_token = 5,
            AboutLogin_bangding = 6,AboutLogin_login = 7,AboutLogin_regular_Password=8;

    public int state=-1;//状态，有上面几种，表示请求的内容
    public String tel=null;//0、2、3
    public String password=null;//1、4、7
    public String userAskName=null;//1、7
    public String newtel=null;//3
    public String newpassword=null;//4
    public String token=null;//5

    public boolean isTelephoneRegularResult=false;//0
    public String regularResult=null;//1
    public String regular_PasswordResult=null;//8
    public String registerResult=null;//2
    public boolean reset_telResult=false;//3
    public boolean reset_secretResult=false;//4
    //登录结果
    public boolean loginResult=false;//5
    public boolean login_tokenResult=false;//7

    public String username=null;
    public int id;


    public AboutLoginMessage(AboutLoginMessage a) {
        this.state = a.state;
        this.tel = a.tel;
        this.password = a.password;
        this.userAskName = a.userAskName;
        this.newtel = a.newtel;
        this.newpassword = a.newpassword;
        this.token = a.token;
        this.regular_PasswordResult=a.regular_PasswordResult;
        this.isTelephoneRegularResult = a.isTelephoneRegularResult;
        this.regularResult = a.regularResult;
        this.registerResult = a.registerResult;
        this.reset_telResult = a.reset_telResult;
        this.reset_secretResult = a.reset_secretResult;
        this.loginResult = a.loginResult;
        this.login_tokenResult = a.login_tokenResult;
        this.username = a.username;
        this.id = a.id;
    }

    public AboutLoginMessage(){

    }

}
