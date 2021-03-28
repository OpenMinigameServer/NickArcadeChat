package io.github.openminigameserver.nickarcade.chat.impl

import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.chat.utils.ChatInput
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

object UserInputChannel : AbstractChatChannel(ChatChannelType.USER_INPUT) {
    override suspend fun getRecipients(sender: ArcadeSender, message: Component): Audience {
        return Audience.empty()
    }

    override fun processChatMessage(sender: ArcadeSender, message: Component): Component {
        if (sender !is ArcadePlayer) return super.processChatMessage(sender, message)
        ChatInput.performInput(sender, (message as TextComponent).content())
        return super.processChatMessage(sender, message)
    }
}
