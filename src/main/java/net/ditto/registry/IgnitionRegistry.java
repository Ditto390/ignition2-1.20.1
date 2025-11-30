package net.ditto.registry;

import net.ditto.ability.IgnitionAbility;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class IgnitionRegistry {
    private static final Map<Identifier, IgnitionAbility> IGNITIONS = new HashMap<>();

    public static void register(Identifier id, IgnitionAbility ability) {
        IGNITIONS.put(id, ability);
    }

    public static IgnitionAbility get(Identifier id) {
        return IGNITIONS.get(id);
    }

    public static Map<Identifier, IgnitionAbility> getAll() {
        return IGNITIONS;
    }

    public static Identifier getId(IgnitionAbility ability) {
        for (Map.Entry<Identifier, IgnitionAbility> entry : IGNITIONS.entrySet()) {
            if (entry.getValue() == ability) {
                return entry.getKey();
            }
        }
        return null;
    }
}