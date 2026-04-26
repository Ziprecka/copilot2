package com.osrscopilot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class OsrsCopilotOverlay extends OverlayPanel
{
    private final OsrsCopilotPlugin plugin;
    private final Client client;

    @Inject
    public OsrsCopilotOverlay(OsrsCopilotPlugin plugin, Client client)
    {
        super(plugin);
        this.plugin = plugin;
        this.client = client;
        setPosition(OverlayPosition.TOP_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.getConfig().showPanel())
        {
            return null;
        }

        CopilotTarget target = plugin.getCurrentTarget();
        if (target == null)
        {
            return null;
        }

        panelComponent.getChildren().add(TitleComponent.builder()
            .text("OSRS CoPilot")
            .color(Color.ORANGE)
            .build());

        if (!target.active)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                .left(target.status == null ? "No target" : target.status)
                .leftColor(Color.LIGHT_GRAY)
                .build());
            return super.render(graphics);
        }

        addLine("Goal", target.goal, Color.WHITE);
        addLine("Step", target.currentStep, Color.YELLOW);

        if (plugin.getConfig().showDistance() && target.targetTile != null && client.getLocalPlayer() != null)
        {
            WorldPoint player = client.getLocalPlayer().getWorldLocation();
            int dx = target.targetTile.x - player.getX();
            int dy = target.targetTile.y - player.getY();
            int dist = Math.abs(dx) + Math.abs(dy);
            addLine("Direction", direction(dx, dy) + " / " + dist + " tiles", Color.CYAN);
        }

        if (target.blockedReason != null && !target.blockedReason.isEmpty())
        {
            addLine("Blocked", target.blockedReason, Color.RED);
        }

        if (!target.requiredItems.isEmpty())
        {
            addLine("Required", String.join(", ", target.requiredItems), Color.WHITE);
        }

        if (!target.recommendedItems.isEmpty())
        {
            addLine("Bring", String.join(", ", target.recommendedItems), Color.LIGHT_GRAY);
        }

        if (!target.missingItems.isEmpty())
        {
            addLine("Missing", String.join(", ", target.missingItems), Color.RED);
        }

        int shown = 0;
        for (String item : target.checklist)
        {
            if (shown >= 5)
            {
                break;
            }
            panelComponent.getChildren().add(LineComponent.builder()
                .left("• " + item)
                .leftColor(Color.LIGHT_GRAY)
                .build());
            shown++;
        }

        return super.render(graphics);
    }

    private void addLine(String left, String right, Color color)
    {
        panelComponent.getChildren().add(LineComponent.builder()
            .left(left)
            .right(right == null ? "" : right)
            .rightColor(color)
            .build());
    }

    private String direction(int dx, int dy)
    {
        String ns = dy > 0 ? "N" : dy < 0 ? "S" : "";
        String ew = dx > 0 ? "E" : dx < 0 ? "W" : "";
        String d = ns + ew;
        return d.isEmpty() ? "Here" : d;
    }
}
