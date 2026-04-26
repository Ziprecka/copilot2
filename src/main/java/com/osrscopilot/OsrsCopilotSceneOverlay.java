package com.osrscopilot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class OsrsCopilotSceneOverlay extends Overlay
{
    private final OsrsCopilotPlugin plugin;
    private final Client client;

    @Inject
    public OsrsCopilotSceneOverlay(OsrsCopilotPlugin plugin, Client client)
    {
        this.plugin = plugin;
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.getConfig().showSceneMarkers())
        {
            return null;
        }

        CopilotTarget target = plugin.getCurrentTarget();
        if (target == null || !target.active)
        {
            return null;
        }

        renderTargetTile(graphics, target);
        renderNpcHighlights(graphics, target);
        renderObjectHighlights(graphics, target);

        return null;
    }

    private void renderTargetTile(Graphics2D graphics, CopilotTarget target)
    {
        if (target.targetTile == null)
        {
            return;
        }

        WorldPoint wp = new WorldPoint(target.targetTile.x, target.targetTile.y, target.targetTile.plane);
        LocalPoint lp = LocalPoint.fromWorld(client, wp);
        if (lp == null)
        {
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly != null)
        {
            graphics.setStroke(new BasicStroke(2));
            OverlayUtil.renderPolygon(graphics, poly, Color.YELLOW);
            OverlayUtil.renderTextLocation(graphics,
                Perspective.getCanvasTextLocation(client, graphics, lp, target.targetTile.label, 0),
                target.targetTile.label,
                Color.YELLOW);
        }
    }

    private void renderNpcHighlights(Graphics2D graphics, CopilotTarget target)
    {
        if (target.highlightNpcs == null || target.highlightNpcs.isEmpty())
        {
            return;
        }

        for (NPC npc : client.getNpcs())
        {
            if (npc == null || npc.getName() == null)
            {
                continue;
            }

            if (!matches(target.highlightNpcs, npc.getName()))
            {
                continue;
            }

            Polygon hull = npc.getConvexHull();
            if (hull != null)
            {
                graphics.setStroke(new BasicStroke(2));
                OverlayUtil.renderPolygon(graphics, hull, Color.CYAN);
            }

            if (npc.getCanvasTextLocation(graphics, npc.getName(), npc.getLogicalHeight() + 40) != null)
            {
                OverlayUtil.renderTextLocation(graphics,
                    npc.getCanvasTextLocation(graphics, npc.getName(), npc.getLogicalHeight() + 40),
                    npc.getName(),
                    Color.CYAN);
            }
        }
    }

    private void renderObjectHighlights(Graphics2D graphics, CopilotTarget target)
    {
        if (target.highlightObjects == null || target.highlightObjects.isEmpty())
        {
            return;
        }

        Tile[][][] tiles = client.getScene().getTiles();
        int z = client.getPlane();

        for (int x = 0; x < tiles[z].length; x++)
        {
            for (int y = 0; y < tiles[z][x].length; y++)
            {
                Tile tile = tiles[z][x][y];
                if (tile == null)
                {
                    continue;
                }

                for (GameObject object : tile.getGameObjects())
                {
                    if (object == null || object.getName() == null)
                    {
                        continue;
                    }

                    if (!matches(target.highlightObjects, object.getName()))
                    {
                        continue;
                    }

                    Polygon poly = object.getCanvasTilePoly();
                    if (poly != null)
                    {
                        graphics.setStroke(new BasicStroke(2));
                        OverlayUtil.renderPolygon(graphics, poly, Color.ORANGE);
                    }

                    if (object.getCanvasTextLocation(graphics, object.getName(), 60) != null)
                    {
                        OverlayUtil.renderTextLocation(graphics,
                            object.getCanvasTextLocation(graphics, object.getName(), 60),
                            object.getName(),
                            Color.ORANGE);
                    }
                }
            }
        }
    }

    private boolean matches(Iterable<String> wanted, String actual)
    {
        String cleanActual = clean(actual);

        for (String w : wanted)
        {
            if (w == null)
            {
                continue;
            }

            String cleanWanted = clean(w);
            if (cleanActual.equals(cleanWanted) || cleanActual.contains(cleanWanted))
            {
                return true;
            }
        }

        return false;
    }

    private String clean(String s)
    {
        return s == null ? "" : s.toLowerCase().replace('\u00A0', ' ').trim();
    }
}
