package org.uacjcontent.uacj.api;

import net.minecraft.server.level.ServerPlayer;
import org.uacjcontent.uacj.init.AttributeInit;

public class PuffishHandler {
    public static int getComplexityLimit(ServerPlayer player) {
        var master = player.getAttribute(AttributeInit.MASTER.get());
        if (master != null && master.getValue() > 0) return Integer.MAX_VALUE;

        var adept = player.getAttribute(AttributeInit.ADEPT.get());
        if (adept != null && adept.getValue() > 0) return 3;

        return 1;
    }
}