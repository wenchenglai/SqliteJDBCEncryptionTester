package com.kla.bbp.jdbctester;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.sqlite.SQLiteConnection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.function.BiConsumer;


@Log4j2
@Data
public class Tester {
    @Value("${spring.connectionString:jdbc:sqlite:source.db}")
    private String connString;

    @Value("${spring.password:}")
    private String password;

    @Value("${spring.targetDbName:plaintext.db}")
    private String targetDbName;

    public void TesterWrapper(BiConsumer<Connection, String> consumer, Boolean needClean, String testName) {
        log.debug("connString = {}, password = {}, targetDbName = {}", connString, password, targetDbName);

        if(needClean) {
            CleanUpBefore();
        }

        //Connection connection = null;



        SQLiteConnection connection = null;

        String sqlAttachNewDb;
        if (password.isEmpty()) {
            Object[] params = new Object[]{targetDbName};
            sqlAttachNewDb = MessageFormat.format("ATTACH DATABASE ''{0}'' AS newtarget", params);
        } else {
            Object[] params = new Object[]{targetDbName, password};
            sqlAttachNewDb = MessageFormat.format("ATTACH DATABASE ''{0}'' AS newtarget KEY ''{1}''", params);
        }

        log.info("We are starting the {} test...", testName);
        Instant start = Instant.now();

        try {

            //connection = DriverManager.getConnection(connString, "", password);
            connection = (SQLiteConnection)DriverManager.getConnection(connString, "", password);

            if (SQLiteConnection.hasEncryptionCapability()) {
                log.info("JDBC Encryption is enabled.");
            } else {
                log.info("JDBC Encryption is disabled.");
            }
            //connection = DriverManager.getConnection(connString);

            consumer.accept(connection, sqlAttachNewDb);
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
                log.info("{} testing is done.  Time elapsed: {} ms", testName, timeElapsed);
            } catch(SQLException e) {
                // connection close failed.
                log.error(e.getMessage());
            }
        }
    }

    protected void CleanUpBefore() {
        File file = new File(targetDbName);
        if(file.delete()){
            log.info("The target db File {} existed and has been deleted.", this.targetDbName);
        } else
            log.debug("The target db file {} doesn't exist, there is nothing to delete.", this.targetDbName);
    }
}
