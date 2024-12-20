package AddDataBase;

import Server.ConnectInterface;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Problem:
 * Author:
 * Date:2023/12/2413:55
 **/
public class ArticleData implements Mysql_User {
    static private Connection connection;
    static private Statement statement;
    static final String tableCompose = "(articleid BIGINT AUTO_INCREMENT PRIMARY KEY, authorid BIGINT, " +
            "title VARCHAR(255), content TEXT, dianzang BIGINT, pinglun BIGINT, timestampinminutes BIGINT)";
    static final String tableName="article";

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

    // 根据 authorid 和 title 查询是否存在该篇文章
    public static boolean doesArticleExist(long authorid, String title) {
        try {
            String query = "SELECT * FROM " + tableName + " WHERE authorid = " + authorid + " AND title = '" + title + "'";
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet.next(); // 如果存在记录，返回 true；否则返回 false
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 处理异常时返回 false
        }
    }

    // 根据 authorid、title 和 content 参数，dianzang 和 pinglun 初始为 0，添加文章到数据库中
    public static void addArticle(long authorid, String title, String content) {
        long currentTimestampInMinutes = System.currentTimeMillis() / (60 * 1000); // 当前时间（分钟级别）
        ConnectInterface.warfare_logger.info("authorid=" + authorid + " title=" + title + " 添加了文章");
        try {
            String insertQuery = "INSERT INTO " + tableName + " (authorid, title, content, dianzang, pinglun, timestampinminutes) VALUES (" +
                    authorid + ", '" + title + "', '" + content + "', 0, 0, " + currentTimestampInMinutes + ")";
            statement.executeUpdate(insertQuery);
            System.out.println("文章已成功添加。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据 authorid 参数，返回所有相关的文章的 articleid 0 和 title 1 和 content 2 和 dianzang 3 和 pinglun 4 和 timestampinminutes 5
    public static List<String[]> getAllArticles(long authorid) {
        List<String[]> articlesList = new ArrayList<>();
        try {
            // SQL查询以选择所有相关的文章，包括TimestampInMinutes列
            String query = "SELECT articleid, title, content, dianzang, pinglun, timestampinminutes FROM " + tableName + " WHERE authorid = " + authorid;
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                long articleId = resultSet.getLong("articleid");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                long dianzang = resultSet.getLong("dianzang");
                long pinglun = resultSet.getLong("pinglun");
                long timestampInMinutes = resultSet.getLong("timestampinminutes");
                articlesList.add(new String[]{String.valueOf(articleId), title, content, String.valueOf(dianzang), String.valueOf(pinglun), String.valueOf(timestampInMinutes)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articlesList;
    }

    // 根据给定的搜索字符串搜索文章，返回所有相关的文章的 articleid 0 和 authorid 1 和 title 2 和 content 3 和 dianzang 4
    // 和 pinglun 5 和 isisDianZhang 6 和 timestampinminutes 7 和 isShouCang 8 和 authorNickName 9 和 authorSignature 10
    public static List<String[]> searchArticlesAllAboutSearchString(String searchString,long userId) {
        List<String[]> result = new ArrayList<>();
        try {
            // SQL查询以搜索标题或内容中包含搜索字符串的文章，包含pinglun列
            String searchQuery = "SELECT articleid, authorid, title, content, dianzang, pinglun, timestampinminutes FROM " + tableName +
                    " WHERE title LIKE '%" + searchString + "%' OR content LIKE '%" + searchString + "%'";
            ResultSet resultSet = statement.executeQuery(searchQuery);
            while (resultSet.next()) {
                long articleId = resultSet.getLong("articleid");
                long authorId = resultSet.getLong("authorid");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                long dianzang = resultSet.getLong("dianzang");
                long pinglun = resultSet.getLong("pinglun");
                long timestampinminutes = resultSet.getLong("timestampinminutes");
                String isDianZhang = DianZangData.doesDianZangDataExist(userId,articleId)?"1":"0";
                String isShouCang = ShouCangData.doesShoucangDataExist(userId,articleId)?"1":"0";
                String authorNickName = SignatureData.getNicknameById(authorId);
                String authorSignature= SignatureData.getSignatureById(authorId);
                result.add(new String[]{String.valueOf(articleId), String.valueOf(authorId), title, content, String.valueOf(dianzang),
                        String.valueOf(pinglun),isDianZhang,String.valueOf(timestampinminutes),isShouCang,authorNickName,authorSignature});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 根据 articleid 返回指定的文章 authorid 0 和 title 1 和 content 2 和 dianzang 3 和 pinglun 4 和 timestampinminutes 5
    public static String[] getArticleById(long articleId) {
        try {
            // SQL查询以获取指定articleid的文章
            String query = "SELECT authorid, title, content, dianzang, pinglun, timestampinminutes FROM " + tableName + " WHERE articleid = " + articleId;
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                long authorId = resultSet.getLong("authorid");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                long dianzang = resultSet.getLong("dianzang");
                long pinglun = resultSet.getLong("pinglun");
                long timestampinminutes = resultSet.getLong("timestampinminutes");
                return new String[]{String.valueOf(authorId), title, content, String.valueOf(dianzang),
                        String.valueOf(pinglun), String.valueOf(timestampinminutes)};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 返回null表示未找到指定articleid的文章
    }

    // 静态方法：根据 articleid 和其他参数，修改该文章的标题、内容和时间戳
    public static void updateArticle(long articleId, String newTitle, String newContent) {
        try {
            long currentTimestampInMinutes = System.currentTimeMillis() / (60 * 1000); // 当前时间（分钟级别）
            String updateQuery = "UPDATE " + tableName + " SET title = '" + newTitle + "', content = '" + newContent + "', timestampinminutes = " + currentTimestampInMinutes + " WHERE articleid = " + articleId;
            statement.executeUpdate(updateQuery);
            System.out.println("文章已成功更新。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 静态方法：根据 articleid 删除该文章
    public static void deleteArticle(long articleId) {
        try {
            String deleteQuery = "DELETE FROM " + tableName + " WHERE articleid = " + articleId;
            statement.executeUpdate(deleteQuery);
            System.out.println("文章已成功删除。");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 返回基于最大 timestampinminutes 的前10篇文章信息
    // articleid 0, authorid 1, title 2, content 3, dianzang 4, pinglun 5, timestampinminutes 6
    public static List<String[]> getTop10ArticlesByTimestamp() {
        List<String[]> topArticlesList = new ArrayList<>();
        try {
            String query = "SELECT articleid, authorid, title, content, dianzang, pinglun, timestampinminutes FROM "
                    + tableName + " ORDER BY timestampinminutes DESC LIMIT 10";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                long articleId = resultSet.getLong("articleid");
                long authorId = resultSet.getLong("authorid");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                long dianzang = resultSet.getLong("dianzang");
                long pinglun = resultSet.getLong("pinglun");
                long timestampInMinutes = resultSet.getLong("timestampinminutes");
                topArticlesList.add(new String[]{String.valueOf(articleId), String.valueOf(authorId), title, content,
                        String.valueOf(dianzang), String.valueOf(pinglun), String.valueOf(timestampInMinutes)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topArticlesList;
    }

    // 返回基于 dianzang 和 pinglun 之和最大的前10篇文章信息
    // articleid 0, authorid 1, title 2, content 3, dianzang 4, pinglun 5, timestampinminutes 6
    public static List<String[]> getTop10ArticlesByDianzangAndPinglun() {
        List<String[]> topArticlesList = new ArrayList<>();
        try {
            String query = "SELECT articleid, authorid, title, content, dianzang, pinglun, timestampinminutes FROM "
                    + tableName + " ORDER BY (dianzang + pinglun) DESC LIMIT 10";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                long articleId = resultSet.getLong("articleid");
                long authorId = resultSet.getLong("authorid");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                long dianzang = resultSet.getLong("dianzang");
                long pinglun = resultSet.getLong("pinglun");
                long timestampInMinutes = resultSet.getLong("timestampinminutes");
                topArticlesList.add(new String[]{String.valueOf(articleId), String.valueOf(authorId), title, content,
                        String.valueOf(dianzang), String.valueOf(pinglun), String.valueOf(timestampInMinutes)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topArticlesList;
    }

    // 根据 articleid 为所在的文章的 dianzang +1
    public static void incrementDianzang(long articleId) {
        try {
            String updateQuery = "UPDATE " + tableName + " SET dianzang = dianzang + 1 WHERE articleid = " + articleId;
            statement.executeUpdate(updateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据 articleid 为所在的文章的 pinglun +1
    public static void incrementPinglun(long articleId) {
        try {
            String updateQuery = "UPDATE " + tableName + " SET pinglun = pinglun + 1 WHERE articleid = " + articleId;
            statement.executeUpdate(updateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 敏感词查询SensitiveWords,返回文章包含的敏感词
    public static String[] checkSensitiveWords(String title, String content) {
        // 将标题和内容合并为一个字符串进行检查
        String combinedText = title + " " + content;
        // 将敏感词库转换为集合，方便检查
        List<String> sensitiveWordsList = Arrays.asList(SensitiveWords);
        // 存储匹配到的敏感词
        List<String> matchedWords = new ArrayList<>();
        // 检查是否包含敏感词
        for (String word : sensitiveWordsList) {
            if (combinedText.contains(word)) {
                matchedWords.add(word); // 添加匹配到的敏感词
            }
        }
        // 将 List 转换为数组
        return matchedWords.toArray(new String[0]);
    }

    // 敏感词成员变量
    private static String[] SensitiveWords= new String[]{
            "习近平","艹","深圳大学","中共","共产","共铲党", "共残党","共惨党","共匪","赤匪","裆中央",
            "北京当局","中宣","真理部","十八大","18大","太子","上海帮","团派","九常委","九长老","政治局常委内幕",
            "锦涛","hujin","家宝","影帝","wenjiabao","wjb","近平","xijinping","xjp","假庆淋","jiaqinglin",
            "李月月鳥","回良玉","汪洋","王山支山","wangqishan","张高丽","俞正声","徐才厚","郭伯雄","梁光烈","孟建柱","戴秉国","马凯"};



}
