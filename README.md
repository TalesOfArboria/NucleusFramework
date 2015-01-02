NucleusFramework for Spigot 1.8
==================

A plugin framework and rapid development library for Bukkit/Spigot. Currently under development, expect breaking changes.

_"Coders like versatility. Server operators like stability. Users like usability. I like balance."_

## Goals
 * Provide a centralized framework for common plugin utilities and services.
 * Reduce the amount of redundant code in plugins.
 * Speed up plugin development by abstracting common plugin features.
 * Maintain a balance between an API for versatility and a predictable foundation for stability.
 * Create a user command interface that has commonality that users can learn to recognize.
 * Reduce server operator work by centralizing features that are used across plugins while not forcing plugin designers to use the centralized features.

## Wiki
The [wiki](https://github.com/JCThePants/NucleusFramework/wiki) is under slow development due to the fact that NucleusFramework is not yet finished.

## Resources
 * [NucleusLocalizer](https://github.com/JCThePants/NucleusLocalizer) - A console program used to generate language localization resource files for Nucleus based plugins.
 * [Scripting examples](https://github.com/JCThePants/NucleusScriptExamples) - Examples of cross plugin scripts using NucleusFramework.
 * [Action Bar Test](https://github.com/JCThePants/NucleusActionBarTest) - A plugin for testing NucleusFramework's Action Bar classes.
 * [A-Star Test](https://github.com/JCThePants/NucleusAStarTest) - A plugin for testing NucleusFramework's A-Star pathing implementation.

## Plugin dependencies
 * [WorldEdit](https://github.com/sk89q/WorldEdit) - soft dependency, not required
 * [Vault](https://github.com/MilkBowl/Vault) - soft dependency, not required

## Plugins using NucleusFramework
 * [PV-Star](https://github.com/JCThePants/PV-Star) - Extensible arena framework.
 * [NucleusCitizens](https://github.com/JCThePants/NucleusCitizens) - Extends Nucleus functionality to include script API support for [Citizens2](https://github.com/CitizensDev/Citizens2) plugin.
 * [TPRegions](https://github.com/JCThePants/TPRegions) - Portal and region teleport.
 * [PhantomPackets](https://github.com/JCThePants/PhantomPackets) - Sets viewable regions and entities to specific players.

## Build dependencies
See the [gradle script](https://github.com/JCThePants/NucleusFramework/blob/master/build.gradle) for build dependencies.

## Maven repository
You can include the latest NucleusFramework snapshot as a Maven dependency with the following:

    <repositories>
        <repository>
            <id>jcthepants-repo</id>
            <url>https://github.com/JCThePants/mvn-repo/raw/master</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.jcwhatever.bukkit</groupId>
            <artifactId>NucleusFramework</artifactId>
            <version>0.0-SNAPSHOT</version>
        </dependency>
    </dependencies>


