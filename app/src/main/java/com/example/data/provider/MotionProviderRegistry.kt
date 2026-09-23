package com.example.data.provider

import com.example.data.model.MotionPlan

class MotionProviderRegistry(
    private val providers: List<MotionProvider> = emptyList()
) {
    fun descriptors(): List<ProviderDescriptor> = providers.map { it.descriptor }

    fun configuredProviders(): List<MotionProvider> = providers.filter {
        it.descriptor.configured && ProviderCapability.IMAGE_TO_VIDEO in it.descriptor.capabilities
    }

    suspend fun generateMotion(
        request: MotionGenerationRequest,
        selectionMode: ProviderSelectionMode = ProviderSelectionMode.AUTOMATIC,
        selectedProviderId: String? = null
    ): ProviderResult<VideoAsset> {
        val candidates = when (selectionMode) {
            ProviderSelectionMode.MANUAL -> providers.filter { it.descriptor.id == selectedProviderId }
            ProviderSelectionMode.AUTOMATIC -> configuredProviders()
        }
        if (candidates.isEmpty()) {
            return ProviderResult.Failure(
                selectedProviderId ?: "motion-registry",
                "No configured motion provider supports image-to-video.",
                retryable = false
            )
        }
        var last: ProviderResult.Failure? = null
        for (provider in candidates) {
            when (val result = provider.generateMotion(request)) {
                is ProviderResult.Success -> return result
                is ProviderResult.Failure -> {
                    last = result
                    if (selectionMode == ProviderSelectionMode.MANUAL || !result.retryable) return result
                }
            }
        }
        return last ?: ProviderResult.Failure("motion-registry", "All motion providers failed.", retryable = true)
    }
}
