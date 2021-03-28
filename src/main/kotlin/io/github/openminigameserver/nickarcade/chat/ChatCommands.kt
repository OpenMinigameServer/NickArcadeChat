package io.github.openminigameserver.nickarcade.chat

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.kotlin.extension.buildAndRegister
import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.chat.model.ChatEmote
import io.github.openminigameserver.nickarcade.chat.model.ChatMessageOrigin
import io.github.openminigameserver.nickarcade.core.commandAnnotationParser
import io.github.openminigameserver.nickarcade.core.commandHelper
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.manager.PlayerDataManager
import io.github.openminigameserver.nickarcade.plugin.extensions.command
import io.github.openminigameserver.nickarcade.plugin.helper.commands.NickArcadeCommandHelper
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*

object ChatCommands {

    fun init() {
        commandAnnotationParser.parse(this)
        registerChatChannelCommands(commandHelper)
    }

    private fun registerChatChannelCommands(commandHelper: NickArcadeCommandHelper) {
        ChatChannelsManager.channels.filterNot { it.key.isInternal }.forEach { (type, channel) ->
            val smallLetter = type.name.first().toLowerCase()
            commandHelper.manager.buildAndRegister("${smallLetter}chat", aliases = arrayOf("${smallLetter}c")) {
                argument {
                    StringArgument.greedy("text")
                }
                handler {
                    command(it.sender, type.requiredRank) {
                        channel.sendMessageInternal(
                            it.sender,
                            text(it.get<String>("text")),
                            ChatMessageOrigin.SHORTCUT_COMMAND
                        )
                    }
                }

            }
        }
    }

    @CommandMethod("emotes")
    fun emotesCommand(sender: ArcadeSender) = command(sender) {
        val asAudience = sender.audience
        asAudience.sendMessage(text {
            it.append(text("Available to ", GREEN))
            it.append(text("MVP", GOLD))
            it.append(text("++", RED))
            it.append(text(":", GREEN))
        })

        ChatEmote.values().forEach { emote ->
            asAudience.sendMessage(text {
                it.append(text(emote.emote, GOLD))
                it.append(text(" - ", WHITE))
                it.append(emote.replacement)
            })
        }
    }

    @CommandMethod("chat <channel>")
    fun channelSwitchCommand(sender: ArcadeSender, @Argument("channel") channel: ChatChannelType) =
        command(sender, channel.requiredRank) {
            if (channel.isInternal) {
                sender.audience.sendMessage(
                    text(
                        "This is an internal chat channel and thus cannot be switched to.",
                        RED
                    )
                )
                return@command
            }
            sender.currentChannel = channel
            sender.audience.sendMessage(text("Changed current channel to $channel.", GREEN))
            PlayerDataManager.savePlayerData(sender)
        }
}