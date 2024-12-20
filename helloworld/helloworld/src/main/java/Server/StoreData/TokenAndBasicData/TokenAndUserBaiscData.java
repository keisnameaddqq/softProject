package Server.StoreData.TokenAndBasicData;

import AddDataBase.Mysql_User;
import Server.ConnectInterface;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Problem:
 * Author:
 * Date:2023/11/2117:22
 **/



//接口的实现
public class TokenAndUserBaiscData implements BasicDataInterface {
    /**
     * tokenDataBase与token相关的数据存储
     */


    @Override
    public String getToken(long id) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.getToken被调用\nid="+id+"\n");
        return tokenDataBase.getToken(id);
    }
    @Override
    public boolean isIdAndTokenExists(long id, String token) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.isIdAndTokenExists被调用\nid="+id+" token="+token+"\n");
        return tokenDataBase.isIdAndTokenExists(id,token);
    }

    /**
     * user_DataBase与基本的用户信息相关的数据存储
     */
    @Override
    public void insertUser(long id, String username, String userpassword, String usertelephone) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.insertUser被调用\nid="+id+" username="+username+
                " userpassword="+userpassword+" usertelephone="+usertelephone+"\n");
        user_DataBase.insertUser(id, username, userpassword, usertelephone);
    }
    @Override
    public void updateUserPassword(long id, String newPassword) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.updateUserPassword被调用\nid="+id+" newPassword="+newPassword+"\n");
        user_DataBase.updateUserPassword(id, newPassword);
    }
    @Override
    public void updateUsername(long id, String newUsername) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.updateUsername被调用\nid="+id+" newUsername="+newUsername+"\n");
        user_DataBase.updateUsername(id, newUsername);
    }
    @Override
    public void updateTelephone(long id, String newTelephone) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.updateTelephone被调用\nid="+id+" newTelephone="+newTelephone+"\n");
        user_DataBase.updateTelephone(id, newTelephone);
    }
    @Override
    public void updateTouxiang(long id, List<Byte> touxiangData) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.updateTouxiang被调用\nid="+id+" touxiangData=*****"+"\n");
        user_DataBase.updateTouxiang(id, touxiangData);
    }
    @Override
    public boolean isUsernameExists(String username) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.isUsernameExists被调用\nusername="+username+"\n");
        return user_DataBase.isUsernameExists(username);
    }
    @Override
    public boolean isTelephoneExists(String telephone) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.isTelephoneExists被调用\ntelephone="+telephone+"\n");
        return user_DataBase.isTelephoneExists(telephone);
    }
    @Override
    public boolean isIdAndTelephoneExists(long id, String telephone) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.isIdAndTelephoneExists被调用\nid="+id+" telephone="+telephone+"\n");
        return user_DataBase.isIdAndTelephoneExists(id,telephone);
    }
    @Override
    public boolean isIdAndPasswordMatch(long id, String password) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.isIdAndPasswordMatch被调用\nid="+id+" password="+password+"\n");
        return user_DataBase.isIdAndPasswordMatch(id, password);
    }
    @Override
    public boolean isNameAndPasswordMatch(String name, String password) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.isNameAndPasswordMatch被调用\nname="+name+" password="+password+"\n");
        return user_DataBase.isNameAndPasswordMatch(name, password);
    }
    @Override
    public boolean isNameAndTelephoneExists(String name, String telephone) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.isNameAndTelephoneExists被调用\nname="+name+" telephone="+telephone+"\n");
        return isNameAndPasswordMatch(name, telephone);
    }
    @Override
    public boolean isIdExists(long id) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.isIdExists被调用\nid="+id+"\n");
        return user_DataBase.isIdExists(id);
    }
    @Override
    public long getIdByName(String name){
        ConnectInterface.warfare_logger.info("BasicDataInterface.getIdByName被调用\nname="+name+"\n");

        return user_DataBase.getIdByName(name);
    }
    @Override
    public String getNameById(long id) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.getNameById被调用\nid="+id+"\n");
        return user_DataBase.getNameById(id);
    }
    @Override
    public List<Byte> getTouxiangById(long id) {
        ConnectInterface.warfare_logger.info("BasicDataInterface.getTouxiangById被调用\nid="+id+"\n");

        return user_DataBase.getTouxiangById(id);
    }
    @Override
    public String getTelephoneById(long id){
        ConnectInterface.warfare_logger.info("BasicDataInterface.getTelephoneById被调用\nid="+id+"\n");
        return user_DataBase.getTelephoneById(id);
    }

}


//直接对用户数据进行操作的静态类——————直接对 存储（id,token,deadline）的数据库表格 进行操作
class tokenDataBase implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(userid BIGINT, token VARCHAR(255) PRIMARY KEY, dealine BIGINT)";
    static final String tableName="token";

    //初始化Connection、Statement，连接url数据库
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        creatTable();
    }
    //创建名字为tableName的数据表格，如果已经存在就不进行创建
    private static void creatTable(){
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

    //根据用户提供的id生成对于的完整的token字符串,加入数据库，同时返回。并且删除数据库中过期的token
    public static String getToken(long id){
        deleteExpiredTokens();
        String token= UUID.randomUUID().toString();
        long dealine=(int)(Instant.now().getEpochSecond()/(24*60*60)+7);
        insertToken(id,token,dealine);
        return "#"+id+"#"+ token +"&"+ dealine+"&";
    }


    // find-判断id和token信息是否在数据库中存在，同时要求deadline小于今天
    public static boolean isIdAndTokenExists(long id, String token) {
        try {
            System.out.println("id="+id+" token="+token);
            String checkIdAndTokenQuery = "SELECT userid FROM " + tableName + " WHERE userid = ? AND token = ? AND dealine >= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkIdAndTokenQuery);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, token);
            preparedStatement.setLong(3, (int)(Instant.now().getEpochSecond()/(24*60*60)));
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在记录，返回true；否则，返回false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // delete-删除数据库中deadline小于t的所有信息条目
    private static void deleteExpiredTokens() {
        try {
            String deleteQuery = "DELETE FROM " + tableName + " WHERE dealine < ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setLong(1, (int)(Instant.now().getEpochSecond()/(24*60*60)));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add-将id、token、deadline信息条目存入数据库
    private static void insertToken(long id, String token, long deadline) {
        try {
            String insertTokenQuery = "INSERT INTO " + tableName + " (userid, token, dealine) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTokenQuery);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, token);
            preparedStatement.setLong(3, deadline);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}


//对用户数据进行操作的静态类——————直接对 存储（id,name,password,tel,touxiang）的数据库表格 进行操作
class user_DataBase implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(userid BIGINT PRIMARY KEY, username VARCHAR(255) , userpassword VARCHAR(255), usertelephone VARCHAR(255), touxiang LONGBLOB)";
    static final String tableName="userbasicdata";

    //初始化Connection、Statement，连接url数据库
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        creatTable();
    }
    //创建名字为tableName的数据表格，如果已经存在就不进行创建
    private static void creatTable(){
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

    //add-在数据库中插入一条用户信息
    public static void insertUser(long id,String username, String userpassword, String usertelephone) {
        try {
            String insertUserQuery = "INSERT INTO " + tableName + " (userid, username, userpassword, usertelephone) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertUserQuery);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, userpassword);
            preparedStatement.setString(4, usertelephone);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //change-通过用户ID在数据库中修改用户密码
    public static void updateUserPassword(long id, String newPassword) {
        try {
            String updatePasswordQuery = "UPDATE " + tableName + " SET userpassword = ? WHERE userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updatePasswordQuery);
            preparedStatement.setString(1, newPassword);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //change-通过用户ID在数据库中修改用户名
    public static void updateUsername(long id, String newUsername) {
        try {
            String updateUsernameQuery = "UPDATE " + tableName + " SET username = ? WHERE userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateUsernameQuery);
            preparedStatement.setString(1, newUsername);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //change-通过用户ID在数据库中修改电话号码
    public static void updateTelephone(long id, String newTelephone) {
        try {
            String updateTelephoneQuery = "UPDATE " + tableName + " SET usertelephone = ? WHERE userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateTelephoneQuery);
            preparedStatement.setString(1, newTelephone);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // change-通过id将头像的数据流存储到数据库中
    public static void updateTouxiang(long id, List<Byte> touxiangData) {
        try {
            // 将 ArrayList<Byte> 转换为 byte[]
            byte[] touxiangBytes = new byte[touxiangData.size()];
            for (int i = 0; i < touxiangData.size(); i++) {
                touxiangBytes[i] = touxiangData.get(i);
            }
            // 插入二进制数据
            String insertTouxiangQuery = "UPDATE " + tableName + " SET touxiang = ? WHERE userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTouxiangQuery);
            preparedStatement.setBinaryStream(1, new ByteArrayInputStream(touxiangBytes), touxiangBytes.length);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //find-在数据库中查找一个用户名，如果存在返回 true，否则返回 false
    public static boolean isUsernameExists(String username) {
        try {
            String checkUsernameQuery = "SELECT userid FROM " + tableName + " WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkUsernameQuery);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在记录，返回true；否则，返回false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //find-在数据库中查找一个电话号码，如果存在返回 true，否则返回 false
    public static boolean isTelephoneExists(String telephone) {
        try {
            String checkTelephoneQuery = "SELECT userid FROM " + tableName + " WHERE usertelephone = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkTelephoneQuery);
            preparedStatement.setString(1, telephone);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在记录，返回true；否则，返回false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //find-在数据库中查找给定用户ID和电话号码是否存在，如果存在返回 true，否则返回 false
    public static boolean isIdAndTelephoneExists(long id, String telephone) {
        try {
            String checkIdAndTelephoneQuery = "SELECT userid FROM " + tableName + " WHERE userid = ? AND usertelephone = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkIdAndTelephoneQuery);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, telephone);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在记录，返回true；否则，返回false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //find-在数据库中查找给定用户ID和密码信息是否存在，如果存在返回 true，否则返回 false
    public static boolean isIdAndPasswordMatch(long id, String password) {
        try {
            String checkIdAndPasswordQuery = "SELECT userid FROM " + tableName + " WHERE userid = ? AND userpassword = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkIdAndPasswordQuery);
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在记录，返回true；否则，返回false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //find-在数据库中查找用户名和密码信息对是否存在，如果存在返回 true，否则返回 false
    public static boolean isNameAndPasswordMatch(String name, String password) {
        try {
            String checkNameAndPasswordQuery = "SELECT userid FROM " + tableName + " WHERE username = ? AND userpassword = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkNameAndPasswordQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在记录，返回true；否则，返回false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //find-判断用户名和电话号码信息对是否在数据库中存在，存在返回 true，否则返回 false
    public static boolean isNameAndTelephoneExists(String name, String telephone) {
        try {
            String checkNameAndTelephoneQuery = "SELECT userid FROM " + tableName + " WHERE username = ? AND usertelephone = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkNameAndTelephoneQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, telephone);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在记录，返回 true；否则，返回 false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //find-判断用户id是否在数据库中存在，存在返回 true，否则返回 false
    public static boolean isIdExists(long id) {
        try {
            String checkIdQuery = "SELECT userid FROM " + tableName + " WHERE userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(checkIdQuery);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // 如果存在记录，返回 true；否则，返回 false
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    //get-通过用户名获取用户的id
    public static long getIdByName(String name) {
        try {
            String getIdByNameQuery = "SELECT userid FROM " + tableName + " WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getIdByNameQuery);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("userid");
            } else {
                return -1; // 如果未找到匹配的用户，返回-1或其他合适的默认值
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // 处理异常时返回-1或其他合适的默认值
        }
    }
    //get-通过用户的id获取用户名
    public static String getNameById(long id) {
        try {
            String getNameByIdQuery = "SELECT username FROM " + tableName + " WHERE userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getNameByIdQuery);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("username");
            } else {
                return null; // 如果未找到匹配的用户，返回null或其他合适的默认值
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // 处理异常时返回null或其他合适的默认值
        }
    }
    // get-通过用户的id获取头像的数据流
    public static List<Byte> getTouxiangById(long id) {
        try {
            String getTouxiangByIdQuery = "SELECT touxiang FROM " + tableName + " WHERE userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getTouxiangByIdQuery);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // 从数据库中读取二进制数据
                byte[] touxiangBytes = resultSet.getBytes("touxiang");
                // 转换为 ArrayList<Byte>
                List<Byte> touxiangList = new ArrayList<>();
                if(touxiangBytes!=null){
                    for (byte b : touxiangBytes) {
                        touxiangList.add(b);
                    }
                }
                return touxiangList;
            } else {
                return null; // 如果未找到匹配的用户，返回null或其他合适的默认值
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // 处理异常时返回null或其他合适的默认值
        }
    }
    // get-通过用户的id获取电话号码，找到匹配的用户，返回null或其他合适的默认值
    public static String getTelephoneById(long id) {
        try {
            String getTelephoneByIdQuery = "SELECT usertelephone FROM " + tableName + " WHERE userid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(getTelephoneByIdQuery);
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("usertelephone");
            } else {
                return null; // 如果未找到匹配的用户，返回null或其他合适的默认值
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // 处理异常时返回null或其他合适的默认值
        }
    }


}
