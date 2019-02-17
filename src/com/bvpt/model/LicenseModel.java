/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bvpt.model;

import com.bvpt.data.LicenseItem;
import com.bvpt.database.MySqlFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/**
 *
 * @author tamvh
 */
public class LicenseModel {
    private static LicenseModel _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected final Logger logger = Logger.getLogger(this.getClass());

    public static LicenseModel getInstance() throws IOException {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new LicenseModel();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    public String getTableName() {
        return "license";
    }
    
    public LicenseItem getLicense(String appCode) {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        LicenseItem licenseItem = null;
        try {
            connection = MySqlFactory.getConnection();
            stmt = connection.createStatement();
            String tableName = getTableName();
            String query = String.format("SELECT * FROM %s WHERE appcode = '%s' LIMIT 0,1", tableName, appCode);
            stmt.execute(query);
            rs = stmt.getResultSet();
            if (rs != null) {
                while (rs.next()) {
                    licenseItem = new LicenseItem();
                    licenseItem.setAppCode(rs.getString("appcode"));
                    licenseItem.setLicCode(rs.getString("liccode"));
                    licenseItem.setRegType(rs.getInt("regtype"));
                    licenseItem.setRegUser(rs.getString("reguser"));
                    licenseItem.setRegDate(rs.getString("regdate"));
                    return licenseItem;
                }
            }
        } catch (SQLException ex) {
            logger.error(getClass().getSimpleName() + ".getLicense: " + ex.getMessage(), ex);
            return licenseItem;
        } finally {
            MySqlFactory.safeClose(rs);
            MySqlFactory.safeClose(stmt);
            MySqlFactory.safeClose(connection);
        }
        return licenseItem;
    }
    
    public int regLicense(
            String appCode,
            String licCode,
            int regType,
            String regUser) {
        int ret = 0;
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = MySqlFactory.getConnection();
            stmt = connection.createStatement();
            String tableName = getTableName();
            String regDate = "";
            String query = String.format("INSERT INTO %s (appcode,liccode,regtype,reguser,regdate) VALUES ('%s','%s',%d,'%s','%s')",
                    tableName, appCode, licCode, regType, regUser, regDate);
            return stmt.executeUpdate(query);
        } catch (SQLException ex) {
            logger.error(getClass().getSimpleName() + ".regLicense: " + ex.getMessage(), ex);
            return ret;
        } finally {
            MySqlFactory.safeClose(rs);
            MySqlFactory.safeClose(stmt);
            MySqlFactory.safeClose(connection);
        }
    }
}
