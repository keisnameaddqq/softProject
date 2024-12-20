package AddDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/2612:49
 **/
public class PersonImagesData implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(userid BIGINT, pictureurl VARCHAR(255))";
    static final String tableName="personimages";

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

    // 插入数据到表格
    public static void insertData(long userid, String pictureurl) {
        try {
            String insertDataQuery = "INSERT INTO " + tableName + " (userid, pictureurl) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertDataQuery)) {
                preparedStatement.setLong(1, userid);
                preparedStatement.setString(2, pictureurl);
                preparedStatement.executeUpdate();
                System.out.println("数据插入成功。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据userid返回所有的pictureurl
    public static List<String> getAllPictureUrls(long userid) {
        List<String> pictureUrls = new ArrayList<>();
        try {
            String selectUrlsQuery = "SELECT pictureurl FROM " + tableName + " WHERE userid = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectUrlsQuery)) {
                preparedStatement.setLong(1, userid);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String pictureurl = resultSet.getString("pictureurl");
                    pictureUrls.add(pictureurl);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pictureUrls;
    }

    // 根据提供的 userid 和 pictureurl 参数删除数据
    public static void deleteData(long userid, String pictureurl) {
        try {
            String deleteDataQuery = "DELETE FROM " + tableName + " WHERE userid = ? AND pictureurl = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteDataQuery)) {
                preparedStatement.setLong(1, userid);
                preparedStatement.setString(2, pictureurl);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("数据删除成功。");
                } else {
                    System.out.println("未找到匹配的数据，无法删除。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
