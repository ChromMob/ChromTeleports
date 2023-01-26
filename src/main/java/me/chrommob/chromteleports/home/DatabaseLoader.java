package me.chrommob.chromteleports.home;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.chrommob.chromteleports.ChromTeleports;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseLoader {
    private final FileConfiguration pluginConfig = ChromTeleports.instance().getConfig();
    private HikariDataSource hikari;

    public DatabaseLoader() {
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

    private void createTable() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS chrom_teleports_home (id INT NOT NULL AUTO_INCREMENT, player VARCHAR(16), home VARCHAR(16), world VARCHAR(16), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, PRIMARY KEY (id))");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    public void requestHomeData(String name, UUID player, HomeData homeData) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT * FROM chrom_teleports_home WHERE home = ? AND player = ?");
            ps.setString(1, name);
            ps.setString(2, player.toString());

            res = ps.executeQuery();
            if (res.next()) {
                homeData.setX(res.getDouble("x"));
                homeData.setY(res.getDouble("y"));
                homeData.setZ(res.getDouble("z"));
                homeData.setWorld(res.getString("world"));
                homeData.setYaw(res.getFloat("yaw"));
                homeData.setPitch(res.getFloat("pitch"));
                homeData.setLoaded(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, res);
        }
    }

    public void writeHomeData(HomeData homeData, UUID player) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("INSERT INTO chrom_teleports_home (player, home, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE world = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ?");
            ps.setString(1, player.toString());
            ps.setString(2, homeData.getName());
            ps.setString(3, homeData.getWorld());
            ps.setDouble(4, homeData.getX());
            ps.setDouble(5, homeData.getY());
            ps.setDouble(6, homeData.getZ());
            ps.setFloat(7, homeData.getYaw());
            ps.setFloat(8, homeData.getPitch());
            ps.setString(9, homeData.getWorld());
            ps.setDouble(10, homeData.getX());
            ps.setDouble(11, homeData.getY());
            ps.setDouble(12, homeData.getZ());
            ps.setFloat(13, homeData.getYaw());
            ps.setFloat(14, homeData.getPitch());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }

    public void deleteHomeData(String name, UUID player) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("DELETE FROM chrom_teleports_home WHERE home = ? AND player = ?");
            ps.setString(1, name);
            ps.setString(2, player.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, null);
        }
    }
}
