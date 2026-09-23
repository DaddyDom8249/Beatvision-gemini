package com.example.data.provider

import com.example.data.arena.SceneImageResult

class ProviderRegistry(
    private val imageProviders: List<ImageProvider>
) {
    fun imageProviders(): List<ImageProvider> = imageProviders

    fun descriptors(): List<ProviderDescriptor> = imageProviders.map { it.descriptor }

    suspend fun generateImage(
        request: ImageGenerationRequest,
        selectionMode: ProviderSelectionMode = ProviderSelectionMode.AUTOMATIC,
        selectedProviderId: String? = null
    ): ProviderResult<SceneImageResult> {
        val candidates = when (selectionMode) {
            ProviderSelectionMode.MANUAL -> imageProviders.filter { it.descriptor.id == selectedProviderId }
            ProviderSelectionMode.AUTOMATIC -> imageProviders.filter {
                it.descriptor.configured &&
                    ProviderCapability.IMAGE_GENERATION in it.descriptor.capabilities
            }
        }
        if (candidates.isEmpty()) {
            return ProviderResult.Failure(
                selectedProviderId ?: "registry",
                "No configured image provider supports image generation."
            )
        }

        var lastFailure: ProviderResult.Failure? = null
        for (provider in candidates) {
            when (val result = provider.generateImage(request)) {
                is ProviderResult.Success -> return result
                is ProviderResult.Failure -> {
                    lastFailure = result
                    if (selectionMode == ProviderSelectionMode.MANUAL || !result.retryable) return result
                }
            }
        }
        return lastFailure ?: ProviderResult.Failure("registry", "All image providers failed.", retryable = true)
    }
}
