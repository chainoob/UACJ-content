package org.uacjcontent.uacj.integrations;

import net.minecraft.server.level.ServerPlayer;
import org.uacjcontent.uacj.init.AttributeInit;

public class PufferfishHandler {
    public static int getComplexityLimit(ServerPlayer player) {
        // Check for Master Tier Attribute (Value > 0)
        var master = player.getAttribute(AttributeInit.MASTER.get());
        if (master != null && master.getValue() > 0) return Integer.MAX_VALUE;

        // Check for Adept Tier Attribute (Value > 0)
        var adept = player.getAttribute(AttributeInit.ADEPT.get());
        if (adept != null && adept.getValue() > 0) return 3;

        // Default / Novice
        return 1;
    }
}