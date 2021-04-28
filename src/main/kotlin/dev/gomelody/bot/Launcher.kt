package dev.gomelody.bot

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import dev.gomelody.bot.config.Config
import dev.gomelody.bot.core.GomelodyBot
import dev.gomelody.bot.core.PlaySoundService
import dev.kord.common.annotation.KordPreview
import dev.kord.common.annotation.KordUnsafe
import dev.kord.core.Kord
import dev.schlaubi.lavakord.kord.lavakord
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.slf4j.LoggerFactory

@KordPreview
@KordUnsafe
public suspend fun main() {
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    rootLogger.level = Level.INFO

    val kord = Kord(Config.DISCORD_TOKEN)
    val lavalink = kord.lavakord().apply {
        Config.LAVALINK_NODES.split("|").forEach {
            val conf = it.split("_")
            addNode(conf[0], conf[1], conf[2])
        }
    }

    val diModule = module {
        single { kord }
        single { lavalink }
        single { PlaySoundService() }
    }

    startKoin {
        modules(diModule)
    }

    GomelodyBot().start()
}