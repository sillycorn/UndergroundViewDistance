package sillycorn.undergroundViewDistance;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class UndergroundViewDistance extends JavaPlugin implements Listener {
    private int defaultView;
    private int undergroundView;
    private int defaultSimulation;
    private int undergroundSimulation;
    private double undergroundY;
    private double surfaceY;
    private long checkInterval;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.undergroundView = getConfig().getInt("settings.underground-view-distance", 6);
        this.undergroundSimulation = getConfig().getInt("settings.underground-simulation-distance", 4);
        this.undergroundY = getConfig().getDouble("settings.underground-y", 0.0);
        this.surfaceY = getConfig().getDouble("settings.surface-y", 20.0);
        this.checkInterval = getConfig().getLong("settings.check-interval-ticks", 200L);
        this.defaultView = Bukkit.getServer().getViewDistance();
        this.defaultSimulation = Bukkit.getServer().getSimulationDistance();

        Bukkit.getPluginManager().registerEvents(this, this);

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.VIEW_DISTANCE) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        event.getPacket().getIntegers().write(0, defaultView);
                    }
                }
        );

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updatePlayer(player);
            }
        }, 0L, checkInterval);
        getLogger().info("Underground View & Simulation Distance active - Config Loaded Successfully");
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            setDistances(player, defaultView, defaultSimulation);
        }
        getLogger().info("Underground View Distance off");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Bukkit.getScheduler().runTask(this, () -> updatePlayer(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTask(this, () -> updatePlayer(event.getPlayer()));
    }

    private void updatePlayer(Player player) {
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            setDistances(player, defaultView, defaultSimulation);
            return;
        }

        double y = player.getY();
        if (y < undergroundY) {
            setDistances(player, undergroundView, undergroundSimulation);
        } else if (y > surfaceY) {
            setDistances(player, defaultView, defaultSimulation);
        }
    }

    private void setDistances(Player player, int view, int simulation) {
        if (player.getViewDistance() != view) {
            player.setViewDistance(view);
            player.setSendViewDistance(view);
        }
        if (player.getSimulationDistance() != simulation) {
            player.setSimulationDistance(simulation);
        }
    }
}