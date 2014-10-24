package com.jcwhatever.bukkit.generic.regions;

/**
 * Specifies how a region should build within itself.
 */
public enum BuildMethod {
    /**
     * Build method maximizes server performance
     * at the cost of build speed.
     */
	PERFORMANCE,

    /**
     * Build method is balanced to increase server performance
     * but not reduce the build speed as much.
     */
	BALANCED,

    /**
     * Build method will build as fast as possible while
     * possibly sacrificing server performance.
     */
	FAST
}
