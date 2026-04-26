package com.osrscopilot;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
    name = "OSRS CoPilot",
    description = "AI route/task helper overlay for OSRS Leagues",
    tags = {"league", "tasks", "guide", "helper", "overlay", "wiki"}
)
public class OsrsCopilotPlugin extends Plugin
{
    private static final Gson GSON = new Gson();

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private OsrsCopilotOverlay overlay;

    @Inject
    private OsrsCopilotSceneOverlay sceneOverlay;

    @Inject
    private OsrsCopilotConfig config;

    @Getter
    private CopilotTarget currentTarget = CopilotTarget.empty();

    @Provides
    OsrsCopilotConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(OsrsCopilotConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        overlayManager.add(sceneOverlay);
        log.info("OSRS CoPilot started");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        overlayManager.remove(sceneOverlay);
        currentTarget = CopilotTarget.empty();
        log.info("OSRS CoPilot stopped");
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if ("osrscopilot".equals(event.getGroup()))
        {
            log.info("OSRS CoPilot config changed");
        }
    }

    @Schedule(period = 1, unit = ChronoUnit.SECONDS)
    public void pollBrain()
    {
        if (!config.enablePlugin())
        {
            return;
        }

        String endpoint = config.brainEndpoint();
        try
        {
            HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(500);
            conn.setReadTimeout(750);

            int status = conn.getResponseCode();
            if (status != 200)
            {
                currentTarget = CopilotTarget.offline("Brain returned HTTP " + status);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())))
            {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line);
                }

                CopilotTarget parsed = GSON.fromJson(sb.toString(), CopilotTarget.class);
                if (parsed == null)
                {
                    currentTarget = CopilotTarget.offline("Brain returned empty target");
                }
                else
                {
                    currentTarget = parsed.withDefaults();
                }
            }
        }
        catch (Exception e)
        {
            currentTarget = CopilotTarget.offline("Brain offline: " + e.getClass().getSimpleName());
        }
    }

    public Client getClient()
    {
        return client;
    }

    public OsrsCopilotConfig getConfig()
    {
        return config;
    }
}
