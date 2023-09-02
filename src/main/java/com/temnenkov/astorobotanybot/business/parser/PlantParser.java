package com.temnenkov.astorobotanybot.business.parser;

import com.temnenkov.astorobotanybot.business.parser.dto.PlantStage;
import com.temnenkov.astorobotanybot.business.parser.dto.PlantState;
import org.jetbrains.annotations.NotNull;

public class PlantParser {
    @NotNull
    public PlantState parse(@NotNull String geminiText) {



        return new PlantState(1, PlantStage.MATURE, );
    }
}
