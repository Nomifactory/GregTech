package gregtech.api.metatileentity;

public interface ITiered {
    /**
     * Tier of machine determines it's input voltage, storage and generation rate
     *
     * @return tier of this machine
     */
    int getTier();
}
