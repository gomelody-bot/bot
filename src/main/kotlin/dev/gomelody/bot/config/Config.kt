package dev.gomelody.bot.config

public object Config : EnvironmentConfig("DISCORD_BOT_") {
    public val ENVIRONMENT: Environment by getEnv(Environment.PRODUCTION) { Environment.valueOf(it) }
    public val DISCORD_TOKEN: String by getEnv()

    public enum class Environment {
        PRODUCTION,
        STAGING,
        DEVELOPMENT
    }
}