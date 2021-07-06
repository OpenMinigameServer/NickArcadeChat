package io.github.openminigameserver.nickarcade.chat.impl

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.chat.model.ChatChannelType
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import kotlin.time.Duration

class AllChatChannel : AbstractChatChannel(ChatChannelType.ALL) {

    override suspend fun getPlayerRateLimit(sender: ArcadePlayer): Duration {
        return if (sender.hasAtLeastRank(HypixelPackageRank.VIP)) Duration.ZERO else Duration.seconds(3)
    }

    override suspend fun getRecipients(sender: ArcadeSender, message: Component): Audience {
        return Audience.audience(Bukkit.getOnlinePlayers())
    }
}