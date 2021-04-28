package dev.gomelody.bot.config

public object Config : EnvironmentConfig("BOT_") {
    public val ENVIRONMENT: Environment by getEnv(Environment.PRODUCTION) { Environment.valueOf(it) }
    public val DISCORD_TOKEN: String by getEnv()
    public val LAVALINK_NODES: String by getEnv()

    public enum class Environment {
        PRODUCTION,
        STAGING,
        DEVELOPMENT
    }
}