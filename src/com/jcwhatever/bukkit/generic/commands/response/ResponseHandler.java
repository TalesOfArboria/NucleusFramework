package com.jcwhatever.bukkit.generic.commands.response;

/**
 * Interface for a response request handler
 */
public interface ResponseHandler {

    /**
     * Executed when the player responds.
     *
     * @param response  The response.
     */
    void onResponse(ResponseType response);

}
