package AddDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/3014:10
 **/
public class ShouCangData implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(userid BIGINT, shoucangarticle BIGINT)";
    static final String tableName="shoucangdata";

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

    // 静态方法：根据参数 userid 和 shoucangarticle，往数据库中插入数据
    public static void addShoucangData(long userid, long shoucangarticle) {
        try {
            String insertQuery = "INSERT INTO " + tableName + " (userid, shoucangarticle) VALUES (" +
                    userid + ", " + shoucangarticle + ")";
            statement.executeUpdate(insertQuery);
            System.out.println("收藏数据已成功添加。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 静态方法：根据参数 userid，返回数据库中所有的收藏的文章 id
    public static List<String> getAllShoucangArticles(long userid) {
        List<String> shoucangList = new ArrayList<>();
        try {
            // SQL 查询以选择所有相关的收藏文章
            String query = "SELECT shoucangarticle FROM " + tableName + " WHERE userid = " + userid;
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                long shoucangarticle = resultSet.getLong("shoucangarticle");
                shoucangList.add(String.valueOf(shoucangarticle));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return shoucangList;
    }

    // 静态方法：根据参数 userid 和 shoucangarticle，从数据库中删除该行数据
    public static void deleteShoucangData(long userid, long shoucangarticle) {
        try {
            String deleteQuery = "DELETE FROM " + tableName + " WHERE userid = " + userid + " AND shoucangarticle = " + shoucangarticle;
            statement.executeUpdate(deleteQuery);
            System.out.println("收藏数据已成功删除。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 静态方法：根据参数 userid 和 shoucangarticle，判断数据行是否存在, 存在返回 true；否则返回 false
    public static boolean doesShoucangDataExist(long userid, long shoucangarticle) {
        try {
            String query = "SELECT * FROM " + tableName + " WHERE userid = " + userid + " AND shoucangarticle = " + shoucangarticle;
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next(); // 如果存在记录，返回 true；否则返回 false
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 处理异常时返回 false
        }
    }

    // 静态方法：根据提供的 shoucangarticle，删除该文章的所有的收藏数据
    public static void deleteAllShoucangDataByArticle(long shoucangarticle) {
        try {
            String deleteQuery = "DELETE FROM " + tableName + " WHERE shoucangarticle = " + shoucangarticle;
            statement.executeUpdate(deleteQuery);
            System.out.println("该文章的所有收藏数据已成功删除。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
