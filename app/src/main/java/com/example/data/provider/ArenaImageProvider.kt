package com.example.data.provider

import com.example.data.arena.ArenaGatewayClient
import com.example.data.arena.ArenaNetworkException
import com.example.data.arena.ArenaRateLimitException
import com.example.data.arena.ArenaProviderException
import com.example.data.arena.SceneImageResult

class ArenaImageProvider(
    private val client: ArenaGatewayClient = ArenaGatewayClient()
) : ImageProvider {
    override val descriptor = ProviderDescriptor(
        id = "arena",
        displayName = "BeatVision Arena",
        capabilities = setOf(ProviderCapability.IMAGE_GENERATION),
        configured = true,
        supportsReferenceImages = true,
        supportsCharacterReferences = true,
        supportsCameraControls = true,
        authMethod = "server-side gateway"
    )

    override suspend fun generateImage(
        request: ImageGenerationRequest
    ): ProviderResult<SceneImageResult> {
        return client.generateSceneImage(request.scene, request.world).fold(
            onSuccess = { ProviderResult.Success(it, descriptor.id, it.model) },
            onFailure = {
                ProviderResult.Failure(
                    descriptor.id,
                    it.message ?: "Arena image generation failed.",
                    retryable = it is ArenaNetworkException || it is ArenaRateLimitException || it is ArenaProviderException,
                    cause = it
                )
            }
        )
    }
}
