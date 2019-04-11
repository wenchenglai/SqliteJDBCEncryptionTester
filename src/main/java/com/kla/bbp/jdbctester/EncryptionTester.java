package com.kla.bbp.jdbctester;

import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Statement;

@Service
public class EncryptionTester extends Tester {
    public void EncryptNewDb() {
        this.TesterWrapper(connection -> {
            try {
                Statement statement = connection.createStatement();
                //statement.execute(String.format("PRAGMA key='%s'", password));
                statement.executeUpdate("DROP TABLE if EXISTS company");
                statement.executeUpdate("CREATE TABLE company (id integer, name string)");
                statement.executeUpdate("INSERT INTO company VALUES(1, 'KLA')");
                statement.executeUpdate("INSERT INTO company VALUES(2, 'XYZ')");

                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, true, this.getClass().getSimpleName());
    }
}
