package com.nooblabs.thermalconfigchanger

enum class ThermalMode (val value: Int) {
    GAME(9),
    INCALL(8),
    EVALUATION(10),
    CLASS_0(11),
    CAMERA(12),
    PUBG(13),
    YOUTUBE(14),
    EXTREME(2),
    ARVR(15),
    GAME2(16),
    RESTORE(0),
    DEFAULT(-1);

    override fun toString(): String {
        return name
    }

}