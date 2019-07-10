package no.kristiania.pgr203.webshop;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> {
    protected DataSource dataSource;

    public AbstractDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public T retrieve(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement("select * from products where id = ?")) {
                stmt.setInt(1, id);
                return queryForSingle(stmt);
            }
        }
    }

    private T queryForSingle(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapResultSet(rs);
            }
            return null;
        }
    }

    protected abstract T mapResultSet(ResultSet rs) throws SQLException;

    protected List<T> listAll(String sql) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            try (var stmt = connection.prepareStatement(sql)) {
                return queryForList(stmt);
            }
        }
    }

    protected List<T> queryForList(PreparedStatement stmt) throws SQLException {
        try (var rs = stmt.executeQuery()) {
            var products = new ArrayList<T>();
            while (rs.next()) {
                products.add(mapResultSet(rs));
            }
            return products;
        }
    }
}
