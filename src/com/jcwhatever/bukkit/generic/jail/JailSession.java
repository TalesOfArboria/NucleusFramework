package com.jcwhatever.bukkit.generic.jail;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a players session in a jail.
 */
public class JailSession {

    private final JailManager _jailManager;
    private final UUID _playerId;
    private Date _expires;

    private boolean _isReleased;

    /**
     * Constructor.
     *
     * @param jailManager      The owning jail manager.
     * @param playerId         The id of the player.
     * @param expires          The time the jail session will expire.
     */
    JailSession(JailManager jailManager, UUID playerId, Date expires) {
        PreCon.notNull(jailManager);
        PreCon.notNull(playerId);
        PreCon.notNull(expires);

        _jailManager = jailManager;
        _playerId = playerId;
        _expires = expires;
    }

    /**
     * Get the owning jail manager.
     */
    public JailManager getJailManager() {
        return _jailManager;
    }

    /**
     * Get the id of the player.
     */
    public UUID getPlayerId() {
        return _playerId;
    }

    /**
     * Get the session expiration date.
     */
    public Date getExpiration() {
        return _expires;
    }

    /**
     * Determine if the session is expired.
     * @return
     */
    public boolean isExpired() {
        return _expires.compareTo(new Date()) <= 0;
    }

    /**
     * Changes the players release flag to True.
     *
     * @param forceRelease  True causes the warden to run.
     */
    public void release(boolean forceRelease) {
        _isReleased = true;
        
        if (forceRelease) {
            _expires = new Date();
            _jailManager._warden.run(true);
        }
    }

    /**
     * Get the location the player is released at.
     */
    public Location getReleaseLocation() {
        return _jailManager.getReleaseLocation();
    }

    // remove the session from the data node.
    void expire(IDataNode dataNode) {
        if (dataNode != null) {
            dataNode.remove();
            dataNode.saveAsync(null);
        }
        
        _expires = new Date();
    }

    public boolean isReleased () {
        return _isReleased;
    }

}
