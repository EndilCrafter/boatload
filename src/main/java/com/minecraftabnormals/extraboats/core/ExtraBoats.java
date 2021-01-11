package com.minecraftabnormals.extraboats.core;

import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.DataProcessors;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedData;
import com.minecraftabnormals.abnormals_core.common.world.storage.tracking.TrackedDataManager;
import com.minecraftabnormals.extraboats.common.dispenser.DispenseChestBoatBehavior;
import com.minecraftabnormals.extraboats.common.dispenser.DispenseFurnaceBoatBehavior;
import com.minecraftabnormals.extraboats.common.dispenser.DispenseLargeBoatBehavior;
import com.minecraftabnormals.extraboats.common.item.ChestBoatItem;
import com.minecraftabnormals.extraboats.common.item.EBBoatItem;
import com.minecraftabnormals.extraboats.common.item.FurnaceBoatItem;
import com.minecraftabnormals.extraboats.common.item.crafting.EBRecipes;
import com.minecraftabnormals.extraboats.core.registry.EBEntities;
import com.minecraftabnormals.extraboats.core.registry.EBItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExtraBoats.MOD_ID)
public class ExtraBoats {
	public static final String MOD_ID = "extraboats";

	public static final TrackedData<ItemStack> BANNER = TrackedData.Builder.create(DataProcessors.STACK, () -> ItemStack.EMPTY).build();
	
	public ExtraBoats() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::clientSetup);

		EBEntities.ENTITIES.register(modEventBus);
		EBItems.ITEMS.register(modEventBus);
		EBRecipes.RECIPE_SERIALIZERS.register(modEventBus);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			for (RegistryObject<Item> item : EBItems.ITEMS.getEntries()) {
				EBBoatItem boatitem = (EBBoatItem) item.get();
				if (boatitem instanceof ChestBoatItem) {
					DispenserBlock.registerDispenseBehavior(boatitem, new DispenseChestBoatBehavior(boatitem.getType()));
				} else if (boatitem instanceof FurnaceBoatItem) {
					DispenserBlock.registerDispenseBehavior(boatitem, new DispenseFurnaceBoatBehavior(boatitem.getType()));
				} else {
					DispenserBlock.registerDispenseBehavior(boatitem, new DispenseLargeBoatBehavior(boatitem.getType()));
				}
			}
			event.enqueueWork(() -> TrackedDataManager.INSTANCE.registerData(new ResourceLocation(MOD_ID, "banner"), BANNER));
		});
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		EBEntities.setupEntitiesClient();
	}
}