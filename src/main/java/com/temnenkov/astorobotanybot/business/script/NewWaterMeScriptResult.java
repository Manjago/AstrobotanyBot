package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.parser.dto.PlantState;

public sealed interface NewWaterMeScriptResult {
    record DoNothing(PlantState stateBefore, int waterLimit) implements NewWaterMeScriptResult {

    }

    record NoChanges(PlantState stateBefore, PlantState stateAfter) implements NewWaterMeScriptResult {

    }

    record Watered(int waterBefore, int waterAfter) implements NewWaterMeScriptResult {

    }
}
