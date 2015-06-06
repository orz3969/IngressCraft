package info.evshiron.ingresscraft.items;

import info.evshiron.ingresscraft.IngressCraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by evshiron on 5/25/15.
 */
public class XMPBursterItem extends Item {

    public static final String NAME = "xmpBurster";

    public XMPBursterItem() {

        super();

        setUnlocalizedName(NAME);
        setCreativeTab(CreativeTabs.tabInventory);
        setTextureName(IngressCraft.MODID + ":" + NAME);

    }

}
