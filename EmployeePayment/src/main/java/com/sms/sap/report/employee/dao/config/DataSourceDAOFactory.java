package com.sms.sap.report.employee.dao.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

class DataSourceDAOFactory extends DAOFactory {
    private DataSource dataSource;

    DataSourceDAOFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
