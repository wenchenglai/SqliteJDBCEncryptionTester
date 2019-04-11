package com.kla.bbp.jdbctester;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Statement;

@Service
@Log4j2
public class EncryptionTester extends Tester {
    public void EncryptNewDb() {
        this.TesterWrapper((connection, sqlAttach) -> {
            try {
                Statement statement = connection.createStatement();
                //statement.execute(String.format("PRAGMA key='%s'", "aaa"));
                statement.executeUpdate("DROP TABLE if EXISTS company");
                statement.executeUpdate("CREATE TABLE company (id integer, name string)");
                statement.executeUpdate("INSERT INTO company VALUES(1, 'KLA')");
                statement.executeUpdate("INSERT INTO company VALUES(2, 'XYZ')");

                statement.close();
            } catch (SQLException e) {
                log.error(e.getLocalizedMessage());
            }
        }, true, this.getClass().getSimpleName() + ".EncryptNewDb");
    }
}
