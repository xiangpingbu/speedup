package com.ecreditpal.maas.common.db.activejdbc;

import org.javalite.activejdbc.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author lifeng
 * @version 1.0 on 2017/5/1.
 */
public class ActiveJDBCUtil {
    private static Logger logger = LoggerFactory.getLogger(ActiveJDBCUtil.class);

    public static DB getDB(String dbName) {
        logger.debug("Trying to get DB connection for {}", dbName);

        DB db = new DB(dbName);

        if (db.hasConnection()) {
            // clean connections when the thread tries to get DB
            Connection conn = db.getConnection();

            try {

                if (conn.isClosed()) {
                    db.close();
                    db.open(DBConnectionManager.getInstance().getDataSource(dbName));
                }
            } catch (Exception e) {
                logger.warn("Failed to close connection for {}", dbName, e);
            }
        } else {
            logger.debug("No DB connection for {}, opening a new one", dbName);
            db.open(DBConnectionManager.getInstance().getDataSource(dbName));
        }
        return db;
    }

    public static void main(String[] args) {
      DB db =   ActiveJDBCUtil.getDB("wool");
     List<Map> list =  db.all("select * from tool_model");
    }


}
