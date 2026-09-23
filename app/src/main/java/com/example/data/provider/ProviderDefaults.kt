package com.example.data.provider

import com.example.data.arena.ArenaGatewayClient

object ProviderDefaults {
    fun imageRegistry(): ProviderRegistry =
        ProviderRegistry(
            listOf(
                ArenaImageProvider(ArenaGatewayClient()),
                PollinationsImageProvider()
            )
        )
}
