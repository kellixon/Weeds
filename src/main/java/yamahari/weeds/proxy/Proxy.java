package yamahari.weeds.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface Proxy {
    public void registerItemRenderer(Item item, int meta, String id);
}
