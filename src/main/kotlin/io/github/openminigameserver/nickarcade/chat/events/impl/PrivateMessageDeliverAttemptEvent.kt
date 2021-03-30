package io.github.openminigameserver.nickarcade.chat.events.impl

import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import net.kyori.adventure.text.Component
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PrivateMessageDeliverAttemptEvent(
    val sender: ArcadeSender,
    var target: ArcadeSender,
    val message: Component,
    var result: PrivateMessageDeliverResult
) : Event(true) {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}