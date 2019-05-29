package yamahari.weeds;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
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

import java.util.Random;

@Mod("agriculturecraft")
public class Weeds {
    public static final String modId = "agriculturecraft";
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

            );
        }

        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event) {
            logger.info("registerBlocks");
            event.getRegistry().registerAll(

            );
        }

        private static ResourceLocation makeLocation(String name) {
            return new ResourceLocation(modId, name);
        }
    }
}

