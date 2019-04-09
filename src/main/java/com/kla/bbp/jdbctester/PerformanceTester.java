package com.kla.bbp.jdbctester;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

@Service
@Log4j2
public class PerformanceTester {
    @Value("${spring.connectionString:jdbc:sqlite:source.db}")
    private String connString;

    @Value("${spring.password:}")
    private String password;

    @Value("${spring.targetDbName:plaintext.db}")
    private String targetDbName;

    public void GenerateNewDb() {
        log.debug("connString = {}, password = {}, targetDbName = {}", connString, password, targetDbName);
        CleanUpBefore();

        Connection connection = null;

        log.info("We are starting the test...");
        Instant start = Instant.now();

        try {
            connection = DriverManager.getConnection(connString);
            Statement statement = connection.createStatement();

            Object[] params = new Object[]{targetDbName, password};
            String sqlAttachNewDb = MessageFormat.format("ATTACH DATABASE ''{0}'' AS newtarget KEY ''{1}''", params);
            log.debug("SQL statement to attach new db file: " + sqlAttachNewDb);
            statement.execute(sqlAttachNewDb);
            statement.execute("CREATE TABLE newtarget.attribTable AS SELECT * FROM attribTable;");
            statement.execute("CREATE TABLE newtarget.blobTable AS SELECT * FROM blobTable;");
            statement.execute("DETACH DATABASE newtarget;");
            statement.close();
        } catch(SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            log.error(e.getMessage());
        } finally {
            try {
                if(connection != null) {
                    connection.close();
                }

                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();
                log.info("Testing is done.  Time elapsed: " + timeElapsed + " ms to generate " + targetDbName);
            } catch(SQLException e) {
                // connection close failed.
                log.error(e.getMessage());
            }
        }
    }

    private void CleanUpBefore() {
        File file = new File(targetDbName);
        if(file.delete()){
            log.info("The target db File {} existed and has been deleted.", this.targetDbName);
        } else
            log.debug("The target db file {} doesn't exist, there is nothing to delete.", this.targetDbName);
    }
}
