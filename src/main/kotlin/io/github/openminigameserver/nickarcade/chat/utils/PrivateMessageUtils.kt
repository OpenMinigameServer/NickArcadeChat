package io.github.openminigameserver.nickarcade.chat.utils

import io.github.openminigameserver.nickarcade.chat.components.PrivateMessageComponent
import io.github.openminigameserver.nickarcade.chat.events.impl.PrivateMessageDeliverAttemptEvent
import io.github.openminigameserver.nickarcade.chat.events.impl.PrivateMessageDeliverResult
import io.github.openminigameserver.nickarcade.chat.impl.PrivateMessageChannel
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.RuntimeExtraDataTag
import io.github.openminigameserver.nickarcade.core.manager.consoleData
import io.github.openminigameserver.nickarcade.plugin.extensions.launchAsync
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor.RED
import net.kyori.adventure.text.format.NamedTextColor.WHITE
import kotlin.time.minutes

private val lastReplyTag = RuntimeExtraDataTag.of<ArcadeSender>("last-reply", 5.minutes)

var ArcadeSender.lastReply: ArcadeSender?
    get() = get(lastReplyTag)
    set(value) {
        set(lastReplyTag, value)
    }

object PrivateMessageUtils {

    private fun getMessageToSend(prefix: String, user: ArcadeSender, message: Component, rawMessage: Component): ComponentLike {
        return PrivateMessageComponent(prefix, user, message, rawMessage)
    }

    fun replyPrivateMessage(from: ArcadeSender, message: Component) {
        val targetReply = from.lastReply
        if (targetReply == null) {
            from.audience.sendMessage(text("There is no one to reply to!", RED))
            return
        }
        sendPrivateMessage(from, targetReply, message)
    }

    fun sendPrivateMessage(from: ArcadeSender, to: ArcadeSender, message: Component) {
        launchAsync {
            var privateMessageTarget = to

            val isOriginalOffline = privateMessageTarget is ArcadePlayer && !privateMessageTarget.isOnline
            if (isOriginalOffline) {
                val event =
                    PrivateMessageDeliverAttemptEvent(from, privateMessageTarget, message, PrivateMessageDeliverResult.PLAYER_OFFLINE)
                event.callEvent()
                if (event.result == PrivateMessageDeliverResult.PLAYER_OFFLINE) {
                    from.audience.sendMessage(text("That player is not online!", RED))
                    return@launchAsync
                } else {
                    privateMessageTarget = event.target
                }
            }

            val result = PrivateMessageChannel.processChatMessage(from, message)
            from.audience.sendMessage(getMessageToSend("To", privateMessageTarget, result, message))
            privateMessageTarget.audience.sendMessage(getMessageToSend("From", from, result, message))
            privateMessageTarget.lastReply = from
            consoleData.audience.sendMessage(text {
                it.append(text("["))
                it.append(text(from.getChatName(actualData = true, colourPrefixOnly = false)))
                it.append(text(" -> ", WHITE))
                it.append(text(privateMessageTarget.getChatName(actualData = true, colourPrefixOnly = false)))
                it.append(text("]: ", WHITE))
                it.append(result)
            })
        }
    }
}