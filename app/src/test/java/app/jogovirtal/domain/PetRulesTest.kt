package app.jogovirtal.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PetRulesTest {
    @Test fun affection_is_capped() {
        val result = PetRules.pet(PetState(energy = 80, affection = 97))
        assertEquals(100, result.affection)
        assertEquals(78, result.energy)
        assertEquals(Mood.HAPPY, result.mood)
    }

    @Test fun exhausted_pet_gets_tired_without_negative_energy() {
        val result = PetRules.pet(PetState(energy = 1, affection = 0))
        assertEquals(0, result.energy)
        assertEquals(Mood.TIRED, result.mood)
    }

    @Test fun rest_replenishes_energy_without_exceeding_limit() {
        val result = PetRules.rest(PetState(energy = 96, affection = 15, mood = Mood.TIRED))
        assertEquals(100, result.energy)
        assertEquals(15, result.affection)
        assertEquals(Mood.CURIOUS, result.mood)
    }
}
