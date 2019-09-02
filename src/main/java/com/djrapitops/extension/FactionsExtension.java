/*
    Copyright(c) 2019 Risto Lahtela (Rsl1122)

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package com.djrapitops.extension;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.FormatType;
import com.djrapitops.plan.extension.Group;
import com.djrapitops.plan.extension.NotReadyException;
import com.djrapitops.plan.extension.annotation.*;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.settings.SettingsService;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Factions DataExtension.
 *
 * @author Rsl1122
 */
@PluginInfo(name = "Factions", iconName = "map", iconFamily = Family.SOLID, color = Color.GREEN)
public class FactionsExtension implements DataExtension {

    public FactionsExtension() {
    }

    private MPlayer getMPlayer(UUID playerUUID) {
        MPlayer player = MPlayer.get(playerUUID);
        if (player == null) throw new NotReadyException();
        return player;
    }

    private Optional<Faction> getFaction(UUID playerUUID) {
        MPlayer player = getMPlayer(playerUUID);
        if (!player.hasFaction()) return Optional.empty();

        Faction faction = player.getFaction();
        if (faction.isNone()) return Optional.empty();

        List<String> ignoredFactions = getIgnoredFactions();
        if (ignoredFactions.contains(faction.getName())) return Optional.empty();

        return Optional.of(faction);
    }

    private List<String> getIgnoredFactions() {
        return SettingsService.getInstance().getStringList("Factions.HideFactions", () -> Collections.singletonList("ExampleFaction"));
    }

    @GroupProvider(text = "Faction", iconName = "flag", groupColor = Color.GREEN)
    public String[] faction(UUID playerUUID) {
        return getFaction(playerUUID)
                .map(faction -> new String[]{faction.getName()})
                .orElse(new String[0]);
    }

    @StringProvider(
            text = "Leader",
            description = "Who leads the faction",
            playerName = true,
            iconName = "user",
            iconColor = Color.GREEN
    )
    public String factionLeader(Group factionName) {
        Faction faction = Faction.get(factionName.getGroupName());
        return faction.getLeader().getName();
    }

    @DoubleProvider(
            text = "Power",
            description = "How much power the player had when they logged in/out",
            priority = 100,
            iconName = "bolt",
            iconColor = Color.GREEN
    )
    public double power(UUID playerUUID) {
        return getMPlayer(playerUUID).getPower();
    }

    @DoubleProvider(
            text = "Power",
            description = "How much power the faction has",
            priority = 100,
            iconName = "bolt",
            iconColor = Color.GREEN
    )
    public double power(Group factionName) {
        return Faction.get(factionName.getGroupName()).getPower();
    }

    @DoubleProvider(
            text = "Max Power",
            description = "How much power the faction can have",
            priority = 95,
            iconName = "bolt",
            iconColor = Color.GREEN
    )
    public double maxPower(Group factionName) {
        return Faction.get(factionName.getGroupName()).getPowerMax();
    }

    @DoubleProvider(
            text = "Max Power",
            description = "How much power the player can have",
            priority = 95,
            iconName = "bolt",
            iconColor = Color.GREEN
    )
    public double maxPower(UUID playerUUID) {
        return getMPlayer(playerUUID).getPowerMax();
    }

    @NumberProvider(
            text = "Created",
            description = "When the faction was created",
            priority = 90,
            iconName = "calendar",
            iconColor = Color.GREEN,
            iconFamily = Family.REGULAR,
            format = FormatType.DATE_YEAR
    )
    public long created(Group factionName) {
        return Faction.get(factionName.getGroupName()).getCreatedAtMillis();
    }
}