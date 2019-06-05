package yamahari.weeds.config;

import net.minecraftforge.common.config.Config;
import yamahari.weeds.util.Reference;

@Config(modid= Reference.MOD_ID)
public class Configuration {
    @Config.Name("Spreading chance for weeds [%]")
    @Config.RangeInt(min = 0, max = 100)
    public static int weed_spread_chance = 16;

    @Config.Name("Pig digging chance [%]")
    @Config.RangeInt(min = 0, max = 100)
    public static int pig_digging_chance = 1;

    @Config.Name("Pig child digging chance [%]")
    @Config.RangeInt(min = 0, max = 100)
    public static int pig_child_digging_chance = 2;
}
