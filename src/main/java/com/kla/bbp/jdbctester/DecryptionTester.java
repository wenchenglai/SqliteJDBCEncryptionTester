package com.kla.bbp.jdbctester;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;

@Service
@Log4j2
public class DecryptionTester {
    @Value("${spring.connectionString:jdbc:sqlite:source.db}")
    private String connString;

    @Value("${spring.password:}")
    private String password;

    @Value("${spring.targetDbName:plaintext.db}")
    private String targetDbName;

    public void DecryptNewDb() {
        log.debug("connString = {}, password = {}, targetDbName = {}", connString, password, targetDbName);
        Connection connection = null;

        log.info("We are starting the decryption test...");
        Instant start = Instant.now();

        try {
            connection = DriverManager.getConnection(connString, "", password);
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM company LIMIT 1");
            resultSet.next();
            String name = resultSet.getString("name");
            log.info("Data fetched from encrypted SQLite file (should be KLA): " + name);

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
}
