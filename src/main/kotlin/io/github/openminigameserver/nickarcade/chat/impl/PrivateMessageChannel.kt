package io.github.openminigameserver.nickarcade.chat.impl

import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component

object PrivateMessageChannel : AbstractChatChannel(ChatChannelType.PRIVATE_MESSAGE) {
    override suspend fun getRecipients(sender: ArcadeSender, message: Component): Audience {
        return Audience.empty()
    }
}