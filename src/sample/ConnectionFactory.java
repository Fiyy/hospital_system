/**
 * FileName: ConnectionFactory
 * Author:   Zhang Yun
 * Date:     2020/5/22 9:17
 * Description:
 * History:
 */
package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zhang Yun
 * @create 2020/5/22
 * @since 1.0.0
 */
public class ConnectionFactory {
    public static Connection connect(String url, String name, String password){
        System.out.println("正在连接...");
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url,"root","Zhangyun0130");
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动加载错误");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("数据库连接错误");
            e.printStackTrace();
        }
        if(connection!=null){
            System.out.println("连接成功");
        }
        return connection;
    }
}