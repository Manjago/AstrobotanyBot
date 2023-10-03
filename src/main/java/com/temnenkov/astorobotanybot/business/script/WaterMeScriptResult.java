package com.temnenkov.astorobotanybot.business.script;

import com.temnenkov.astorobotanybot.business.parser.dto.PlantState;

public sealed interface WaterMeScriptResult {
    record DoNothing(PlantState stateBefore, int waterLimit) implements WaterMeScriptResult {

    }

    record NoChanges(PlantState stateBefore, PlantState stateAfter) implements WaterMeScriptResult {

    }

    record Watered(int waterBefore, int waterAfter) implements WaterMeScriptResult {

    }
}
