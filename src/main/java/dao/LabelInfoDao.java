package dao;

import entity.LabelInfo;
import org.apache.log4j.Logger;
import util.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 标签信息
 * Created by zhangtao on 2017/11/6.
 */
public class LabelInfoDao {

    private Logger log = Logger.getLogger(LabelInfoDao.class);

    public boolean insertLabelInfo(LabelInfo labelInfo)throws SQLException{
        Connection connection = JdbcUtil.getConnection();
        String sql = "insert into t_label(ocrLabelName, middleX, middleY, scrollCount, lineName, step, labelNum, labelType, moduleType, tabNum)"+
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        log.info(sql);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,labelInfo.getOcrLabelName());
        preparedStatement.setInt(2,labelInfo.getMiddleX());
        preparedStatement.setInt(3,labelInfo.getMiddleY());
        preparedStatement.setInt(4,labelInfo.getScrollCount());
        preparedStatement.setString(5,labelInfo.getLineName());
        preparedStatement.setString(6,labelInfo.getStep());
        preparedStatement.setInt(7,labelInfo.getLabelNum());
        preparedStatement.setString(8,labelInfo.getLabelType());
        preparedStatement.setString(9,labelInfo.getModuleType());
        preparedStatement.setInt(10,labelInfo.getTabNum());
        int result = preparedStatement.executeUpdate();
//        log.info("受影响的行数:" + result);
        JdbcUtil.close(null,preparedStatement,connection);
        return result > 0 ? true : false;
    }
}
