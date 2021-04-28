package dev.gomelody.bot.core

import dev.gomelody.bot.commands.PlayCommand
import dev.gomelody.bot.commands.TestCommand
import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.schlaubi.lavakord.LavaKord
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KordPreview
@KordUnsafe
public class GomelodyBot : KoinComponent {
    private val kord: Kord by inject()
    private val lavakord: LavaKord by inject()

    public suspend fun start() {
        kord.on<ReadyEvent> {
            guilds.forEach {
                TestCommand().register(it.id)
                PlayCommand().register(it.id)
            }
        }

        // Login Kord
        kord.login()
    }
}