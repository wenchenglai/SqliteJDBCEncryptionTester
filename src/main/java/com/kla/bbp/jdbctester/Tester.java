package com.kla.bbp.jdbctester;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

@Log4j2
public class Tester {
    @Value("${spring.connectionString:jdbc:sqlite:source.db}")
    private String connString;

    @Value("${spring.password:}")
    private String password;

    @Value("${spring.targetDbName:plaintext.db}")
    private String targetDbName;

    public void TesterWrapper(Consumer<Connection> consumer, Boolean needClean, String testName) {
        log.debug("connString = {}, password = {}, targetDbName = {}", connString, password, targetDbName);

        if(needClean) {
            CleanUpBefore();
        }

        Connection connection = null;

        log.info("We are starting the {} test...", testName);
        Instant start = Instant.now();

        try {
            connection = DriverManager.getConnection(connString, "", password);

            consumer.accept(connection);
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

    private void CleanUpBefore() {
        File file = new File(targetDbName);
        if(file.delete()){
            log.info("The target db File {} existed and has been deleted.", this.targetDbName);
        } else
            log.debug("The target db file {} doesn't exist, there is nothing to delete.", this.targetDbName);
    }
}
