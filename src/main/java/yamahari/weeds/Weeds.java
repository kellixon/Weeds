package yamahari.weeds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemSlab;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import yamahari.weeds.blocks.*;
import yamahari.weeds.lists.BlockList;
import yamahari.weeds.lists.ItemList;
import yamahari.weeds.proxy.Proxy;
import yamahari.weeds.util.Reference;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS, version = Reference.VERSION)
public class Weeds {

    @Mod.Instance
    public static Weeds instance;

    public static Logger logger;

    @SidedProxy(serverSide = Reference.COMMON_PROXY_CLASS, clientSide = Reference.CLIENT_PROXY_CLASS)
    public static Proxy proxy;

    @Mod.EventHandler
    public static void onPreInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public static void onInit(FMLInitializationEvent event) {
        LootTableList.register(new ResourceLocation(Reference.MOD_ID, "pig_digging"));
    }

    @Mod.EventHandler
    public static void onPostInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventBusSubscriber
    public static class ModEventHandler {
        @SubscribeEvent
        public static void onRegisterItems(RegistryEvent.Register<Item> event) {
            logger.info(Reference.MOD_ID + ": onRegisterItems called");
            event.getRegistry().registerAll(
                new ItemBlock(BlockList.dry_farmland).setRegistryName(BlockList.dry_farmland.getRegistryName()),
                new ItemSeeds(BlockList.weeds, Blocks.FARMLAND).setRegistryName("weed").setUnlocalizedName(makeUnlocalizedName("weed")),
                new ItemBlock(BlockList.weed_bale).setRegistryName(BlockList.weed_bale.getRegistryName()),
                new ItemBlock(BlockList.dried_weed_bale).setRegistryName(BlockList.dried_weed_bale.getRegistryName()),
                new ItemBlock(BlockList.dried_weed_bale_stairs).setRegistryName(BlockList.dried_weed_bale_stairs.getRegistryName()),
                new ItemSlab(BlockList.dried_weed_bale_slab, ((BlockSlab)BlockList.dried_weed_bale_slab), ((BlockSlab)BlockList.dried_weed_bale_double_slab)).setRegistryName(BlockList.dried_weed_bale_slab.getRegistryName())
            );
        }

        @SubscribeEvent
        public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
            logger.info(Reference.MOD_ID + ": onRegisterBlocks called");
            Block block = new BlockDriedWeedBale().setRegistryName("dried_weed_bale").setUnlocalizedName(makeUnlocalizedName("dried_weed_bale"));
            event.getRegistry().registerAll(
                new BlockDryFarmland().setRegistryName("dry_farmland").setUnlocalizedName(makeUnlocalizedName("dry_farmland")),
                new BlockWeeds().setRegistryName("weeds").setUnlocalizedName(makeUnlocalizedName("weeds")),
                new BlockWeedBale().setRegistryName("weed_bale").setUnlocalizedName(makeUnlocalizedName("weed_bale")),
                block,
                new BlockDriedWeedBaleStairs(block.getDefaultState()).setRegistryName("dried_weed_bale_stairs").setUnlocalizedName(makeUnlocalizedName("dried_weed_bale_stairs")),
                (new BlockDriedWeedBaleSlab.Half()).setRegistryName("dried_weed_bale_slab").setUnlocalizedName(makeUnlocalizedName("dried_weed_bale_slab")),
                (new BlockDriedWeedBaleSlab.Double()).setRegistryName("dried_weed_bale_double_slab").setUnlocalizedName(makeUnlocalizedName("dried_weed_bale_double_slab"))
            );
        }

        @SubscribeEvent
        public static void onRegisterModels(ModelRegistryEvent event) {
            logger.info(Reference.MOD_ID + ": onRegisterModels called");
            Weeds.proxy.registerItemRenderer(ItemList.dry_farmland, 0, "inventory");
            Weeds.proxy.registerItemRenderer(ItemList.weed, 0, "inventory");
            Weeds.proxy.registerItemRenderer(Item.getItemFromBlock(BlockList.dry_farmland), 0, "inventory");
            Weeds.proxy.registerItemRenderer(Item.getItemFromBlock(BlockList.weed_bale), 0, "inventory");
            Weeds.proxy.registerItemRenderer(Item.getItemFromBlock(BlockList.dried_weed_bale), 0, "inventory");
            Weeds.proxy.registerItemRenderer(Item.getItemFromBlock(BlockList.dried_weed_bale_stairs), 0, "inventory");
            Weeds.proxy.registerItemRenderer(Item.getItemFromBlock(BlockList.dried_weed_bale_slab), 0, "inventory");
        }

        private static String makeUnlocalizedName(String name) {
            return Reference.MOD_ID + "." + name;
        }
    }
}
