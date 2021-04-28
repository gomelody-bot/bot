package dev.gomelody.bot.command

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.EphemeralFollowupMessageBehavior
import dev.kord.core.behavior.interaction.EphemeralInteractionResponseBehavior
import dev.kord.core.behavior.interaction.InteractionResponseBehavior
import dev.kord.core.behavior.interaction.followUp
import dev.kord.core.entity.Member
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.ResolvedChannel
import dev.kord.core.entity.interaction.OptionValue
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.interaction.ApplicationCommandCreateBuilder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KordPreview
public abstract class SlashCommand : KoinComponent {
    public abstract val name: String
    public abstract val description: String
    public abstract val builder: ApplicationCommandCreateBuilder.() -> Unit

    private val kord: Kord by inject()

    public suspend fun register() {
        kord.slashCommands.createGlobalApplicationCommand(name, description, builder)
        kord.events.buffer(Channel.UNLIMITED)
            .filterIsInstance<InteractionCreateEvent>()
            .filter { it.interaction.command.rootName == name }
            .onEach {
                kord.launch {
                    handle(it)
                }
            }.launchIn(kord)
    }

    public suspend fun register(guild: Snowflake) {
        kord.slashCommands.createGuildApplicationCommand(guild, name, description, builder)
        kord.events.buffer(Channel.UNLIMITED)
            .filterIsInstance<InteractionCreateEvent>()
            .filter { it.interaction.command.rootName == name }
            .onEach {
                kord.launch {
                    handle(it)
                }
            }.launchIn(kord)
    }

    public abstract suspend fun handle(event: InteractionCreateEvent)

    public suspend fun EphemeralInteractionResponseBehavior.send(content: String): Unit {
        followUp(content)
    }

    public fun OptionValue<*>.user(): User = value as User
    public fun OptionValue<*>.role(): Role = value as Role
    public fun OptionValue<*>.member(): Member = value as Member
    public fun OptionValue<*>.channel(): ResolvedChannel = value as ResolvedChannel
    public fun OptionValue<*>.string(): String = value as String
    public fun OptionValue<*>.int(): Int = value as Int
    public fun OptionValue<*>.boolean(): Boolean = value as Boolean
}