package Server.StoreData.FriendData;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Problem:
 * Author:
 * Date:2023/11/2118:08
 **/
public interface FriendDataInterface {

    //find-添加一个通过用户名查找好友并返回Map<Integer, String>的方法
    public Map<Integer, String> findFriendsByUsername(String username) ;
    //find-添加一个通过用户ID查找好友并返回Map<Integer, String>的方法
    public Map<Integer, String> findFriendsByUserId(long userId) ;
    //find-检查是否存在具有给定friendid的好友
    public boolean doesFriendIdExist(int friendId) ;
    //find-检查是否存在具有给定friendName的好友
    public boolean doesFriendNameExist(String friendName) ;

    //delete-添加一个通过用户名删除好友的方法
    public void deleteFriendsByUsername(String username) ;
    //delete-添加一个通过用户ID删除好友的方法
    public void deleteFriendsByUserId(long userId) ;
    //add-添加一个好友的方法
    public void addFriends(int friendId, String friendName) ;

    //添加一个返回所有好友信息的方法，返回类型为Map<Integer, String>
    public Map<Integer, String> getAllFriends() ;
}
