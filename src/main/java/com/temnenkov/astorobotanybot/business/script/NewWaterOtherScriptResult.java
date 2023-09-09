package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.parser.dto.PlantState;

public sealed interface NewWaterOtherScriptResult {
    enum NoPretenders implements NewWaterOtherScriptResult {
        INSTANCE;
    }

    record TooEarly(PlantState stateBefore, PlantState stateAfter) implements NewWaterOtherScriptResult {

    }

    record Watered(int waterBefore, int waterAfter) implements NewWaterOtherScriptResult {

    }
}
