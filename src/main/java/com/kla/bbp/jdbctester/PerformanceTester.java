package com.kla.bbp.jdbctester;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Statement;

@Service
@Log4j2
public class PerformanceTester extends Tester {
    public void GenerateNewDb() {
        this.TesterWrapper((connection, sqlAttach) -> {
            try {
                Statement statement = connection.createStatement();
                log.debug("SQL statement to attach new db file: " + sqlAttach);
                statement.execute(sqlAttach);
                statement.execute("CREATE TABLE newtarget.attribTable AS SELECT * FROM attribTable;");
                statement.execute("CREATE TABLE newtarget.blobTable AS SELECT * FROM blobTable;");
                statement.execute("DETACH DATABASE newtarget;");
                statement.close();
            } catch (SQLException e) {
                log.error(e.getLocalizedMessage());
            }
        }, true, this.getClass().getSimpleName() + ".GenerateNewDb");
    }
}
