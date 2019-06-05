package yamahari.weeds.proxy;

import net.minecraft.item.Item;

public interface Proxy {
    public void registerItemRenderer(Item item, int meta, String id);
}
