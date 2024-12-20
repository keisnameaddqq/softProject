package Communication;

import Server.UserInServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Problem:
 * Author:
 * Date:2023/11/2817:28
 **/

//与登录后界面的相关的操作
public class AfterLoginFaceMessage implements Serializable {

    public final static int AfterLoginFaceMessage_GetAllData = 0,AfterLoginFaceMessage_UpdateTouxiang=1,
            AfterLoginFaceMessage_UpdateUsername=2,AfterLoginFaceMessage_IsUsernameExists=3,
            AfterLoginFaceMessage_IsIdExists=4, AfterLoginFaceMessage_GetIdByName=5,
            AfterLoginFaceMessage_GetNameById=6, AfterLoginFaceMessage_FriendDataInterface_DoesFriendNameExist=7,
            AfterLoginFaceMessage_FriendDataInterface_DoesFriendIdExist=8,
            AfterLoginFaceMessage_FriendDataInterface_FindFriendsByUsername=9,
            AfterLoginFaceMessage_FriendDataInterface_FindFriendsByUserId=10,
            AfterLoginFaceMessage_FriendDataInterface_DeleteFriendsByUsername=11,
            AfterLoginFaceMessage_FriendDataInterface_DeleteFriendsByUserId=12,
            AfterLoginFaceMessage_FriendDataInterfac_AddFriends=13,
            AfterLoginFaceMessage_FriendDataInterface_GetAllFriends=14;

    public int state=-1;//状态，有上面几种，表示请求的内容

    //服务器返回给客户端的结果
    public Boolean getAllDataResult=false;
    public Boolean updateTouxiangResult=false;
    public Boolean updateUsernameResult=false;
    public Boolean doesFriendNameExistResult=false;
    public Boolean doesFriendIdExistResult=false;
    public Boolean findFriendsByUsernameResult=false;
    public Boolean findFriendsByUserIdResult=false;
    public Boolean deleteFriendsByUsernameResult=false;
    public Boolean deleteFriendsByUserIdResult=false;
    public Boolean addFriendsResult=false;
    public Boolean getAllFriendsResult=false;
    public Boolean isUsernameExistsResult=false;
    public Boolean isIdExistsResult=false;
    public Boolean getIdByNameResult=false;
    public Boolean getNameByIdResult=false;


    //两个判断请求的存储数据
    public String isUsername=null;
    public int isId;

    //getby请求
    public String getByUsername=null;
    public int getById;

    //查找朋友请求获取到的数据
    public Map<Integer, String> findFriendsResult=null;

    //好友操作
    public String friendName=null;
    public int friendId;

    //该用户的基本的用户数据
    public String userName=null,telephone=null,token=null;
    public int userId;
    public ArrayList<Byte> touxiang=new ArrayList<Byte>();
    public HashMap<Integer,String> allFriends=new HashMap<Integer,String>();

    public AfterLoginFaceMessage(){

    }

    // 使用拷贝构造函数初始化成员变量
    public AfterLoginFaceMessage(AfterLoginFaceMessage a) {
        this.isUsername=a.isUsername;
        this.isId=a.isId;
        this.getByUsername=a.getByUsername;
        this.getById=a.getById;
        this.state = a.state;
        this.friendName = a.friendName;
        this.friendId = a.friendId;
        this.userName = a.userName;
        this.telephone = a.telephone;
        this.token = a.token;
        this.userId = a.userId;
        this.touxiang = new ArrayList<Byte> (a.touxiang); // 使用 clone() 复制 byte[]
        this.allFriends = new HashMap<>(a.allFriends); // 使用拷贝构造函数复制 HashMap
    }
}
