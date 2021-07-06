package io.github.openminigameserver.nickarcade.chat.impl

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.chat.events.impl.AsyncChatChannelMessageSentEvent
import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.chat.model.ChatEmote
import io.github.openminigameserver.nickarcade.chat.model.ChatMessageOrigin
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.plugin.extensions.launchAsync
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit

abstract class AbstractChatChannel(val type: ChatChannelType) {
    open val showActualValues: Boolean = type.useActualName

    open suspend fun checkSender(sender: ArcadeSender, origin: ChatMessageOrigin): Boolean = true

    abstract suspend fun getRecipients(sender: ArcadeSender, message: Component): Audience

    open suspend fun getSenderRateLimit(sender: ArcadeSender): Duration {
        return if (sender is ArcadePlayer) {
            getPlayerRateLimit(sender)
        } else
            Duration.ZERO
    }

    open suspend fun getPlayerRateLimit(sender: ArcadePlayer): Duration = Duration.ZERO

    open suspend fun formatMessage(
        sender: ArcadeSender,
        senderName: ComponentLike,
        message: ComponentLike
    ): ComponentLike {
        val chatColor =
            if (sender.hasAtLeastRank(
                    HypixelPackageRank.VIP,
                    showActualValues
                )
            ) NamedTextColor.WHITE else NamedTextColor.GRAY

        return text { builder ->
            if (type.prefix != null)
                builder.append(text(
                    "${
                        type.name.lowercase(Locale.getDefault())
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }> ", type.prefix.color))
            builder.append(senderName)
            builder.append(text(": ", chatColor))
            builder.append(text("", chatColor).append(message))
        }
    }

    suspend fun sendMessageInternal(sender: ArcadeSender, message: Component, origin: ChatMessageOrigin) {
        val rateLimit = getSenderRateLimit(sender)

        if (!checkSender(sender, origin)) return

        val isInCooldown = sender is ArcadePlayer && sender.coolDown("chat-$type", rateLimit)
        if (rateLimit > Duration.ZERO && !isInCooldown) {
            sender.audience.sendMessage(
                text {
                    it.append(
                        text(
                            "You can only chat once every ${rateLimit.toDouble(DurationUnit.SECONDS)} seconds! Ranked users bypass this restriction!",
                            NamedTextColor.RED
                        )
                    )
                }
            )

            return
        }
        val recipients = getRecipients(sender, message)

        val hoverComponent = (sender as? ArcadePlayer)?.computeHoverEventComponent(showActualValues)
        val senderId = sender.uuid

        val inputMessageResult = processChatMessage(sender, message)
        val fullFormattedMessage = formatMessage(
            sender,
            text(sender.getChatName(showActualValues, false)),
            inputMessageResult
        ).asComponent()

        launchAsync { AsyncChatChannelMessageSentEvent(sender, this@AbstractChatChannel, inputMessageResult).callEvent() }

        recipients.sendMessage(
            Identity.identity(senderId),
            fullFormattedMessage.hoverEvent(hoverComponent)
        )
    }

    open fun processChatMessage(sender: ArcadeSender, message: Component): Component {
        var modifiedMessage = message
        if (sender.hasAtLeastRank(HypixelPackageRank.SUPERSTAR, showActualValues)) {
            modifiedMessage = processEmotes(modifiedMessage)
        }
        return modifiedMessage
    }

    private fun processEmotes(message: Component): Component {
        var modifiedMessage = message
        ChatEmote.values().forEach { emote ->
            modifiedMessage = modifiedMessage.replaceText {
                it.matchLiteral(emote.emote)
                it.replacement(emote.replacement)
            }
        }
        return modifiedMessage
    }
}