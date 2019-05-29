package yamahari.weeds;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yamahari.weeds.blocks.BlockDryFarmland;
import yamahari.weeds.blocks.BlockWeeds;
import yamahari.weeds.lists.BlockList;
import yamahari.weeds.lists.ItemList;

import java.util.Random;

@Mod("weeds")
public class Weeds {
    public static final String modId = "weeds";
    public static Weeds instance;
    public static final Logger logger = LogManager.getLogger(modId);

    public Weeds() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        instance = this;

        MinecraftForge.EVENT_BUS.register(instance);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        logger.info("commonSetup");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        logger.info("clientSetup");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            logger.info("registerItems");
            event.getRegistry().registerAll(
                ItemList.weed = new ItemSeeds(BlockList.weeds, new Item.Properties().group(ItemGroup.MATERIALS)).setRegistryName(makeLocation("weed")),
                ItemList.dry_farmland = new ItemBlock(BlockList.dry_farmland, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(BlockList.dry_farmland.getRegistryName())
            );
        }

        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            logger.info("registerBlocks");
            event.getRegistry().registerAll(
                BlockList.weeds = new BlockWeeds(Block.Properties.create(Material.PLANTS).sound(SoundType.PLANT).needsRandomTick().doesNotBlockMovement().hardnessAndResistance(0.0f)).setRegistryName(makeLocation("weeds")),
                BlockList.dry_farmland = new BlockDryFarmland(Block.Properties.create(Material.GROUND).needsRandomTick().hardnessAndResistance(0.6F).sound(SoundType.GROUND)).setRegistryName(makeLocation("dry_farmland"))
            );
        }

        private static ResourceLocation makeLocation(String name) {
            return new ResourceLocation(modId, name);
        }
    }
}
