package app.jogovirtal.domain

enum class Mood { CURIOUS, HAPPY, TIRED }

data class PetState(
    val energy: Int = 80,
    val affection: Int = 0,
    val mood: Mood = Mood.CURIOUS
) {
    init {
        require(energy in 0..100)
        require(affection in 0..100)
    }
}

/** Fonte única de verdade das regras locais. Independente de AR e IA. */
object PetRules {
    fun pet(state: PetState): PetState = state.copy(
        energy = (state.energy - 2).coerceAtLeast(0),
        affection = (state.affection + 10).coerceAtMost(100),
        mood = if (state.energy <= 2) Mood.TIRED else Mood.HAPPY
    )

    fun rest(state: PetState): PetState = state.copy(
        energy = (state.energy + 25).coerceAtMost(100),
        mood = Mood.CURIOUS
    )
}
