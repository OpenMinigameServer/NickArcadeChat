package io.github.openminigameserver.nickarcade.chat.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.specifier.Greedy
import io.github.openminigameserver.nickarcade.chat.utils.PrivateMessageUtils
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import net.kyori.adventure.text.Component.text

object MessageCommands {

    @CommandMethod("w|msg|message|tell <target> <message>")
    fun messageCommand(
        sender: ArcadeSender,
        @Argument("target") target: ArcadePlayer,
        @Argument("message") @Greedy message: String
    ) {
        PrivateMessageUtils.sendPrivateMessage(sender, target, text(message))
    }

    @CommandMethod("r <message>")
    fun messageCommand(
        sender: ArcadeSender,
        @Argument("message") @Greedy message: String
    ) {
        PrivateMessageUtils.replyPrivateMessage(sender, text(message))
    }
}