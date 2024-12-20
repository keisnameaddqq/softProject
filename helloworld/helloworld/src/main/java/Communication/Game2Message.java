package Communication;

import java.io.Serializable;

/**
 * Problem:
 * Author:
 * Date:2023/11/2817:28
 **/


//关于石头剪刀布的相关通信需求
public class Game2Message implements Serializable {
    public final static int Game2Message_ConnectWithListen=0,Game2Message_ConnectWithFriend=1,
            Game2Message_ConnectWithGameResult=2,Game2Message_DisConnect=3;

    public int state=-1;//状态，有上面几种，表示请求的内容

    //服务器返回给客户端的结果
    public Boolean ConnectWithListenResult=false;
    public Boolean ConnectWithFriendResult=false;
    public Boolean ConnectWithGameResultResult=false;
    public Boolean DisConnectResult=false;

    //监听到的好友的连接的请求
    public int ListenResultId;
    public String ListenResultName=null;

    //作为信息传输的数据
    public String friendIdOrName=null;
    public int friendId;
    public String friendName=null;

    //出的结果：0剪刀、1石头、2布
    public int ScissorsStoneCloth=-1;
    //好友结果
    public int friendScissorsStoneCloth=-1;

    //基本信息
    public String userName;
    public int userId;

    Game2Message(){
    }

    // 使用拷贝构造函数初始化成员变量
    Game2Message(Game2Message a) {
        this.state = a.state;
        this.ListenResultId = a.ListenResultId;
        this.ListenResultName = a.ListenResultName;
        this.friendIdOrName = a.friendIdOrName;
        this.friendId = a.friendId;
        this.friendName = a.friendName;
        this.ScissorsStoneCloth = a.ScissorsStoneCloth;
        this.friendScissorsStoneCloth = a.friendScissorsStoneCloth;
    }

}
