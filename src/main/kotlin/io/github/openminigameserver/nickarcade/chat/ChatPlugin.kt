package io.github.openminigameserver.nickarcade.chat

import io.github.openminigameserver.nickarcade.chat.impl.StaffChatChannel
import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.chat.model.ChatMessageOrigin
import io.github.openminigameserver.nickarcade.core.data.sender.misc.ArcadeWatcherSender
import io.github.openminigameserver.nickarcade.core.events.data.PlayerDataJoinEvent
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import io.github.openminigameserver.nickarcade.plugin.extensions.event
import io.github.openminigameserver.nickarcade.plugin.extensions.launchAsync
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.plugin.java.JavaPlugin

class ChatPlugin : JavaPlugin() {
    override fun onEnable() {
        registerJoinEvent()
        registerChatEvent()
        ChatCommands.init()
        prepareArcadeSender()
    }

    private fun prepareArcadeSender() {
        ArcadeWatcherSender.currentChannel = ChatChannelType.STAFF
        ArcadeWatcherSender.messageSender = {
            launchAsync {
                StaffChatChannel.sendMessageInternal(
                    ArcadeWatcherSender,
                    it,
                    ChatMessageOrigin.SHORTCUT_COMMAND
                )
            }
        }
    }

    private fun registerJoinEvent() {
        event<PlayerDataJoinEvent> {
            val currentChannel = player.currentChannel
            if (currentChannel.isInternal || !player.hasAtLeastRank(currentChannel.requiredRank, true)) {
                player.currentChannel = ChatChannelType.ALL
            }
        }
    }

    private fun registerChatEvent() {
        event<AsyncChatEvent>(forceBlocking = true) {
            isCancelled = true
            val playerData = this.player.getArcadeSender()
            val channel = ChatChannelsManager.getChannelByType(playerData.currentChannel)
            channel.sendMessageInternal(playerData, message(), ChatMessageOrigin.CHAT)
        }
    }
}