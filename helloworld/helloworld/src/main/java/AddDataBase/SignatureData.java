package AddDataBase;

import java.sql.*;

/**
 * Problem:
 * Author:
 * Date:2023/12/2214:25
 **/
public class SignatureData implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(userid BIGINT PRIMARY KEY, signature VARCHAR(255), nickname VARCHAR(255))";
    static final String tableName="signature";

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
    // 插入特定id的signature和nickname
    public static void insertSignatureAndNickname(long userId, String signature, String nickname) {
        try {
            String insertQuery = "INSERT INTO " + tableName + " (userid, signature, nickname) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, signature);
            preparedStatement.setString(3, nickname);
            preparedStatement.executeUpdate();
            System.out.println("插入成功。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 通过特定id获取signature
    public static String getSignatureById(long userId) {
        try {
            String selectQuery = "SELECT signature FROM " + tableName + " WHERE userid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("signature");
            } else {
                // 在未找到时插入默认值
                insertSignatureAndNickname(userId, "", "11111");
                return ""; // 返回空字符串作为默认值
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "null"; // 如果没有找到结果，则返回null
    }
    // 通过特定id获取nickname
    public static String getNicknameById(long userId) {
        try {
            String selectQuery = "SELECT nickname FROM " + tableName + " WHERE userid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("nickname");
            }  else {
                // 在未找到时插入默认值
                insertSignatureAndNickname(userId, "", "11111");
                return "11111"; // 返回空字符串作为默认值
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "null"; // 如果没有找到结果，则返回null
    }
    // 通过特定id修改signature
    public static void updateSignatureById(long userId, String newSignature) {
        try {
            String updateQuery = "UPDATE " + tableName + " SET signature=? WHERE userid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newSignature);
            preparedStatement.setLong(2, userId);
            preparedStatement.executeUpdate();
            System.out.println("修改成功。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 通过特定id修改nickname
    public static void updateNicknameById(long userId, String newNickname) {
        try {
            String updateQuery = "UPDATE " + tableName + " SET nickname=? WHERE userid=?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newNickname);
            preparedStatement.setLong(2, userId);
            preparedStatement.executeUpdate();
            System.out.println("修改成功。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
