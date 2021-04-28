package dev.gomelody.bot.core

import dev.kord.core.behavior.MemberBehavior
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.kord.getLink
import dev.schlaubi.lavakord.rest.loadItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

public class PlaySoundService : KoinComponent {
    private val lavakord: LavaKord by inject()

    public suspend fun playSound(member: MemberBehavior, sound: String, res: suspend (String) -> Unit) {
        val link = member.getGuild().getLink(lavakord)
        val channel = member.getVoiceStateOrNull()?.channelId ?: return res("sksksk")
        println(channel.value)
        link.connectAudio(channel.value)
        val track = link.loadItem("https://translate.google.com/translate_tts?ie=UTF-8&tl=en-US&client=tw-ob&q=" + "Hello YESSSSSS THIS IS VERY GREAT T ATATATATATTATATAT".replace(' ', '+')).track
        link.player.playTrack(track)
        res("Joined")
    }
}