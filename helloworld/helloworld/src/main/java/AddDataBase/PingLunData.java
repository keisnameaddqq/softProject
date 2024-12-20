package AddDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/2918:09
 **/
public class PingLunData implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(pinglunid BIGINT AUTO_INCREMENT PRIMARY KEY, pinglunauthorid BIGINT, " +
            "pingluncontent TEXT, pinglunarticleid BIGINT)";
    static final String tableName="pinglundata";

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

    // 根据提供的 pinglunarticleid 返回所有的评论数据, pinglunid 0 和 pinglunauthorid 1 和 pingluncontent 2 和 pinglunauthorname 3
    public static List<String[]> getCommentsByArticleId(long pinglunarticleid) {
        List<String[]> commentsList = new ArrayList<>();
        try {
            String query = "SELECT pinglunid, pinglunauthorid, pingluncontent FROM " + tableName + " WHERE pinglunarticleid = " + pinglunarticleid;
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                long pinglunid = resultSet.getLong("pinglunid");
                long pinglunauthorid = resultSet.getLong("pinglunauthorid");
                String pingluncontent = resultSet.getString("pingluncontent");
                String pinglunauthorname = SignatureData.getNicknameById(pinglunauthorid);
                commentsList.add(new String[]{String.valueOf(pinglunid), String.valueOf(pinglunauthorid), pingluncontent, pinglunauthorname});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentsList;
    }

    // 往数据库中插入评论数据
    public static void insertComment(long pinglunauthorid, String pingluncontent, long pinglunarticleid) {
        try {
            String insertQuery = "INSERT INTO " + tableName + " (pinglunauthorid, pingluncontent, pinglunarticleid) VALUES (" +
                    pinglunauthorid + ", '" + pingluncontent + "', " + pinglunarticleid + ")";
            statement.executeUpdate(insertQuery);
            System.out.println("评论数据已成功插入。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 静态方法：根据提供的 pinglunarticleid，删除所有的该文章的评论
    public static void deleteCommentsByArticleId(long pinglunarticleid) {
        try {
            String deleteQuery = "DELETE FROM " + tableName + " WHERE pinglunarticleid = " + pinglunarticleid;
            statement.executeUpdate(deleteQuery);
            System.out.println("该文章的所有评论数据已成功删除。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
