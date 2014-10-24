package com.jcwhatever.bukkit.generic.sounds;

import com.jcwhatever.bukkit.generic.storage.IDataNode;

/**
 * A resource sound for a music disc.
 */
public class MusicDiskSound extends ResourceSound {

    private int _diskId;

    /**
     * Constructor.
     *
     * @param dataNode  The resource sound data node.
     */
    MusicDiskSound(IDataNode dataNode) {
        super(dataNode);
    }

    /**
     * Get the disk id.
     */
    public final int getDiskId() {
        return _diskId;
    }

    /*
     * Get the name of the disk from the id.
     */
    @Override
    protected String loadName(IDataNode dataNode) {

        _diskId = dataNode.getInteger("disk-id");

        switch (_diskId) {
            case 2256:
                return "records.13";

            case 2257:
                return "records.cat";

            case 2258:
                return "records.blocks";

            case 2259:
                return "records.chirp";

            case 2260:
                return "records.far";

            case 2261:
                return "records.mall";

            case 2262:
                return "records.mellohi";

            case 2263:
                return "records.stal";

            case 2264:
                return "records.strad";

            case 2265:
                return "records.ward";

            case 2266:
                return "records.11";

            case 2267:
                return "records.wait";

            default:
                throw new RuntimeException("Invalid record ID detected in resource sounds.");
        }
    }
}
