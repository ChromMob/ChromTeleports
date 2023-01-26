package me.chrommob.chromteleports.home;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.chrommob.chromteleports.ChromTeleports;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS chrom_teleports_home (id INT NOT NULL AUTO_INCREMENT, player TEXT, home TEXT, world TEXT, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, PRIMARY KEY (id))");
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
            ps = conn.prepareStatement("SELECT * FROM chrom_teleports_home");
            res = ps.executeQuery();
            double x = 0;
            double y = 0;
            double z = 0;
            float yaw = 0;
            float pitch = 0;
            String world = "";
            boolean loaded = false;
            while (res.next()) {
                if (res.getString("player").equals(player.toString()) && res.getString("home").equals(name)) {
                    x = res.getDouble("x");
                    y = res.getDouble("y");
                    z = res.getDouble("z");
                    yaw = res.getFloat("yaw");
                    pitch = res.getFloat("pitch");
                    world = res.getString("world");
                    loaded = true;
                    break;
                }
            }
            if (loaded) {
                homeData.setX(x);
                homeData.setY(y);
                homeData.setZ(z);
                homeData.setYaw(yaw);
                homeData.setPitch(pitch);
                homeData.setWorld(world);
                homeData.setLoaded(true);
            } else {
                ChromTeleports.instance().getLogger().info("Home " + name + " not found for player " + player.toString());
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
        if (alreadyExists(homeData.getName(), player)) {
            try {
                conn = hikari.getConnection();
                ps = conn.prepareStatement("UPDATE chrom_teleports_home SET x = ?, y = ?, z = ?, world = ?, yaw = ?, pitch = ? WHERE home = ? AND player = ?");
                ps.setDouble(1, homeData.getX());
                ps.setDouble(2, homeData.getY());
                ps.setDouble(3, homeData.getZ());
                ps.setString(4, homeData.getWorld());
                ps.setFloat(5, homeData.getYaw());
                ps.setFloat(6, homeData.getPitch());
                ps.setString(7, homeData.getName());
                ps.setString(8, player.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                close(conn, ps, null);
            }
        } else {
            try {
                conn = hikari.getConnection();
                ps = conn.prepareStatement("INSERT INTO chrom_teleports_home (player, home, world, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, player.toString());
                ps.setString(2, homeData.getName());
                ps.setString(3, homeData.getWorld());
                ps.setDouble(4, homeData.getX());
                ps.setDouble(5, homeData.getY());
                ps.setDouble(6, homeData.getZ());
                ps.setFloat(7, homeData.getYaw());
                ps.setFloat(8, homeData.getPitch());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                close(conn, ps, null);
            }
        }
    }

    private boolean alreadyExists(String name, UUID player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT * FROM chrom_teleports_home");
            res = ps.executeQuery();

            while (res.next()) {
                if (res.getString("home").equals(name) && res.getString("player").equals(player.toString())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, res);
        }
        return false;
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

    public Map<UUID, Set<HomeData>> loadAllHomes() {
        Map<UUID, Set<HomeData>> homeData = new ConcurrentHashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try {
            conn = hikari.getConnection();
            ps = conn.prepareStatement("SELECT * FROM chrom_teleports_home");
            res = ps.executeQuery();
            while (res.next()) {
                UUID player = UUID.fromString(res.getString("player"));
                String home = res.getString("home");
                String world = res.getString("world");
                double x = res.getDouble("x");
                double y = res.getDouble("y");
                double z = res.getDouble("z");
                float yaw = res.getFloat("yaw");
                float pitch = res.getFloat("pitch");
                HomeData data = new HomeData(home, new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch), player, false);
                if (!homeData.containsKey(player)) {
                    Set<HomeData> set = new HashSet<>();
                    set.add(data);
                    homeData.put(player, set);
                } else {
                    homeData.get(player).add(data);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps, res);
        }
        return homeData;
    }
}
