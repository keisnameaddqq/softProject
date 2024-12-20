package Server.StoreData.TokenAndBasicData;

import java.util.ArrayList;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/11/2117:59
 **/
public interface BasicDataInterface {
    /**
     * tokenDataBase与token相关的数据存储
     */
    //对token进行分解，分解为【0】id、【1】real_token、【2】deadline
    public static ArrayList<String> brokenToke(String token){
        if(token.length()<10) {return null;}
        StringBuilder builder=new StringBuilder(token);
        ArrayList<String> ret = new ArrayList<>();
        int fen1=builder.indexOf("#",1);
        int fen2=token.length()-new StringBuilder(token).reverse().indexOf("&",1)-1;
        ret.add(builder.substring(1,fen1));//id
        ret.add(builder.substring(fen1+1,fen2));//real_token
        ret.add(builder.substring(fen2+1));//dealine
        return ret;
    }
    public String getToken(long id);//获取token
    public boolean isIdAndTokenExists(long id, String token);//判断token是否存在


    /**
     * user_DataBase与基本的用户信息相关的数据存储
     */
    //add-在数据库中插入一条用户信息
    public void insertUser(long id,String username, String userpassword, String usertelephone);
    //change-通过用户ID在数据库中修改用户密码
    public void updateUserPassword(long id, String newPassword);
    //change-通过用户ID在数据库中修改用户名
    public void updateUsername(long id, String newUsername);
    //change-通过用户ID在数据库中修改电话号码
    public void updateTelephone(long id, String newTelephone);
    // change-通过id将头像的数据流存储到数据库中
    public void updateTouxiang(long id, List<Byte> touxiangData);
    //find-在数据库中查找一个用户名，如果存在返回 true，否则返回 false
    public boolean isUsernameExists(String username);
    //find-在数据库中查找一个电话号码，如果存在返回 true，否则返回 false
    public boolean isTelephoneExists(String telephone);
    //find-在数据库中查找给定用户ID和电话号码是否存在，如果存在返回 true，否则返回 false
    public boolean isIdAndTelephoneExists(long id, String telephone);
    //find-在数据库中查找给定用户ID和密码信息是否存在，如果存在返回 true，否则返回 false
    public boolean isIdAndPasswordMatch(long id, String password);
    //find-在数据库中查找用户名和密码信息对是否存在，如果存在返回 true，否则返回 false
    public boolean isNameAndPasswordMatch(String name, String password);
    //find-判断用户名和电话号码信息对是否在数据库中存在，存在返回 true，否则返回 false
    public boolean isNameAndTelephoneExists(String name, String telephone);
    //find-判断用户id是否在数据库中存在，存在返回 true，否则返回 false
    public boolean isIdExists(long id);
    //get-通过用户名获取用户的id
    public long getIdByName(String name);
    //get-通过用户的id获取用户名
    public String getNameById(long id);
    // get-通过用户的id获取头像的数据流
    public List<Byte> getTouxiangById(long id);
    // get-通过用户的id获取电话号码，找到匹配的用户，返回null或其他合适的默认值
    public String getTelephoneById(long id);
}
