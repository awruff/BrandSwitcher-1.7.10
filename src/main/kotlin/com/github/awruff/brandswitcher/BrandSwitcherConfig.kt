package com.github.awruff.brandswitcher

import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.config.v1.KtConfig
import org.polyfrost.oneconfig.internal.ui.components.settings.bumpResetEpoch

object BrandSwitcherConfig : KtConfig(
    "${BrandSwitcher.ID}.json",
    BrandSwitcher.NAME,
    Category.UTILITY,
    "/assets/brandswitcher/icon.svg",
) {
    private val warning by info(
        name = "You must rejoin for changes to take effect!",
        type = "warning",
    )

    var enabled: Boolean by switch(
        def = true,
        name = "Enabled"
    )

    var preset: Int by dropdown(
        options = Preset.entries.map(Preset::label).toTypedArray(),
        def = Preset.FABRIC.ordinal,
        name = "Preset",
    ).onChange {
        val preset = Preset.entries[it].brand
        if (preset != null && preset != brand) {
            brand = preset
            bumpResetEpoch(::brand.property)
        }
    }

    var brand: String by text(
        name = "Brand",
        def = "fabric",
    ).onChange {
        if (Preset.entries[preset].brand != it) {
            preset = Preset.CUSTOM.ordinal
            bumpResetEpoch(::preset.property)
        }
    }

    private val effective: String?
        get() = if (enabled) brand else null

    private var sent: String? = null

    init {
        hideIf(::warning) { Minecraft.getInstance()?.world == null || sent == effective }
    }

    @JvmStatic
    fun send(): String? {
        sent = effective
        ::warning.property.revaluateDisplay()
        return sent
    }
}

enum class Preset(val label: String, val brand: String?) {
    VANILLA("Vanilla", "vanilla"),
    FABRIC("Fabric", "fabric"),
    FORGE("Forge", "fml,forge"),
    // TODO: Make this auto-update to match Lunar
    LUNAR("Lunar Client", "lunarclient:v2.22.44-2640"),
    CUSTOM("Custom", null),
}
