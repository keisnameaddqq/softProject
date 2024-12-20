package AddDataBase;

import java.sql.*;

/**
 * Problem:
 * Author:
 * Date:2023/12/2918:08
 **/
public class DianZangData implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(dianzangauthorid BIGINT, dianzangarticleid BIGINT)";
    static final String tableName="dianzangdata";

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

    // 静态方法：根据 dianzangauthorid 和 dianzangarticleid 往数据库中插入数据
    public static void insertDianZangData(long dianzangauthorid, long dianzangarticleid) {
        try {
            String insertQuery = "INSERT INTO " + tableName + " (dianzangauthorid, dianzangarticleid) VALUES (" + dianzangauthorid + ", " + dianzangarticleid + ")";
            statement.executeUpdate(insertQuery);
            System.out.println("点赞数据已成功插入。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 静态方法：根据 dianzangauthorid 和 dianzangarticleid 进行判断，判断是否存在该行数据
    public static boolean doesDianZangDataExist(long dianzangauthorid, long dianzangarticleid) {
        try {
            String query = "SELECT * FROM " + tableName + " WHERE dianzangauthorid = " + dianzangauthorid + " AND dianzangarticleid = " + dianzangarticleid;
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next(); // 如果存在记录，返回 true；否则返回 false
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 处理异常时返回 false
        }
    }

    // 静态方法：根据提供的 dianzangarticleid，删除该文章的所有点赞记录
    public static void deleteAllDianZangDataByArticle(long dianzangarticleid) {
        try {
            String deleteQuery = "DELETE FROM " + tableName + " WHERE dianzangarticleid = " + dianzangarticleid;
            statement.executeUpdate(deleteQuery);
            System.out.println("该文章的所有点赞记录已成功删除。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
