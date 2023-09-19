import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
        private val preferences: SharedPreferences = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE)

    // Recupere o token de autenticação
    fun getToken(): String? {
        return preferences.getString("token", "")
    }

}
