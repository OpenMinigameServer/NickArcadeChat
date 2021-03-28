package io.github.openminigameserver.nickarcade.chat.events.impl

import io.github.openminigameserver.nickarcade.chat.impl.AbstractChatChannel
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import net.kyori.adventure.text.Component
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class AsyncChatChannelMessageSentEvent(val sender: ArcadeSender, val channel: AbstractChatChannel, val message: Component) :
    Event(true) {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}