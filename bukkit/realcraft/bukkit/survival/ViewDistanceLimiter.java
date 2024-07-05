package realcraft.bukkit.survival;

import org.bukkit.Bukkit;
import org.bukkit.World;
import realcraft.bukkit.RealCraft;

import java.util.ArrayList;
import java.util.LinkedList;

public class ViewDistanceLimiter implements Runnable {

    private static final int DEFAULT_VIEW_DISTANCE = 12;
    private static final int DEFAULT_SIMULATION_DISTANCE = 8;
    private static final int DISTANCE_APPLY_TIMEOUT = 180 * 1000;

    private long lastDistanceLimitsApplied = 0;
    private final LinkedList<Integer> history = new LinkedList<>();
    private final ArrayList<DistanceLimits> thresholdLimits = new ArrayList<>();

    public ViewDistanceLimiter() {
        thresholdLimits.add(new DistanceLimits(0,  DEFAULT_VIEW_DISTANCE, DEFAULT_SIMULATION_DISTANCE));
        thresholdLimits.add(new DistanceLimits(14, DEFAULT_VIEW_DISTANCE - 1, DEFAULT_SIMULATION_DISTANCE - 1));
        thresholdLimits.add(new DistanceLimits(18, DEFAULT_VIEW_DISTANCE - 2, DEFAULT_SIMULATION_DISTANCE - 2));
        thresholdLimits.add(new DistanceLimits(22, DEFAULT_VIEW_DISTANCE - 3, DEFAULT_SIMULATION_DISTANCE - 3));
        thresholdLimits.add(new DistanceLimits(26, DEFAULT_VIEW_DISTANCE - 4, DEFAULT_SIMULATION_DISTANCE - 4));

        thresholdLimits.get(0).apply();

        Bukkit.getScheduler().runTaskTimer(RealCraft.getInstance(), this, 10 * 20, 10 * 20);
    }

    @Override
    public void run() {
        history.add(Bukkit.getOnlinePlayers().size());

        if (history.size() < 4) {
            return;
        }

        history.remove();

        this._checkThresholdLimits();
    }

    protected double _getAveragePlayers() {
        double avg = 0;

        for (final double f : history) {
            avg += f;
        }

        return avg / history.size();
    }

    protected void _checkThresholdLimits() {
        double averagePlayers = this._getAveragePlayers();

        DistanceLimits limit = null;

        for (DistanceLimits l : thresholdLimits) {
            if (l.minPlayers <= averagePlayers) {
                limit = l;
            }
        }

        if (lastDistanceLimitsApplied + (DISTANCE_APPLY_TIMEOUT) > System.currentTimeMillis()) {
            return;
        }

        if (limit != null && limit.apply()) {
            lastDistanceLimitsApplied = System.currentTimeMillis();
            System.out.println("DistanceLimits applied: " + limit.viewDistance + "; " + limit.simulationDistance);
        }
    }

    private record DistanceLimits(int minPlayers, int viewDistance, int simulationDistance) {

        public boolean apply() {
            boolean applied = false;

            for (World world: Bukkit.getWorlds()) {
                if (world.getViewDistance() != viewDistance) {
                    world.setViewDistance(viewDistance);
                    world.setSendViewDistance(viewDistance + 1);
                    applied = true;
                }

                if (world.getSimulationDistance() != simulationDistance) {
                    world.setSimulationDistance(simulationDistance);
                    applied = true;
                }
            }

            return applied;
        }
    }
}
