package dev.gomelody.bot.commands

import dev.gomelody.bot.command.SlashCommand
import dev.gomelody.bot.core.PlaySoundService
import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KordPreview
@KordUnsafe
public class PlayCommand : SlashCommand(), KoinComponent {
    override val name: String = "play"
    override val description: String = "Play a sound from the library."
    override val builder: ApplicationCommandCreateBuilder.() -> Unit = {
        string("sound", "The sound you want to play.") {
            require(true)
        }
    }

    private val playSoundService: PlaySoundService by inject()

    override suspend fun handle(event: InteractionCreateEvent) {
        with(event.interaction) {
            val ack = acknowledgeEphemeral()

            val guildId = data.guildId.value ?: return ack.send("Could not get server.")
            val member = user.asMember(guildId)

            playSoundService.playSound(member, "aaaa") {
                ack.followUp(it)
            }
        }
    }
}