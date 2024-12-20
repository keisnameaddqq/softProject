package Server;

import Communication.AfterLoginFaceMessage;
import Communication.Game2Message;
import Communication.MyMessage;
import Server.StoreData.FriendData.FriendDataInterface;
import Server.StoreData.FriendData.MyFriendData;
import Server.StoreData.TokenAndBasicData.TokenAndUserBaiscData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;


public class  UserInServer extends TokenAndUserBaiscData implements Runnable {

    public String userName;
    public int userId;
    public String token;
    private FriendDataInterface friendDataInterface;
    private ObjectInputStream objin=null;
    private ObjectOutputStream objout=null;

    public UserInServer(String name,int id,ObjectInputStream in,ObjectOutputStream out,String token){
        this.userName=name;
        this.userId= id;
        this.friendDataInterface=new MyFriendData(userId);
        this.objin=in;
        this.objout=out;
        this.token=token;
    }


    @Override
    public void run() {
        while(true){
            try {
                MyMessage message = (MyMessage) objin.readObject();
                if(message.MessageAbout==MyMessage.AskForLoginFace){
                    MyMessage ret = dealMyMessage.ServerFromDataBase(message);
                    objout.writeObject(ret);
                } else if(message.MessageAbout==MyMessage.AskForGame2){
                    if(message.game2Message.state==Game2Message.Game2Message_ConnectWithFriend){
                        message.game2Message.friendId=new ConnectInterface().getIdByAny(message.game2Message.friendIdOrName);
                        message.game2Message.friendName=new ConnectInterface().getNameByAny(message.game2Message.friendIdOrName);
                    }
                    message.game2Message.userId=userId;message.game2Message.userName=userName;
                    MyMessage ret = null;
                    try {
                        ret = game2Server.ServerFromGameServer(message);
                    } catch (InterruptedException e) {
                        ret = new MyMessage();
                        e.printStackTrace();
                    }
                    objout.writeObject(ret);
                }
            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
                ConnectInterface.warfare_logger.info("成功登录的用户userId="+userId+"断开连接");
                ServerWaitSocket.delete_Customer(userId);
                break;
            }
        }
    }

    Game2Server game2Server = new Game2Server();


    //对客户端发送过来的包进行处理
    DealMyMessage dealMyMessage=new DealMyMessage();
    public final static LoginInterface loginInterface=new ConnectInterface();
    class DealMyMessage {
        public MyMessage ServerFromDataBase(MyMessage message){
            MyMessage ret = new MyMessage(message.afterLoginFaceMessage);
            switch (message.afterLoginFaceMessage.state) {
                case AfterLoginFaceMessage.AfterLoginFaceMessage_GetAllData:
                    ret.afterLoginFaceMessage.userName=userName;
                    ret.afterLoginFaceMessage.userId=userId;
                    ret.afterLoginFaceMessage.token=token;
                    ret.afterLoginFaceMessage.telephone=getTelephoneById(userId);
                    ret.afterLoginFaceMessage.allFriends=(HashMap<Integer, String>) friendDataInterface.getAllFriends();
                    ret.afterLoginFaceMessage.touxiang= (ArrayList<Byte>) getTouxiangById(userId);
                    ret.afterLoginFaceMessage.getAllDataResult=true;
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_UpdateTouxiang:
                    updateTouxiang(userId,message.afterLoginFaceMessage.touxiang);
                    ret.afterLoginFaceMessage.updateTouxiangResult=true;
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_UpdateUsername:
                    updateUsername(userId,message.afterLoginFaceMessage.userName);
                    ret.afterLoginFaceMessage.updateUsernameResult=true;
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_IsUsernameExists:
                    ret.afterLoginFaceMessage.isUsernameExistsResult =
                            isUsernameExists(message.afterLoginFaceMessage.isUsername);
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_IsIdExists:
                    ret.afterLoginFaceMessage.isIdExistsResult=
                            isIdExists(message.afterLoginFaceMessage.isId);
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_GetIdByName:
                    ret.afterLoginFaceMessage.getById =
                            (int) getIdByName(message.afterLoginFaceMessage.getByUsername);
                    ret.afterLoginFaceMessage.getIdByNameResult=true;
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_GetNameById:
                    ret.afterLoginFaceMessage.getByUsername =
                            getNameById(message.afterLoginFaceMessage.getById);
                    ret.afterLoginFaceMessage.getNameByIdResult=true;
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_DoesFriendNameExist:
                    ret.afterLoginFaceMessage.doesFriendNameExistResult=
                            friendDataInterface.doesFriendNameExist(message.afterLoginFaceMessage.friendName);
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_DoesFriendIdExist:
                    ret.afterLoginFaceMessage.doesFriendIdExistResult=
                            friendDataInterface.doesFriendIdExist(message.afterLoginFaceMessage.friendId);
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_FindFriendsByUsername:
                    Map<Integer, String> friendsByUsername1 = friendDataInterface.findFriendsByUsername(message.afterLoginFaceMessage.friendName);
                    if(friendsByUsername1==null||friendsByUsername1.size()==0){
                        ret.afterLoginFaceMessage.findFriendsByUsernameResult=false;
                    }else {
                        ret.afterLoginFaceMessage.findFriendsByUsernameResult=true;
                        ret.afterLoginFaceMessage.findFriendsResult=friendsByUsername1;
                    }
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_FindFriendsByUserId:
                    Map<Integer, String> friendsByUsername2 = friendDataInterface.findFriendsByUserId(message.afterLoginFaceMessage.friendId);
                    if(friendsByUsername2==null||friendsByUsername2.size()==0){
                        ret.afterLoginFaceMessage.findFriendsByUserIdResult=false;
                    }else {
                        ret.afterLoginFaceMessage.findFriendsByUserIdResult=true;
                        ret.afterLoginFaceMessage.findFriendsResult=friendsByUsername2;
                    }
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_DeleteFriendsByUsername:
                    friendDataInterface.deleteFriendsByUsername(message.afterLoginFaceMessage.friendName);
                    ret.afterLoginFaceMessage.deleteFriendsByUsernameResult=true;
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_DeleteFriendsByUserId:
                    friendDataInterface.deleteFriendsByUserId(message.afterLoginFaceMessage.friendId);
                    ret.afterLoginFaceMessage.deleteFriendsByUserIdResult=true;
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterfac_AddFriends:
                    friendDataInterface.addFriends(message.afterLoginFaceMessage.friendId,message.afterLoginFaceMessage.friendName);
                    ret.afterLoginFaceMessage.addFriendsResult=true;
                    break;

                case AfterLoginFaceMessage.AfterLoginFaceMessage_FriendDataInterface_GetAllFriends:
                    ret.afterLoginFaceMessage.allFriends = (HashMap<Integer, String>) friendDataInterface.getAllFriends();
                    ret.afterLoginFaceMessage.getAllFriendsResult=true;
                    break;

                default:
                    ConnectInterface.warfare_logger.error("UserInServer的DealMyMessage的ServerFromDataBase收到无效的请求");
                    break;
            }
            return ret;
        }
    }

}

class Game2Server{

    static private Map<Integer,String> listener = Collections.synchronizedMap(new HashMap<Integer,String>());
    static private Map<Integer,String> DoingGameCustomer = Collections.synchronizedMap(new HashMap<Integer,String>());
    static private Map<Integer,Integer> DoingGamePlay = Collections.synchronizedMap(new HashMap<Integer,Integer>());
    static private Map<Integer,Integer> DoingGamePlayResult = Collections.synchronizedMap(new HashMap<Integer,Integer>());


    //连接者寻找被连接者
    private boolean connectServer(int id,String name,String friendName,int friendId) throws InterruptedException {
        ConnectInterface.warfare_logger.info("Game2Server的connectServer被调用\nid="+id+" name"+name+" friendName"+friendName+" friendId="+friendId+"\n");
        for(int i=0;i<30;i++){
            if(connectListener(friendId)){
                DoingGameCustomer.put(id,name);
                DoingGameCustomer.put(friendId,friendName);
                DoingGamePlay.put(id,friendId);
                DoingGamePlay.put(friendId,id);
                return true;
            }
            Thread.sleep(1000);
        }
        return false;
    }
    private boolean connectListener(int ConnectFriendId){
        synchronized (Game2Server.class){
            ConnectInterface.warfare_logger.info("Game2Server的connectListener被调用");
            for (Map.Entry<Integer, String> entry : listener.entrySet()) {
                if(entry.getKey()==ConnectFriendId){
                    listener.remove(entry.getKey());
                    return true;
                }
            }
            return false;
        }
    }

    //等待连接的用户所用
    private boolean listenerServer(int id,String name) throws InterruptedException {
        ConnectInterface.warfare_logger.info("Game2Server的listenerServer被调用");
        listener.put(id,name);
        for(int i=0;i<30;i++){
            Thread.sleep(1000);
            if(!listener.containsKey(id)){
                return true;
            }
        }
        DeleteListener(id);
        Thread.sleep(1000);
        return DoingGameCustomer.containsKey(id);
    }
    private void DeleteListener(int listenerId){
        ConnectInterface.warfare_logger.info("Game2Server的DeleteListener被调用");
        synchronized (Game2Server.class){
            for (Map.Entry<Integer, String> entry : listener.entrySet()) {
                if(entry.getKey()==listenerId){
                    listener.remove(entry.getKey());
                }
            }
        }
    }

    //对局信息交换服务 0,1,2为结果，-1为没回应，-2为掉线了
    private int getRsult(int myid,int myShow) throws InterruptedException {
        ConnectInterface.warfare_logger.info("Game2Server的getRsult被调用");
        DoingGamePlayResult.put(myid,myShow);
        int duishouid=DoingGamePlay.get(myid);
        for(int i=0;i<30;i++){
            if(!DoingGamePlayResult.containsKey(duishouid)){
                int ret=DoingGamePlayResult.get(duishouid);
                DoingGamePlayResult.remove(duishouid);
                return ret;
            }
            Thread.sleep(1000);
        }
        if(!DoingGamePlayResult.containsKey(duishouid)){
            int ret=DoingGamePlayResult.get(duishouid);
            DoingGamePlayResult.remove(duishouid);
            return ret;
        }else if(DoingGameCustomer.containsKey(duishouid)){
            DoingGamePlayResult.remove(myid);
            return -1;
        } else {
            DoingGamePlayResult.remove(myid);
            return -2;
        }
    }


    public MyMessage ServerFromGameServer(MyMessage message) throws InterruptedException {
        MyMessage ret = new MyMessage(message.game2Message);
        ConnectInterface.warfare_logger.info("Game2Server的ServerFromGameServer被调用：\nuserName="+message.game2Message.userName+" userId="+message.game2Message.userId+"\n");
        switch (message.game2Message.state) {
            case Game2Message.Game2Message_ConnectWithListen:
                ret.game2Message.ConnectWithListenResult=
                        listenerServer(message.game2Message.userId,message.game2Message.userName);
                if(ret.game2Message.ConnectWithListenResult){
                    ret.game2Message.friendId=DoingGamePlay.get(message.game2Message.userId);
                    ret.game2Message.friendName=DoingGameCustomer.get(ret.game2Message.friendId);
                }
                break;

            case Game2Message.Game2Message_ConnectWithFriend:
                ret.game2Message.ConnectWithFriendResult=
                        connectServer(message.game2Message.userId,message.game2Message.userName,message.game2Message.friendName,message.game2Message.friendId);
                System.out.println("ret.game2Message.ConnectWithFriendResult="+ret.game2Message.ConnectWithFriendResult);
                break;

            case Game2Message.Game2Message_ConnectWithGameResult:
                ret.game2Message.friendScissorsStoneCloth=
                        getRsult(message.game2Message.userId,message.game2Message.ScissorsStoneCloth);
                ret.game2Message.ConnectWithGameResultResult=true;
                break;

            case Game2Message.Game2Message_DisConnect:
                DoingGameCustomer.remove(message.game2Message.userId);
                DoingGamePlay.remove(message.game2Message.userId);
                break;

            default:
                ConnectInterface.warfare_logger.error("Game2Server的ServerFromGameServer收到无效的请求");
                break;
        }
        return ret;
    }

}






