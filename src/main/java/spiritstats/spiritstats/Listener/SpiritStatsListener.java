package spiritstats.spiritstats.Listener;

import org.bukkit.event.Listener;
import spiritstats.spiritstats.api.SpiritStatsAPI;

public class SpiritStatsListener implements Listener {

    private final SpiritStatsAPI api;

    public SpiritStatsListener(SpiritStatsAPI api) {
        this.api = api;
    }
}