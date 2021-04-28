package dev.gomelody.bot.commands

import dev.gomelody.bot.command.SlashCommand
import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder

@KordPreview
@KordUnsafe
public class TestCommand : SlashCommand() {
    override val name: String = "test"
    override val description: String = "Test Command"
    override val builder: ApplicationCommandCreateBuilder.() -> Unit = {
        user("yeeter", "a yeeter") {
            require(true)
        }
    }

    override suspend fun handle(event: InteractionCreateEvent) {
        with(event.interaction) {
            val ack = acknowledgeEphemeral()
            ack.followUp {
                content = command.options["yeeter"]?.member()?.joinedAt.toString()
            }
        }
    }
}
