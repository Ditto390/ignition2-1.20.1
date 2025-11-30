package net.ditto;


import net.ditto.content.DevilsFootIgnition;
import net.ditto.impl.PlayerAbilityManager;
import net.ditto.registry.IgnitionRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ignition implements ModInitializer {
    public static final String MOD_ID = "ignition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier PACKET_CYCLE_ABILITY = new Identifier(MOD_ID, "cycle_ability");
    public static final Identifier PACKET_SYNC_IGNITION = new Identifier(MOD_ID, "sync_ignition"); // <--- ADD THIS

    @Override
    public void onInitialize() {
        LOGGER.info("Ignition Mod Initializing...");

        // 1. Register Abilities
        IgnitionRegistry.register(new Identifier(MOD_ID, "devils_foot"), new DevilsFootIgnition());

        // 2. Register Commands


        // 3. Register Networking
        ServerPlayNetworking.registerGlobalReceiver(PACKET_CYCLE_ABILITY, (server, player, handler, buf, responseSender) -> {
            int direction = buf.readInt();
            server.execute(() -> {
                if (player instanceof PlayerAbilityManager) {
                    ((PlayerAbilityManager) player).cycleAbility(direction);
                }
            });
        });
    }
}