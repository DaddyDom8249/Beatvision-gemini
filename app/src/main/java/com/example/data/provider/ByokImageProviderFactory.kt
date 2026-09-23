package com.example.data.provider

import android.content.Context

data class CustomImageProviderConfig(
    val providerId: String,
    val displayName: String,
    val endpoint: String,
    val model: String,
    val supportsReferenceImages: Boolean = false,
    val supportsCharacterReferences: Boolean = false,
    val supportsCameraControls: Boolean = false
)

class ByokImageProviderFactory(context: Context) {
    private val credentials = ProviderPreferenceStore(context)

    fun saveConfiguration(config: CustomImageProviderConfig, apiKey: String) {
        require(config.providerId.isNotBlank())
        require(config.displayName.isNotBlank())
        require(config.endpoint.startsWith("https://"))
        require(config.model.isNotBlank())
        credentials.putApiKey(config.providerId, apiKey)
        contextPrefs.edit()
            .putString("display_name_${config.providerId}", config.displayName)
            .putString("endpoint_${config.providerId}", config.endpoint)
            .putString("model_${config.providerId}", config.model)
            .putBoolean("refs_${config.providerId}", config.supportsReferenceImages)
            .putBoolean("chars_${config.providerId}", config.supportsCharacterReferences)
            .putBoolean("camera_${config.providerId}", config.supportsCameraControls)
            .apply()
    }

    fun buildProvider(providerId: String): OpenAiCompatibleImageProvider? {
        val key = credentials.getApiKey(providerId) ?: return null
        val endpoint = contextPrefs.getString("endpoint_$providerId", null) ?: return null
        val model = contextPrefs.getString("model_$providerId", null) ?: return null
        val displayName = contextPrefs.getString("display_name_$providerId", providerId) ?: providerId
        return OpenAiCompatibleImageProvider(
            descriptor = ProviderDescriptor(
                id = providerId,
                displayName = displayName,
                capabilities = setOf(ProviderCapability.IMAGE_GENERATION),
                configured = true,
                supportsReferenceImages = contextPrefs.getBoolean("refs_$providerId", false),
                supportsCharacterReferences = contextPrefs.getBoolean("chars_$providerId", false),
                supportsCameraControls = contextPrefs.getBoolean("camera_$providerId", false),
                authMethod = "user-supplied API key"
            ),
            endpoint = endpoint,
            apiKey = key,
            model = model
        )
    }

    fun removeConfiguration(providerId: String) {
        credentials.removeApiKey(providerId)
        contextPrefs.edit().remove("display_name_$providerId")
            .remove("endpoint_$providerId")
            .remove("model_$providerId")
            .remove("refs_$providerId")
            .remove("chars_$providerId")
            .remove("camera_$providerId")
            .apply()
    }

    private val contextPrefs = context.getSharedPreferences(
        "beatvision_provider_connections",
        Context.MODE_PRIVATE
    )
}
