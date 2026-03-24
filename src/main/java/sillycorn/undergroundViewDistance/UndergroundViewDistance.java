package sillycorn.undergroundViewDistance;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class UndergroundViewDistance extends JavaPlugin {
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

        this.undergroundView = getConfig().getInt("settings.underground-view-distance", 8);
        this.undergroundSimulation = getConfig().getInt("settings.underground-simulation-distance", 4);
        this.undergroundY = getConfig().getDouble("settings.underground-y", 0.0);
        this.surfaceY = getConfig().getDouble("settings.surface-y", 20.0);
        this.checkInterval = getConfig().getLong("settings.check-interval-ticks", 200L);
        this.defaultView = Bukkit.getServer().getViewDistance();
        this.defaultSimulation = Bukkit.getServer().getSimulationDistance();

        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.VIEW_DISTANCE) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        event.getPacket().getIntegers().write(0, defaultView);
                    }
                }
        );

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            if (players.isEmpty()) {
                return;
            }

            for (Player player : players) {
                if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
                    if (player.getViewDistance() != defaultView) {
                        player.setViewDistance(defaultView);
                        player.setSendViewDistance(defaultView);
                    }
                    if (player.getSimulationDistance() != defaultSimulation) {
                        player.setSimulationDistance(defaultSimulation);
                    }
                    continue;
                }

                double y = player.getY();

                if (y < undergroundY) {
                    if (player.getViewDistance() != undergroundView) {
                        player.setViewDistance(undergroundView);
                        player.setSendViewDistance(undergroundView);
                    }
                    if (player.getSimulationDistance() != undergroundSimulation) {
                        player.setSimulationDistance(undergroundSimulation);
                    }
                }
                else if (y > surfaceY) {
                    if (player.getViewDistance() != defaultView) {
                        player.setViewDistance(defaultView);
                        player.setSendViewDistance(defaultView);
                    }
                    if (player.getSimulationDistance() != defaultSimulation) {
                        player.setSimulationDistance(defaultSimulation);
                    }
                }
            }
        }, 0L, checkInterval);
        getLogger().info("Underground View & Simulation Distance active - Config Loaded Successfully");
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getViewDistance() != defaultView) {
                player.setViewDistance(defaultView);
                player.setSendViewDistance(defaultView);
            }
            if (player.getSimulationDistance() != defaultSimulation) {
                player.setSimulationDistance(defaultSimulation);
            }
        }
        getLogger().info("Underground View Distance off");
    }
}