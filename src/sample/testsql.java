/**
 * FileName: testsql
 * Author:   Zhang Yun
 * Date:     2020/5/21 23:28
 * Description:
 * History:
 */
package sample;

import javax.xml.crypto.Data;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author Zhang Yun
 * @create 2020/5/21
 * @since 1.0.0
 */

public class testsql {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital_system";
        System.out.println("正在连接...");
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = null;
        connection = DriverManager.getConnection(url,"root","Zhangyun0130");
        if(connection!=null){
            System.out.println("连接成功。");
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM doctor");
        System.out.println("查询的数据如下：");
        while(resultSet.next()){
            String s1 = resultSet.getString("name");
            System.out.println(s1);
            Date date = resultSet.getDate(7);
            System.out.println(date);
            new SimpleDateFormat("yyyy-mm-dd").format(date);
        }
        //statement.executeUpdate("INSERT INTO doctor VALUES ('13','01','2','3','4','5','0000-00-00 00:00:00')");
        resultSet.close();
        statement.close();
        connection.close();
    }
}