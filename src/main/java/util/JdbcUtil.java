package util;

import java.sql.*;

/**
 * Created by zhangtao on 2017/11/6.
 */
public class JdbcUtil {

    static {
        try {
            Class.forName(PropertyUtil.getValueByKey("mysql.driverClassName"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    //建立连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(PropertyUtil.getValueByKey("mysql.url"), PropertyUtil.getValueByKey("mysql.username"), PropertyUtil.getValueByKey("mysql.password"));
    }

    //释放资源
    public static void close(ResultSet rs, Statement st, Connection conn) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (st != null)
                    st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null)
                        conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
