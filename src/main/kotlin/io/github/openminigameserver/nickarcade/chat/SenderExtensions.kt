package io.github.openminigameserver.nickarcade.chat

import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.plugin.extensions.getExtraDataValue
import io.github.openminigameserver.nickarcade.plugin.extensions.setExtraDataValue

var ArcadeSender.currentChannel: ChatChannelType
    get() = getExtraDataValue(ArcadeSender::currentChannel) ?: ChatChannelType.ALL
    set(value) {
        setExtraDataValue(ArcadeSender::currentChannel, value)
    }