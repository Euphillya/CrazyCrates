plugins {
    id("crazycrates-api")
}

project.version = "${extra["plugin_version"]}"
project.group = "${rootProject.group}.CrazyCrates"
project.description = "Add unlimited crates to your server with 10 different crate types to choose from!"

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    /**
     * NBT Team
     */
    maven("https://repo.codemc.org/repository/maven-public/")

    /**
     * Paper Team
     */
    maven("https://repo.papermc.io/repository/maven-public/")

    /**
     * Triumph Team
     */
    maven("https://repo.triumphteam.dev/snapshots/")

    /**
     * CrazyCrew Team
     */
    maven("https://repo.crazycrew.us/plugins/")

    /**
     * Minecraft Team
     */
    maven("https://libraries.minecraft.net/")

    /**
     * Vault Team
     */
    maven("https://jitpack.io/")

    /**
     * Everything else we need.
     */
    mavenCentral()
}

dependencies {
    implementation("dev.triumphteam", "triumph-cmd-bukkit", "2.0.0-SNAPSHOT")

    implementation("de.tr7zw", "nbt-data-api", "2.11.1")

    implementation("org.bstats", "bstats-bukkit", "3.0.0")

    compileOnly("io.papermc.paper", "paper-api", "${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")

    compileOnly("me.filoghost.holographicdisplays", "holographicdisplays-api", "3.0.0")

    compileOnly("com.github.decentsoftware-eu", "decentholograms", "2.7.7")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    compileOnly("com.Zrips.CMI", "CMI-API", "9.2.6.1")
    compileOnly("CMILib", "CMILib", "1.2.3.7")

    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }
}