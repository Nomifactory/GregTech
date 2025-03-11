package gregtech.integration.theoneprobe;

import gregtech.integration.nomilabs.element.*;
import gregtech.integration.theoneprobe.provider.*;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;

public class TheOneProbeCompatibility {

    public static int FLUID_NAME_ELEMENT;
    public static int FLUID_STACK_ELEMENT;
    public static int CUSTOM_NAME_ELEMENT;
    public static int CHANCED_ITEM_STACK_ELEMENT;

    public static void registerCompatibility() {
        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        FLUID_NAME_ELEMENT = oneProbe.registerElementFactory(LabsFluidNameElement::new);
        FLUID_STACK_ELEMENT = oneProbe.registerElementFactory(LabsFluidStackElement::new);
        CUSTOM_NAME_ELEMENT = oneProbe.registerElementFactory(CustomNameElement::new);
        CHANCED_ITEM_STACK_ELEMENT = oneProbe.registerElementFactory(LabsChancedItemStackElement::new);

        oneProbe.registerProvider(new ElectricContainerInfoProvider());
        oneProbe.registerProvider(new FuelableInfoProvider());
        oneProbe.registerProvider(new RecipeLogicInfoProvider());
        oneProbe.registerProvider(new WorkableInfoProvider());
        oneProbe.registerProvider(new ControllableInfoProvider());
        oneProbe.registerProvider(new DebugPipeNetInfoProvider());
        oneProbe.registerProvider(new TransformerInfoProvider());
        oneProbe.registerProvider(new MuffleableInfoProvider());
        oneProbe.registerProvider(new RecipeOutputsProvider());
    }

}
