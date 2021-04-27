package dev.gomelody.bot

import dev.kord.core.Kord
import dev.gomelody.bot.config.Config

public suspend fun main() {
    val client = Kord(Config.DISCORD_TOKEN)
    client.login()
}