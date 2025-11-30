package net.ditto.impl;

import net.ditto.ability.Ability;
import net.ditto.ability.IgnitionAbility;
import java.util.List;

public interface PlayerAbilityManager {
    // Sets the overarching Ignition form
    void setIgnitionAbility(IgnitionAbility ability);

    // Gets the current form
    IgnitionAbility getIgnitionAbility();

    // Gets the specific abilities granted by the current Ignition
    List<Ability> getActiveAbilities();

    // Gets the one currently highlighted in the UI (or internally selected)
    Ability getSelectedAbility();

    // Used by the scroll wheel logic
    void cycleAbility(int direction);
}