package com.jcwhatever.bukkit.generic.sounds;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.sun.istack.internal.Nullable;

public class VoiceSound extends ResourceSound {

    private final Transcript _transcript;

    VoiceSound(IDataNode dataNode) {
        super(dataNode);

        String transcript = dataNode.getString("transcript", "");

        _transcript = new Transcript(transcript);
    }

    @Nullable
    public final Transcript getTranscript() {
        return _transcript;
    }
}
