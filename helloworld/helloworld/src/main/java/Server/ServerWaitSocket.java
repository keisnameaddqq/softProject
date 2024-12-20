package Server;

import Communication.AboutLoginMessage;
import Communication.MyMessage;
import Server.StoreData.TokenAndBasicData.BasicDataInterface;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Problem:
 * Author:
 * Date:2023/11/2816:34
 **/
public class ServerWaitSocket {
    //存储当前在线的用户
    public static HashMap<Integer,UserInServer> onLineUser=new HashMap<Integer,UserInServer>();

    //删除成功登录的客户
    public static void delete_Customer(int id){
        for (Map.Entry<Integer, UserInServer> entry : ServerWaitSocket.onLineUser.entrySet()) {
            if (entry.getKey().equals(id)) {
                ServerWaitSocket.onLineUser.remove(entry.getKey());
                break; // 如果只删除一个匹配项，可以在这里添加 break
            }
        }
    }

    //测试主main函数
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1001);
        while (true){
            Socket accept = serverSocket.accept();
            new Thread(new ServerDealLogin(accept)).start();
            System.out.println("客户连接");
        }

    }

}


class ServerDealLogin implements Runnable{

    private Socket socket;
    private ObjectInputStream objin=null;
    private ObjectOutputStream objout=null;

    ServerDealLogin(Socket socket) throws IOException {
        this.socket=socket;
        objout=new ObjectOutputStream(socket.getOutputStream());
        objin=new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while(true){
            try {
                MyMessage message = (MyMessage) objin.readObject();
                MyMessage ret = dealMyMessage.ServerFromLoginInterface(message);
                if(message.aboutLoginMessage.state==AboutLoginMessage.AboutLogin_login_token&&ret.aboutLoginMessage.login_tokenResult
                        ||message.aboutLoginMessage.state==AboutLoginMessage.AboutLogin_login&&ret.aboutLoginMessage.loginResult){
                    if(add_NewCustomer (ret.aboutLoginMessage.username,ret.aboutLoginMessage.id,ret.aboutLoginMessage.token)){
                        objout.writeObject(ret);
                        break;
                    } else {
                        ret.aboutLoginMessage.login_tokenResult=ret.aboutLoginMessage.loginResult=false;
                    }
                }
                objout.writeObject(ret);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                ConnectInterface.warfare_logger.info("未成功登录的用户断开连接");
                break;
            }
        }
    }

    //添加成功登录的客户,如果没有已登录的，则登录，否则返回false
    public boolean add_NewCustomer(String userName,int id,String token){
        for (Map.Entry<Integer, UserInServer> entry : ServerWaitSocket.onLineUser.entrySet()) {
            if (entry.getKey().equals(id)) {
                return false;
            }
        }
        UserInServer userInServer = new UserInServer(userName, id, objin, objout, token);
        ServerWaitSocket.onLineUser.put(id,userInServer);
        new Thread(userInServer).start();
        return true;
    }


    DealMyMessage dealMyMessage=new DealMyMessage();
    public final static LoginInterface loginInterface=new ConnectInterface();
    class DealMyMessage {
        public  MyMessage ServerFromLoginInterface(MyMessage message){
            MyMessage ret = new MyMessage(message.aboutLoginMessage);
            switch (message.aboutLoginMessage.state) {
                case AboutLoginMessage.AboutLogin_isTelephoneRegular://电话号码规范
                    ret.aboutLoginMessage.isTelephoneRegularResult =
                            loginInterface.isTelephoneRegular(message.aboutLoginMessage.tel);
                    break;

                case AboutLoginMessage.AboutLogin_regular://用户名和密码的规范
                    ret.aboutLoginMessage.regularResult =
                            loginInterface.regular(message.aboutLoginMessage.userAskName, message.aboutLoginMessage.password);
                    break;

                case AboutLoginMessage.AboutLogin_regular_Password:
                    ret.aboutLoginMessage.regular_PasswordResult=
                            loginInterface.regularPassword(message.aboutLoginMessage.password);

                case AboutLoginMessage.AboutLogin_register://注册
                    ret.aboutLoginMessage.registerResult =
                            loginInterface.register(message.aboutLoginMessage.userAskName,message.aboutLoginMessage.password,message.aboutLoginMessage.tel);
                    break;

                case AboutLoginMessage.AboutLogin_reset_tel://重设电话号码
                    ret.aboutLoginMessage.reset_telResult =
                            loginInterface.reset_tel(message.aboutLoginMessage.userAskName, message.aboutLoginMessage.tel, message.aboutLoginMessage.newtel);
                    break;

                case AboutLoginMessage.AboutLogin_reset_secret://重设密码
                    ret.aboutLoginMessage.reset_secretResult =
                            loginInterface.reset_secret(message.aboutLoginMessage.userAskName, message.aboutLoginMessage.tel, message.aboutLoginMessage.newpassword);
                    break;

                case AboutLoginMessage.AboutLogin_bangding://绑定第三方账号
                    loginInterface.bangding();
                    break;

                case AboutLoginMessage.AboutLogin_login_token://token登录
                    ret.aboutLoginMessage.login_tokenResult =
                            loginInterface.login_token(message.aboutLoginMessage.token);
                    ret.aboutLoginMessage.token =
                            message.aboutLoginMessage.token;
                    if (ret.aboutLoginMessage.login_tokenResult) {
                        ret.aboutLoginMessage.id =
                                Integer.parseInt(Objects.requireNonNull(BasicDataInterface.brokenToke(message.aboutLoginMessage.token)).get(0));
                        ret.aboutLoginMessage.username =
                                loginInterface.getNameByAny(String.valueOf(ret.aboutLoginMessage.id));
                    }
                    break;

                case AboutLoginMessage.AboutLogin_login://用户名和密码登录
                    ret.aboutLoginMessage.token =
                            loginInterface.login(message.aboutLoginMessage.userAskName, message.aboutLoginMessage.password);
                    ret.aboutLoginMessage.loginResult =
                            ret.aboutLoginMessage.token != null;
                    if (ret.aboutLoginMessage.loginResult) {
                        ret.aboutLoginMessage.id =
                                loginInterface.getIdByAny(message.aboutLoginMessage.userAskName);
                        ret.aboutLoginMessage.username =
                                loginInterface.getNameByAny(message.aboutLoginMessage.userAskName);
                    }
                    break;

                default:
                    ConnectInterface.warfare_logger.error("ServerDealLogin的DealMyMessage的ServerFromLoginInterface收到无效的请求");
                    break;
            }
            return ret;
        }
    }



}
