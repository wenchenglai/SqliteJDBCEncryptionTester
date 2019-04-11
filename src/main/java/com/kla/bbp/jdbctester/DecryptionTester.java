package com.kla.bbp.jdbctester;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
@Log4j2
public class DecryptionTester extends Tester {
    public void DecryptNewDb() {
        this.TesterWrapper(connection -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM company LIMIT 1");
                resultSet.next();
                String name = resultSet.getString("name");
                log.info("Data fetched from encrypted SQLite file (should be KLA): " + name);
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, false, this.getClass().getSimpleName());
    }
}
