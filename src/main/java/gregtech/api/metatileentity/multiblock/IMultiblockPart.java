package gregtech.api.metatileentity.multiblock;

public interface IMultiblockPart {

    /**
     * @return {@code true} if this part is registered with at least one multiblock controller,
     * {@code false} otherwise.
     */
    boolean isAttachedToMultiBlock();

    void addToMultiBlock(MultiblockControllerBase controllerBase);

    void removeFromMultiBlock(MultiblockControllerBase controllerBase);

    /**
     * @return {@code true} if this part can be used by multiple multiblock controllers simultaneously,
     * {@code false} otherwise.
     */
    default boolean canPartShare() {
        return false;
    }
}
