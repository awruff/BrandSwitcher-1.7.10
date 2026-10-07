package com.github.awruff.brandswitcher

import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer
import org.polyfrost.oneconfig.api.commands.v1.CommandManager
import org.polyfrost.oneconfig.utils.v1.dsl.addDefaultCommand

class BrandSwitcher : ClientModInitializer {

    override fun initClient() {
        CommandManager.register(BrandSwitcherConfig.addDefaultCommand(ID))
    }

    companion object {
        const val ID = "brandswitcher"
        const val NAME = "Brand Switcher"
    }
}
