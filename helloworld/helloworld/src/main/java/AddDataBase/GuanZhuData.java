package AddDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/3014:08
 **/

public class GuanZhuData implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(userid BIGINT, guanzhuauthorid BIGINT)";
    static final String tableName="guanzhudata";

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

    // 插入关注数据
    public static void insertGuanZhuData(long userid, long guanzhuauthorid) {
        try {
            String insertQuery = "INSERT INTO " + tableName + " (userid, guanzhuauthorid) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setLong(1, userid);
                preparedStatement.setLong(2, guanzhuauthorid);
                preparedStatement.executeUpdate();
                System.out.println("关注数据插入成功。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 返回所有关注的作者ID
    public static List<String> getGuanZhuAuthorIds(long userid) {
        List<String> guanzhuAuthorIds = new ArrayList<>();
        try {
            String selectQuery = "SELECT guanzhuauthorid FROM " + tableName + " WHERE userid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setLong(1, userid);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        guanzhuAuthorIds.add(String.valueOf(resultSet.getLong("guanzhuauthorid")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guanzhuAuthorIds;
    }

    // 删除关注数据
    public static void deleteGuanZhuData(long userid, long guanzhuauthorid) {
        try {
            String deleteQuery = "DELETE FROM " + tableName + " WHERE userid = ? AND guanzhuauthorid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setLong(1, userid);
                preparedStatement.setLong(2, guanzhuauthorid);
                preparedStatement.executeUpdate();
                System.out.println("关注数据删除成功。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 判断是否存在关注数据, 如果存在返回 true, 不存在返回 false
    public static boolean existsGuanZhuData(long userid, long guanzhuauthorid) {
        try {
            String existsQuery = "SELECT * FROM " + tableName + " WHERE userid = ? AND guanzhuauthorid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(existsQuery)) {
                preparedStatement.setLong(1, userid);
                preparedStatement.setLong(2, guanzhuauthorid);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 返回所有关注指定作者的用户ID
    public static List<String> getGuanZhuUserIds(long guanzhuauthorid) {
        List<String> guanzhuUserIds = new ArrayList<>();
        try {
            String selectQuery = "SELECT userid FROM " + tableName + " WHERE guanzhuauthorid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setLong(1, guanzhuauthorid);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        guanzhuUserIds.add(String.valueOf(resultSet.getLong("userid")));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guanzhuUserIds;
    }

}
