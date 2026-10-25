package app.jogovirtal.domain

import android.content.Context

/** Somente fatos do pet persistem; nunca imagens da câmera. */
class PetStore(context: Context) {
    private val prefs = context.getSharedPreferences("pet_state_v1", Context.MODE_PRIVATE)

    fun load(): PetState {
        val energy = prefs.getInt("energy", 80).coerceIn(0, 100)
        val affection = prefs.getInt("affection", 0).coerceIn(0, 100)
        val mood = prefs.getString("mood", Mood.CURIOUS.name)
            ?.let { name -> Mood.entries.firstOrNull { it.name == name } }
            ?: Mood.CURIOUS
        return PetState(energy, affection, mood)
    }

    fun save(pet: PetState) {
        prefs.edit()
            .putInt("energy", pet.energy)
            .putInt("affection", pet.affection)
            .putString("mood", pet.mood.name)
            .apply()
    }
}
