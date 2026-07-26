package com.accbdd.reclamation_util;

import com.accbdd.reclamation_util.capability.GlobeCountCapability;
import com.accbdd.reclamation_util.datagen.DataGenerators;
import com.accbdd.reclamation_util.event.AreaBreakItemUsage;
import com.accbdd.reclamation_util.item.BiomeGlobeItem;
import com.accbdd.reclamation_util.item.CamelPackItem;
import com.accbdd.reclamation_util.compat.ICauldronTankHolder;
import com.accbdd.reclamation_util.naturesaura.ReclaimEffect;
import com.accbdd.reclamation_util.network.ReclamationPacketHandler;
import com.accbdd.reclamation_util.particle.ColoredDripParticle;
import com.accbdd.reclamation_util.particle.ColoredLeafParticle;
import com.accbdd.reclamation_util.plugin.ReclamationPlantModifiers;
import com.accbdd.reclamation_util.register.BeeRegistration;
import com.accbdd.reclamation_util.register.Blocks;
import com.accbdd.reclamation_util.register.Items;
import com.accbdd.reclamation_util.register.Particles;
import com.mojang.logging.LogUtils;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(ReclamationUtil.MODID)
public class ReclamationUtil {
    public static final String MODID = "reclamation_util";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ReclamationUtil(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(AreaBreakItemUsage::onAreaBreakItemUsage);

        modEventBus.addListener(ReclamationUtil::onCommonSetup);
        modEventBus.addListener(DataGenerators::generate);
        modEventBus.addListener(ReclamationUtil::afterLoadComplete);

        Particles.REGISTER.register(modEventBus);
        Items.REGISTER.register(modEventBus);
        Blocks.REGISTER.register(modEventBus);
        Items.CREATIVE_MODE_TAB.register(modEventBus);
        BeeRegistration.EFFECTS.register(modEventBus);
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ReclamationPlantModifiers.register();
        NaturesAuraAPI.DRAIN_SPOT_EFFECTS.put(ReclaimEffect.NAME, ReclaimEffect::new);
        NaturesAuraAPI.EFFECT_POWDERS.put(ReclaimEffect.NAME, 0xFFCC00);
        ReclamationPacketHandler.register();
    }

    public static void afterLoadComplete(FMLLoadCompleteEvent event) {
        NaturesAuraAPI.PROJECTILE_GENERATIONS.put(EntityType.SNOWBALL, 0);
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().revive();
        event.getOriginal().getCapability(GlobeCountCapability.GLOBE_COUNT).ifPresent(oldCap -> {
            event.getEntity().getCapability(GlobeCountCapability.GLOBE_COUNT).ifPresent(newCap -> {
                newCap.getData().count = oldCap.getData().count;
            });
        });
        event.getOriginal().setRemoved(event.isWasDeath() ? Entity.RemovalReason.KILLED : Entity.RemovalReason.CHANGED_DIMENSION);
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(GlobeCountCapability.GLOBE_COUNT).ifPresent(cap -> {
                ReclamationPacketHandler.sendToPlayer(cap.getData().count, player);
            });
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        BiomeGlobeItem.serverTick();
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(GlobeCountCapability.GLOBE_COUNT).ifPresent(cap -> {
                ReclamationPacketHandler.sendToPlayer(cap.getData().count, player);
            });
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(GlobeCountCapability.GLOBE_COUNT).ifPresent(cap -> {
                ReclamationPacketHandler.sendToPlayer(cap.getData().count, player);
            });
        }
    }

    @SubscribeEvent
    public void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(GlobeCountCapability.class);
    }

    @SubscribeEvent
    public void entityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!event.getObject().getCapability(GlobeCountCapability.GLOBE_COUNT).isPresent()) {
                event.addCapability(GlobeCountCapability.ID, new GlobeCountCapability());
            }
        }
    }

    @SubscribeEvent
    public void itemStackCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof CamelPackItem camelPack && !camelPack.infinite) {
            event.addCapability(ResourceLocation.tryParse("reclamation_util:camel_fluid"), new FluidHandlerItemStack(event.getObject(), camelPack.capacity));
        }
    }

    @SubscribeEvent
    public void blockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof ICauldronTankHolder cauldron) {
            event.addCapability(ResourceLocation.tryParse("reclamation_util:cauldron_fluid"), new ICapabilityProvider() {
                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == ForgeCapabilities.FLUID_HANDLER) {
                        return cauldron.reclamation_util$getFluidHandler().cast();
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSprite(Particles.COLORED_DRIP_HANG.get(),
                    new ColoredDripParticle.Provider());
            event.registerSprite(Particles.TWO_COLORED_DRIP_HANG.get(),
                    new ColoredDripParticle.TwoProvider());
            event.registerSprite(Particles.COLORED_DRIP_FALL.get(),
                    new ColoredDripParticle.Provider());
            event.registerSprite(Particles.COLORED_DRIP_LAND.get(),
                    new ColoredDripParticle.Provider());
            event.registerSpriteSet(Particles.COLORED_LEAF_TYPE.get(), set -> (options, pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed) ->
                    new ColoredLeafParticle(pLevel, pX, pY, pZ, set, options));
        }

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemPropertyFunction fluidProperty = (stack, level, entity, seed) ->
                        stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                                .map(h -> (float) h.getFluidInTank(0).getAmount()
                                        / ((CamelPackItem) stack.getItem()).capacity)
                                .orElse(0f);
                ItemProperties.register(
                        Items.CAMEL_PACK_BASIC.get(),
                        ResourceLocation.fromNamespaceAndPath(MODID, "fill_level"),
                        fluidProperty
                );

                ItemProperties.register(
                        Items.CAMEL_PACK_ADVANCED.get(),
                        ResourceLocation.fromNamespaceAndPath(MODID, "fill_level"),
                        fluidProperty
                );
            });
        }

    }
}
