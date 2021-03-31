package io.github.openminigameserver.nickarcade.chat.components

import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor

class PrivateMessageComponent(val action: String, val user: ArcadeSender, val message: Component, val rawMessage: Component) :
    ComponentLike {
    override fun asComponent(): Component {
        return Component.text {
            it.append(Component.text("$action ", NamedTextColor.LIGHT_PURPLE))
            it.append(Component.text(user.getChatName(actualData = true, colourPrefixOnly = false)))
            it.append(Component.text(": ", NamedTextColor.GRAY))
            it.append(message)
        }
    }
}