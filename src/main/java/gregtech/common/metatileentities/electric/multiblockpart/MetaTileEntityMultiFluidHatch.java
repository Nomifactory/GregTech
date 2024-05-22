package gregtech.common.metatileentities.electric.multiblockpart;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import gregtech.api.GTValues;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.NotifiableFluidTankFromList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.gui.widgets.TankWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.api.render.ICubeRenderer;
import gregtech.api.render.SimpleOverlayRenderer;
import gregtech.api.render.Textures;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class MetaTileEntityMultiFluidHatch extends MetaTileEntityMultiblockNotifiablePart implements IMultiblockAbilityPart<IFluidTank> {

	private static final int TANK_SIZE = 16_000;

	protected FluidTankList fluidTanks;

	public MetaTileEntityMultiFluidHatch(ResourceLocation metaTileEntityId, int tier, boolean isExportHatch) {
		super(metaTileEntityId, tier, isExportHatch);
		initializeInventory();
	}

	@Override
	protected void initializeInventory() {
		FluidTank[] fluidsHandlers = new FluidTank[tier * tier];
		for(int i = 0; i < fluidsHandlers.length; i++)
			fluidsHandlers[i] = new NotifiableFluidTankFromList(TANK_SIZE, this, isExportHatch, i) {
				@Override
				public Supplier<IMultipleTankHandler> getFluidTankList() {
					return () -> MetaTileEntityMultiFluidHatch.this.fluidTanks;
				}
			};
		this.fluidTanks = new FluidTankList(false, fluidsHandlers);
		this.fluidInventory = fluidTanks;
		super.initializeInventory();
	}

	@Override
	public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
		return new MetaTileEntityMultiFluidHatch(metaTileEntityId, getTier(), isExportHatch);
	}

	@Override
	public void update() {
		super.update();
		if (!getWorld().isRemote) {
			if (isExportHatch) {
				pushFluidsIntoNearbyHandlers(getFrontFacing());
			} else {
				pullFluidsFromNearbyHandlers(getFrontFacing());
			}
		}
	}

	@Override
	public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
		super.renderMetaTileEntity(renderState, translation, pipeline);
		if (shouldRenderOverlay()) {
			SimpleOverlayRenderer renderer = getTier() == 2 ? Textures.PIPE_4X_OVERLAY : Textures.PIPE_9X_OVERLAY;
			renderer.renderSided(getFrontFacing(), renderState, translation, pipeline);
			SimpleOverlayRenderer overlay = isExportHatch ? Textures.FLUID_HATCH_OUTPUT_OVERLAY : Textures.FLUID_HATCH_INPUT_OVERLAY;
			overlay.renderSided(getFrontFacing(), renderState, translation, pipeline);
		}
	}

	@Override
	public ICubeRenderer getBaseTexture() {
		MultiblockControllerBase controller = getController();
		int tier = getTier() == 2 ? GTValues.HV : GTValues.IV;
		return controller == null ? Textures.VOLTAGE_CASINGS[tier] : controller.getBaseTexture(this);
	}

	@Override
	protected FluidTankList createImportFluidHandler() {
		return isExportHatch ? new FluidTankList(false) : fluidTanks;
	}

	@Override
	protected FluidTankList createExportFluidHandler() {
		return !isExportHatch ? new FluidTankList(false) : fluidTanks;
	}

	@Override
	public MultiblockAbility<IFluidTank> getAbility() {
		return isExportHatch ? MultiblockAbility.EXPORT_FLUIDS : MultiblockAbility.IMPORT_FLUIDS;
	}

	@Override
	public void registerAbilities(List<IFluidTank> abilityList) {
		abilityList.addAll(isExportHatch ? this.exportFluids.getFluidTanks() : this.importFluids.getFluidTanks());
	}

	@Override
	protected ModularUI createUI(EntityPlayer entityPlayer) {
		ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 176, 18 + 18 * getTier() + 94);
		builder.widget(new LabelWidget(10, 5, getMetaFullName()));
		final int rowSize = getTier();
		for(int y = 0; y < rowSize; y++) {
			for(int x = 0; x < rowSize; x++) {
				int slotIndex = y * rowSize + x;
				int xpos = 89 - rowSize * 9 + x * 18;
				int ypos = 18 + y * 18;
				builder.widget(new TankWidget(fluidTanks.getTankAt(slotIndex), xpos, ypos, 18, 18)
					               .setAlwaysShowFull(true)
					               .setBackgroundTexture(GuiTextures.FLUID_SLOT)
					               .setContainerClicking(true, !isExportHatch));
			}
		}
		builder.bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 7, 18 + 18 * rowSize + 12);
		return builder.build(getHolder(), entityPlayer);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format(isExportHatch
			                        ? "gregtech.machine.fluid_hatch.export.tooltip"
			                        : "gregtech.machine.fluid_hatch.import.tooltip"));
		tooltip.add(I18n.format("gregtech.machine.fluid_multi_hatch.capacity", 16000, getTier() * getTier()));
	}
}
