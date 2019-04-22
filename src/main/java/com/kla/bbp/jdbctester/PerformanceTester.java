package com.kla.bbp.jdbctester;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@Log4j2
public class PerformanceTester extends Tester {
    private Optional<Connection> getConnection(String connString) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(connString);

        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return Optional.ofNullable(conn);
    }

    public void ConvertFromEncryptedToClearText() {
        log.debug("connString = {}, password = {}, targetDbName = {}",
                super.getConnString(),
                super.getPassword(),
                super.getTargetDbName());

        super.CleanUpBefore();

        String sqlAttachNewDb = String.format("ATTACH DATABASE ''%s'' AS newtarget", super.getTargetDbName());

        Optional<Connection> connectionOptional = getConnection(super.getConnString());

        if (!connectionOptional.isPresent()) {
            log.error("Cannot get the SQLite Connection object");
            return;
        }

        String testName = String.format("%s.%s", this.getClass().getSimpleName(), this.getClass().getEnclosingMethod().getName());
        log.info("We are starting the {} test...", testName);
        Instant start = Instant.now();

        Connection connection = connectionOptional.get();

        try {

            Statement statement = connection.createStatement();
            log.debug("SQL statement to attach new db file: " + sqlAttachNewDb);
            statement.execute(sqlAttachNewDb);
            statement.execute(".CLONE");
            statement.execute("CREATE TABLE newtarget.blobTable AS SELECT * FROM blobTable;");
            statement.execute("DETACH DATABASE newtarget;");
            statement.close();
        } catch(SQLException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if(connection != null) {
                    connection.close();
                }
            } catch(SQLException e) {
                log.error(e.getMessage());
            }

            Instant finish = Instant.now();
            long timeElapsed = Duration.between(start, finish).toMillis();
            log.info("{} testing is done.  Time elapsed: {} ms", testName, timeElapsed);
        }
    }
}
