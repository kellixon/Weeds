package yamahari.weeds.config;

import net.minecraftforge.common.config.Config;
import yamahari.weeds.util.Reference;

@Config(modid= Reference.MOD_ID)
public class Configuration {
    @Config.Name("Transforming chance for weeds [%]")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static double transform_to_weed_chance = 0.08;

    @Config.Name("Spreading chance for weeds [%]")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static double spread_weed_chance = 0.05;

    @Config.Name("Pig digging chance [%]")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static double pig_digging_chance = 0.001;

    @Config.Name("Pig (child) digging chance [%]")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static double pig_child_digging_chance = 0.02;

    @Config.Name("Pig (child) digging dropchance [%]")
    @Config.RangeDouble(min=0.0, max = 1.0)
    public static double pig_child_digging_drop_chance = 0.05;

    @Config.Name("Pig digging dropchance [%]")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static double pig_digging_drop_chance = 0.025;

    @Config.Name("Weed Bale drying chace [%]")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    public static double weed_bale_drying_chance = 0.1;
}
