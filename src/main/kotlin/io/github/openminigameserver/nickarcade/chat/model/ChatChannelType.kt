package io.github.openminigameserver.nickarcade.chat.model

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import net.kyori.adventure.text.format.NamedTextColor

enum class ChatChannelType(
    val prefix: PrefixData? = null,
    val requiredRank: HypixelPackageRank = HypixelPackageRank.NONE,
    val useActualName: Boolean = true,
    val isInternal: Boolean = false
) {
    ALL(useActualName = false),
    PARTY(PrefixData(NamedTextColor.BLUE)),
    STAFF(PrefixData(NamedTextColor.AQUA), HypixelPackageRank.HELPER),
    USER_INPUT(isInternal = true)
}