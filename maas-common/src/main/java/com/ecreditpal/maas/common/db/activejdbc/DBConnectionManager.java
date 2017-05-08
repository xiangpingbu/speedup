package com.ecreditpal.maas.common.db.activejdbc;

import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class DBConnectionManager {
    private static Logger logger = LoggerFactory.getLogger(DBConnectionManager.class);
    
    private final static String DB_HOST = ConfigurationManager.getConfiguration().getString("db.host", "localhost");
    private final static String DB_USERNAME = ConfigurationManager.getConfiguration().getString("db.username", "root");
    private final static String DB_PASSWORD = ConfigurationManager.getConfiguration().getString("db.password", "Cisco123");


    private static DBConnectionManager instance = new DBConnectionManager();

    private Map<String, DataSource> dbPools = new HashMap<>();
    
    private DBConnectionManager() {
    }
    
    public static DBConnectionManager getInstance() {
        return instance;
    }
    
    public DataSource createDataSource(String dbName) {
        PoolProperties properties = new PoolProperties();
        properties.setName(dbName);
      
        properties.setUrl("jdbc:mysql://" + DB_HOST + ":3306/" + dbName);
        // properties.setUrl("jdbc:mysql://" + DB_HOST + ":3306/" + dbName + "?user=" + DB_USERNAME + "&password=" + DB_PASSWORD);
        properties.setDriverClassName("com.mysql.jdbc.Driver");
        properties.setUsername(DB_USERNAME);
        properties.setPassword(DB_PASSWORD);
        
        properties.setJmxEnabled(true);
        properties.setTestWhileIdle(false);
        properties.setTestOnBorrow(true);
        properties.setValidationQuery("SELECT 1");
        properties.setTestOnReturn(false);
        properties.setValidationInterval(30000);
        properties.setTimeBetweenEvictionRunsMillis(30000);
        properties.setMaxActive(100);
        properties.setInitialSize(10);
        properties.setMaxWait(10000);
        properties.setRemoveAbandonedTimeout(60);
        properties.setMinEvictableIdleTimeMillis(30000);
        properties.setMinIdle(10);
        properties.setLogAbandoned(true);
        properties.setRemoveAbandoned(true);
        properties.setJdbcInterceptors(
          "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
          "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;" +
                  "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");

        
        logger.info("connection info {}", properties.toString());
        DataSource dataSource = new DataSource();
        
        dataSource.setPoolProperties(properties);
        
        try {
            dataSource.createPool();
        }
        catch (Throwable t) {
            logger.error("Failed to create connection pool for {}", dbName);
        }
        
        return dataSource;
    }
    
    public synchronized DataSource getDataSource(String dbName) {
       if (!dbPools.containsKey(dbName))  {
           DataSource dataSource = createDataSource(dbName);
           dbPools.put(dbName, dataSource);
       }
       
       return dbPools.get(dbName);
    }
    
    public void returnConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception e) {
            logger.error("Exception happened.", e);
        }
    }

    public Map<String, DataSource> getDbPools() {
        return dbPools;
    }
}
