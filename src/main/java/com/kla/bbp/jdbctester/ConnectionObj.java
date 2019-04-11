package com.kla.bbp.jdbctester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionObj {
    private Connection connection;
    private String connString;
    private String password;
    private String targetDbName;
}
