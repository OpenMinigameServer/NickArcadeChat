package io.github.openminigameserver.nickarcade.chat.impl

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.filterSuspend
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

object StaffChatChannel : AbstractChatChannel(ChatChannelType.STAFF) {
    override val showActualValues: Boolean
        get() = true

    override suspend fun getRecipients(sender: ArcadeSender, message: Component): Audience {
        return Audience.audience(Bukkit.getOnlinePlayers()).filterSuspend {
            it.getArcadeSender().hasAtLeastRank(HypixelPackageRank.HELPER, true)
        }
    }
}