package com.kla.bbp.jdbctester;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
@Log4j2
public class DecryptionTester extends Tester {
    public void DecryptNewDb() {
        this.TesterWrapper((connection, sqlAttach) -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM company LIMIT 1");
                resultSet.next();
                String name = resultSet.getString("name");
                log.info("Data fetched from encrypted SQLite file (should be KLA): " + name);
                statement.close();
            } catch (SQLException e) {
                log.error(e.getLocalizedMessage());
            }
        }, false, this.getClass().getSimpleName() + ".DecryptNewDb");
    }

    public void DecryptNewDbWithWrongPassword() {
        this.TesterWrapper((connection, sqlAttach) -> {
            try {
                log.info("Testing with wrong password...");
                Statement statement = connection.createStatement();
                statement.execute(String.format("PRAGMA key='%s'", "wrong"));
                ResultSet resultSet = statement.executeQuery("SELECT * FROM company LIMIT 1");
                resultSet.next();
                String name = resultSet.getString("name");
                log.info("Data fetched from encrypted SQLite file (you are not supposed to see this KLA): " + name);
                statement.close();
            } catch (SQLException e) {
                log.error(e.getLocalizedMessage());
            }
        }, false, this.getClass().getSimpleName() + ".DecryptNewDbWithWrongPassword");
    }
}
