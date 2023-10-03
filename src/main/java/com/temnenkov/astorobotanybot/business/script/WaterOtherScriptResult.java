package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.parser.dto.PlantState;

public sealed interface WaterOtherScriptResult {
    enum NoPretenders implements WaterOtherScriptResult {
        INSTANCE
    }

    record TooEarly(PlantState stateBefore, PlantState stateAfter) implements WaterOtherScriptResult {

    }

    record Watered(int waterBefore, int waterAfter) implements WaterOtherScriptResult {

    }
}
