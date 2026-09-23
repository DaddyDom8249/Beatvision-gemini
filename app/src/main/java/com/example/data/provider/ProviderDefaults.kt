package com.example.data.provider

object ProviderDefaults {
    fun imageRegistry(): ProviderRegistry = ProviderRegistry(
        imageProviders = listOf(
            ArenaImageProvider(),
            PollinationsImageProvider()
        )
    )
}
