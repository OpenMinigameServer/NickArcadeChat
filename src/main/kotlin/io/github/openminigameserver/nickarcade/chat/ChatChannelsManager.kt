package io.github.openminigameserver.nickarcade.chat

import io.github.openminigameserver.nickarcade.chat.impl.AbstractChatChannel
import io.github.openminigameserver.nickarcade.chat.impl.AllChatChannel
import io.github.openminigameserver.nickarcade.chat.impl.StaffChatChannel
import io.github.openminigameserver.nickarcade.chat.impl.UserInputChannel
import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType

object ChatChannelsManager {
    val channels = mutableMapOf<ChatChannelType, AbstractChatChannel>()
    private val allChatChannel = AllChatChannel()

    init {
        registerChannel(allChatChannel)
        registerChannel(StaffChatChannel)
        registerChannel(UserInputChannel)
    }

    fun registerChannel(channel: AbstractChatChannel) {
        channels[channel.type] = channel
    }

    fun getChannelByType(id: ChatChannelType): AbstractChatChannel {
        return channels[id] ?: allChatChannel
    }
}