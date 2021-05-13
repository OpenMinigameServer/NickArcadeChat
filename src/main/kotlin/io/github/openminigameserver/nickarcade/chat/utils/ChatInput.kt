package io.github.openminigameserver.nickarcade.chat.utils

import io.github.openminigameserver.nickarcade.chat.currentChannel
import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.RuntimeExtraDataTag
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.entity.Player
import org.geysermc.cumulus.CustomForm
import org.geysermc.floodgate.api.FloodgateApi

val inputWaitingTag = RuntimeExtraDataTag.of<InputWaiting>("input_waiting")

data class InputWaiting(
    val oldChannel: ChatChannelType,
    val onSuccess: Player.(String) -> Unit,
    val isValid: Player.(String) -> Boolean
)

object ChatInput {

    suspend fun requestInput(
        player: Player,
        onSuccess: Player.(String) -> Unit,
        isValid: Player.(String) -> Boolean = { true },
        formsContent: String? = null
    ) {
        coroutineScope {
            val coroutineScope = this

            val playerData = player.getArcadeSender()

            if (playerData.isFloodgatePlayer) {
                FloodgateApi.getInstance().sendForm(playerData.uuid, CustomForm.builder().apply {
                    title("Input Required")
                    if (formsContent != null) {
                        label(formsContent)
                    }

                    input("Input")

                    this.responseHandler { form, responseStr ->
                        val result = form.parseResponse(responseStr)
                        if (result.isCorrect) {
                            result.getInput(0)?.let {
                                if (!performInput(playerData, it)) {
                                    coroutineScope.launch {
                                        requestInput(player, onSuccess, isValid, formsContent)
                                    }
                                }
                            }
                        }
                    }
                })

            }
            playerData[inputWaitingTag] = InputWaiting(playerData.currentChannel, onSuccess, isValid)
            if (!playerData.isFloodgatePlayer) {
                playerData.currentChannel = ChatChannelType.USER_INPUT
            }
        }
    }

    fun performInput(sender: ArcadePlayer, content: String): Boolean {
        val input = sender[inputWaitingTag] ?: return false
        val player = sender.player ?: return false
        if (!input.isValid(player, content)) {
            sender.audience.sendMessage(
                Component.text(
                    "The input you gave '${content}' is not valid. Please enter a valid input.",
                    RED
                )
            )

            return false
        }

        sender.currentChannel = input.oldChannel
        input.onSuccess(player, content)

        return true
    }

}