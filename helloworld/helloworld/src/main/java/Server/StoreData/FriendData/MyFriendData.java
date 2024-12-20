package Server.StoreData.FriendData;

import Server.ConnectInterface;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Problem:
 * Author:
 * Date:2023/11/111:39
 **/


//用于对用户的好友数据进行操作的数据库类：以id作为区分的关键key——————直接只对 id的（frienid,friendname）的数据库表格 进行操作
public class MyFriendData implements FriendDataInterface{
    private Connection connection;
    private Statement statement;
    private final long id;
    final String url = "jdbc:mysql://localhost:3306/web大作业";
    final String username = "root";
    final String password = "wcy20020714";
    final String tableCompose = "(friendid BIGINT PRIMARY KEY, friendname VARCHAR(255))";

    //初始化Connection、Statement，连接url数据库,如果该用户id的好友表格不存就进行创建
    public MyFriendData(long userid) {
        id=userid;
        try {
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        creatTable("friend"+ id);
    }
    //创建名字为tableName的数据表格，如果已经存在就不进行创建
    private void creatTable(String tableName){
        try {
            // 检查表格是否存在
            String checkTableExistsQuery = "SHOW TABLES LIKE '" + tableName + "'";
            if (statement.executeQuery(checkTableExistsQuery).next()) {
                System.out.println("表格已存在，不需要创建。");
            } else {
                // 如果表格不存在，创建它
                String createTableQuery = "CREATE TABLE " + tableName + " "+tableCompose;
                statement.executeUpdate(createTableQuery);
                System.out.println("表格已成功创建。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override//find-添加一个通过用户名查找好友并返回Map<Integer, String>的方法
    public Map<Integer, String> findFriendsByUsername(String username) {
        ConnectInterface.warfare_logger.info("FriendDataInterface.findFriendsByUsername被调用\nusername="+username+" id="+id+"\n");
        Map<Integer, String> friendMap = new HashMap<>();
        try {
            String tableName = "friend" + id; // 假设您使用用户的ID
            String findFriendsQuery = "SELECT friendid, friendname FROM " + tableName + " WHERE friendname=?";
            PreparedStatement preparedStatement = connection.prepareStatement(findFriendsQuery);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int friendId = resultSet.getInt("friendid");
                String friendName = resultSet.getString("friendname");
                friendMap.put(friendId, friendName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendMap;
    }

    @Override//find-添加一个通过用户ID查找好友并返回Map<Integer, String>的方法
    public Map<Integer, String> findFriendsByUserId(long userId) {
        ConnectInterface.warfare_logger.info("FriendDataInterface.findFriendsByUserId被调用\nuserId="+userId+" id="+id+"\n");
        Map<Integer, String> friendMap = new HashMap<>();
        try {
            String tableName = "friend" + id; // 假设您使用用户的ID
            String findFriendsQuery = "SELECT friendid, friendname FROM " + tableName + " WHERE friendid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(findFriendsQuery);
            preparedStatement.setLong(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int friendId = resultSet.getInt("friendid");
                String friendName = resultSet.getString("friendname");
                friendMap.put(friendId, friendName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendMap;
    }

    @Override//find-检查是否存在具有给定friendid的好友
    public boolean doesFriendIdExist(int friendId) {
        ConnectInterface.warfare_logger.info("FriendDataInterface.doesFriendIdExist被调用\nfriendId="+friendId+" id="+id+"\n");
        try {
            String tableName = "friend" + id;
            String checkFriendIdQuery = "SELECT friendid FROM " + tableName + " WHERE friendid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkFriendIdQuery);
            preparedStatement.setInt(1, friendId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在结果，返回true，否则返回false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override//find-检查是否存在具有给定friendName的好友
    public boolean doesFriendNameExist(String friendName) {
        ConnectInterface.warfare_logger.info("FriendDataInterface.doesFriendNameExist被调用\nfriendName="+friendName+" id="+id+"\n");
        try {
            String tableName = "friend" + id;
            String checkFriendNameQuery = "SELECT friendname FROM " + tableName + " WHERE friendname=?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkFriendNameQuery);
            preparedStatement.setString(1, friendName);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在结果，返回true，否则返回false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override//delete-添加一个通过用户名删除好友的方法
    public void deleteFriendsByUsername(String username) {
        ConnectInterface.warfare_logger.info("FriendDataInterface.deleteFriendsByUsername被调用\nusername="+username+" id="+id+"\n");
        try {
            String tableName = "friend" + id; // 假设您使用用户的ID
            String deleteFriendsQuery = "DELETE FROM " + tableName + " WHERE friendname=?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteFriendsQuery);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override//delete-添加一个通过用户ID删除好友的方法
    public void deleteFriendsByUserId(long userId) {
        ConnectInterface.warfare_logger.info("FriendDataInterface.deleteFriendsByUserId被调用\nuserId="+userId+" id="+id+"\n");
        try {
            String tableName = "friend" + id; // 假设您使用用户的ID
            String deleteFriendsQuery = "DELETE FROM " + tableName + " WHERE friendid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteFriendsQuery);
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override//add-添加一个好友的方法
    public void addFriends(int friendId, String friendName) {
        ConnectInterface.warfare_logger.info("FriendDataInterface.addFriends被调用\nfriendId="+friendId+" friendName="+friendName+" id="+id+"\n");
        try {
            String tableName = "friend" + id; // 假设您使用用户的ID
            String insertFriendQuery = "INSERT INTO " + tableName + " (friendid, friendname) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertFriendQuery);
            preparedStatement.setInt(1, friendId);
            preparedStatement.setString(2, friendName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override//添加一个返回所有好友信息的方法，返回类型为Map<Integer, String>
    public Map<Integer, String> getAllFriends() {
        ConnectInterface.warfare_logger.info("FriendDataInterface.getAllFriends被调用\nid="+id+"\n");
        Map<Integer, String> friendMap = new HashMap<>();
        try {
            String tableName = "friend" + id; // 假设您使用用户的ID
            String selectAllQuery = "SELECT friendid, friendname FROM " + tableName;
            ResultSet resultSet = statement.executeQuery(selectAllQuery);
            while (resultSet.next()) {
                int friendId = resultSet.getInt("friendid");
                String friendName = resultSet.getString("friendname");
                friendMap.put(friendId, friendName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendMap;
    }

}




