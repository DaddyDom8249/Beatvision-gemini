package com.example.data.provider

import android.content.Context
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class ProviderPreferenceStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun putApiKey(providerId: String, apiKey: String) {
        require(providerId.isNotBlank() && apiKey.isNotBlank())
        prefs.edit().putString(keyName(providerId), encrypt(apiKey)).apply()
    }

    fun getApiKey(providerId: String): String? =
        prefs.getString(keyName(providerId), null)?.let(::decrypt)

    fun removeApiKey(providerId: String) {
        prefs.edit().remove(keyName(providerId)).apply()
    }

    private fun keyName(providerId: String) = "api_key_${providerId.trim()}"

    private fun getKey(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
        generator.init(256)
        return generator.generateKey()
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(
            ByteBuffer.allocate(4 + cipher.iv.size + encrypted.size)
                .putInt(cipher.iv.size)
                .put(cipher.iv)
                .put(encrypted)
                .array(),
            Base64.NO_WRAP
        )
    }

    private fun decrypt(value: String): String? = try {
        val bytes = Base64.decode(value, Base64.NO_WRAP)
        val buffer = ByteBuffer.wrap(bytes)
        val ivSize = buffer.int
        if (ivSize !in 12..32 || buffer.remaining() <= ivSize) return null
        val iv = ByteArray(ivSize).also(buffer::get)
        val encrypted = ByteArray(buffer.remaining()).also(buffer::get)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getKey(), GCMParameterSpec(128, iv))
        String(cipher.doFinal(encrypted), Charsets.UTF_8)
    } catch (_: Exception) {
        null
    }

    private companion object {
        const val PREFS = "beatvision_provider_preferences"
        const val KEY_ALIAS = "BeatVisionProviderCredentialKey"
    }
}
