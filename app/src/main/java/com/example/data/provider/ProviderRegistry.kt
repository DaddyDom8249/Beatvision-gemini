package com.example.data.provider

class ProviderRegistry(
    private val providers: List<ImageProvider>
) {
    fun descriptors(): List<ProviderDescriptor> = providers.map { it.descriptor }

    fun configuredProviders(): List<ImageProvider> = providers.filter {
        it.descriptor.configured && ProviderCapability.IMAGE_GENERATION in it.descriptor.capabilities
    }

    suspend fun generateImage(
        request: ImageGenerationRequest,
        selectionMode: ProviderSelectionMode = ProviderSelectionMode.AUTOMATIC,
        selectedProviderId: String? = null
    ): ProviderResult<com.example.data.arena.SceneImageResult> {
        val candidates = when (selectionMode) {
            ProviderSelectionMode.AUTOMATIC -> configuredProviders()
            ProviderSelectionMode.MANUAL -> providers.filter {
                it.descriptor.id == selectedProviderId &&
                    it.descriptor.configured &&
                    ProviderCapability.IMAGE_GENERATION in it.descriptor.capabilities
            }
        }

        if (candidates.isEmpty()) {
            return ProviderResult.Failure(
                selectedProviderId ?: "image-registry",
                if (selectionMode == ProviderSelectionMode.MANUAL)
                    "The selected image provider is not configured or unavailable."
                else
                    "No configured image provider is available.",
                retryable = false
            )
        }

        var lastFailure: ProviderResult.Failure? = null
        for (provider in candidates) {
            when (val result = provider.generateImage(request)) {
                is ProviderResult.Success -> return result
                is ProviderResult.Failure -> {
                    lastFailure = result
                    if (selectionMode == ProviderSelectionMode.MANUAL || !result.retryable) {
                        return result
                    }
                }
            }
        }

        return lastFailure ?: ProviderResult.Failure(
            "image-registry",
            "All configured image providers failed.",
            retryable = true
        )
    }
}
