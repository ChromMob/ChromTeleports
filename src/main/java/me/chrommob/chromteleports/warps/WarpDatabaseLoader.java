package me.chrommob.chromteleports.warps;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.chrommob.chromteleports.ChromTeleports;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WarpDatabaseLoader {
    private final FileConfiguration pluginConfig = ChromTeleports.instance().getConfig();
    private HikariDataSource hikari;

    public WarpDatabaseLoader() {
        ChromTeleports.instance().getLogger().info("DatabaseManager initialized!");
        setupPool();
        createTable();
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                "jdbc:mysql://" +
                        pluginConfig.getString("mysql.ip") +
                        ":" +
                        pluginConfig.getString("mysql.port") +
                        "/" +
                        pluginConfig.getString("mysql.database") +
                        "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(pluginConfig.getString("mysql.username"));
        config.setPassword(pluginConfig.getString("mysql.password"));
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(100);
        config.setConnectionTimeout(2000);
        config.setLeakDetectionThreshold(60000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        // config.setConnectionTestQuery(testQuery);
        hikari = new HikariDataSource(config);
    }

    private void createTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS chrom_teleports_warps (id INT NOT NULL AUTO_INCREMENT, name TEXT, world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, PRIMARY KEY (id))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    public Location loadByName(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT * FROM chrom_teleports_warps WHERE name = ?");
            ps.setString(1, name);
            res = ps.executeQuery();
            if (res.next()) {
                return new Location(
                        ChromTeleports.instance().getServer().getWorld(res.getString("world")),
                        res.getDouble("x"),
                        res.getDouble("y"),
                        res.getDouble("z"),
                        res.getFloat("yaw"),
                        res.getFloat("pitch")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
        return null;
    }

    public Map<String, Location> loadAll() {
        Map<String, Location> warps = new ConcurrentHashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT * FROM chrom_teleports_warps");
            res = ps.executeQuery();
            while (res.next()) {
                warps.put(res.getString("name"), new Location(
                        ChromTeleports.instance().getServer().getWorld(res.getString("world")),
                        res.getDouble("x"),
                        res.getDouble("y"),
                        res.getDouble("z"),
                        res.getFloat("yaw"),
                        res.getFloat("pitch")
                ));
            }
            ChromTeleports.instance().getLogger().info("Loaded " + warps.size() + " warps!");
            return warps;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
        return new ConcurrentHashMap<>();
    }

    private void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        if (ps != null)
            try {
                ps.close();
            } catch (SQLException ignored) {
            }
        if (res != null)
            try {
                res.close();
            } catch (SQLException ignored) {
            }
    }

    public void saveWarp(String name, Location location) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("INSERT INTO chrom_teleports_warps (name, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, location.getWorld().getName());
            ps.setDouble(3, location.getX());
            ps.setDouble(4, location.getY());
            ps.setDouble(5, location.getZ());
            ps.setFloat(6, location.getYaw());
            ps.setFloat(7, location.getPitch());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    public void deleteWarp(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("DELETE FROM chrom_teleports_warps WHERE name = ?");
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }
}
