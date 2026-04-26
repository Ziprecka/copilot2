package com.osrscopilot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CopilotTarget
{
    public boolean active = false;
    public String status = "No target";
    public String goal = "";
    public String currentStep = "";
    public String tooltip = "";
    public String blockedReason = "";

    public TargetTile targetTile;

    public List<String> highlightNpcs = new ArrayList<>();
    public List<String> highlightObjects = new ArrayList<>();
    public List<String> highlightItems = new ArrayList<>();
    public List<String> requiredItems = new ArrayList<>();
    public List<String> recommendedItems = new ArrayList<>();
    public List<String> missingItems = new ArrayList<>();
    public List<String> checklist = new ArrayList<>();

    public RenderOptions render = new RenderOptions();

    public static CopilotTarget empty()
    {
        CopilotTarget t = new CopilotTarget();
        t.active = false;
        t.status = "No current target";
        t.tooltip = "Open OSRS CoPilot brain and choose a task.";
        return t;
    }

    public static CopilotTarget offline(String message)
    {
        CopilotTarget t = new CopilotTarget();
        t.active = false;
        t.status = message;
        t.tooltip = message;
        return t;
    }

    public CopilotTarget withDefaults()
    {
        if (highlightNpcs == null) highlightNpcs = Collections.emptyList();
        if (highlightObjects == null) highlightObjects = Collections.emptyList();
        if (highlightItems == null) highlightItems = Collections.emptyList();
        if (requiredItems == null) requiredItems = Collections.emptyList();
        if (recommendedItems == null) recommendedItems = Collections.emptyList();
        if (missingItems == null) missingItems = Collections.emptyList();
        if (checklist == null) checklist = Collections.emptyList();
        if (render == null) render = new RenderOptions();
        if (goal == null) goal = "";
        if (currentStep == null) currentStep = "";
        if (tooltip == null) tooltip = "";
        if (status == null) status = "";
        if (blockedReason == null) blockedReason = "";
        return this;
    }

    public static class TargetTile
    {
        public int x;
        public int y;
        public int plane;
        public String label = "Target";
    }

    public static class RenderOptions
    {
        public boolean questHelperStylePanel = true;
        public boolean minimapFlashingArrow = true;
        public boolean worldTileMarker = true;
        public boolean topLeftTooltip = true;
        public boolean sidebarChecklist = true;
    }
}
