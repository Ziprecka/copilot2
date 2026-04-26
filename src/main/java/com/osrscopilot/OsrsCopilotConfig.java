package com.osrscopilot;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("osrscopilot")
public interface OsrsCopilotConfig extends Config
{
    @ConfigItem(
        keyName = "enablePlugin",
        name = "Enable",
        description = "Enable OSRS CoPilot overlays",
        position = 0
    )
    default boolean enablePlugin()
    {
        return true;
    }

    @ConfigItem(
        keyName = "brainEndpoint",
        name = "Brain endpoint",
        description = "Local Python brain endpoint",
        position = 1
    )
    default String brainEndpoint()
    {
        return "http://127.0.0.1:11777/current-target";
    }

    @ConfigItem(
        keyName = "showPanel",
        name = "Show top-left helper",
        description = "Show the Quest Helper-style instruction panel",
        position = 2
    )
    default boolean showPanel()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showSceneMarkers",
        name = "Show scene markers",
        description = "Highlight target tiles, NPCs, and objects in the game scene",
        position = 3
    )
    default boolean showSceneMarkers()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showDistance",
        name = "Show distance",
        description = "Show distance and rough direction to current target",
        position = 4
    )
    default boolean showDistance()
    {
        return true;
    }
}
