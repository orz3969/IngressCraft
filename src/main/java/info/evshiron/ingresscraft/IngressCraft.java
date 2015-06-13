package info.evshiron.ingresscraft;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import info.evshiron.ingresscraft.blocks.XMBlock;
import info.evshiron.ingresscraft.entities.PortalEntity;
import info.evshiron.ingresscraft.entities.ResonatorEntity;
import info.evshiron.ingresscraft.items.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import info.evshiron.ingresscraft.messages.SyncPortalMessage;
import info.evshiron.ingresscraft.messages.SyncScannerMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = IngressCraft.MODID, version = IngressCraft.VERSION)
public class IngressCraft
{

    public static final String MODID = "ingresscraft";
    public static final String VERSION = "0.0.1";

    @Mod.Instance(MODID)
    public static IngressCraft Instance;

    public static double CONFIG_RANGE_FACTOR;
    public static double CONFIG_PORTAL_RANGE;

    public static final CreativeTabs CreativeTab = new CreativeTabs("ingress") {

        @Override
        public Item getTabIconItem() {

            return L8ResonatorItem;

        }

    };

    public static final ScannerItem ScannerItem = new ScannerItem();

    public static final PortalItem PortalItem = new PortalItem();
    public static final PortalKeyItem PortalKeyItem = new PortalKeyItem();

    public static final ResonatorItem L1ResonatorItem = new ResonatorItem(1);
    public static final ResonatorItem L2ResonatorItem = new ResonatorItem(2);
    public static final ResonatorItem L3ResonatorItem = new ResonatorItem(3);
    public static final ResonatorItem L4ResonatorItem = new ResonatorItem(4);
    public static final ResonatorItem L5ResonatorItem = new ResonatorItem(5);
    public static final ResonatorItem L6ResonatorItem = new ResonatorItem(6);
    public static final ResonatorItem L7ResonatorItem = new ResonatorItem(7);
    public static final ResonatorItem L8ResonatorItem = new ResonatorItem(8);
    public static final XMPBursterItem L1XMPBursterItem = new XMPBursterItem(1);
    public static final XMPBursterItem L2XMPBursterItem = new XMPBursterItem(2);
    public static final XMPBursterItem L3XMPBursterItem = new XMPBursterItem(3);
    public static final XMPBursterItem L4XMPBursterItem = new XMPBursterItem(4);
    public static final XMPBursterItem L5XMPBursterItem = new XMPBursterItem(5);
    public static final XMPBursterItem L6XMPBursterItem = new XMPBursterItem(6);
    public static final XMPBursterItem L7XMPBursterItem = new XMPBursterItem(7);
    public static final XMPBursterItem L8XMPBursterItem = new XMPBursterItem(8);

    @SidedProxy(clientSide = "info.evshiron.ingresscraft.ClientProxy", serverSide = "info.evshiron.ingresscraft.CommonProxy")
    public static CommonProxy Proxy;

    public static SimpleNetworkWrapper SyncScannerChannel = NetworkRegistry.INSTANCE.newSimpleChannel("SyncScanner");
    public static SimpleNetworkWrapper SyncPortalChannel = NetworkRegistry.INSTANCE.newSimpleChannel("SyncPortal");

    public Configuration Config;

    public static ResonatorItem GetResonatorItem(int level) {

        switch(level) {
            case 1:
                return L1ResonatorItem;
            case 2:
                return L2ResonatorItem;
            case 3:
                return L3ResonatorItem;
            case 4:
                return L4ResonatorItem;
            case 5:
                return L5ResonatorItem;
            case 6:
                return L6ResonatorItem;
            case 7:
                return L7ResonatorItem;
            case 8:
                return L8ResonatorItem;
            default:
                return null;
        }

    }

    public static XMPBursterItem GetXMPBursterItem(int level) {

        switch(level) {
            case 1:
                return L1XMPBursterItem;
            case 2:
                return L2XMPBursterItem;
            case 3:
                return L3XMPBursterItem;
            case 4:
                return L4XMPBursterItem;
            case 5:
                return L5XMPBursterItem;
            case 6:
                return L6XMPBursterItem;
            case 7:
                return L7XMPBursterItem;
            case 8:
                return L8XMPBursterItem;
            default:
                return null;
        }

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        Config = new Configuration(event.getSuggestedConfigurationFile());

        CONFIG_RANGE_FACTOR = Config.get("general", "RangeFactor", 0.1).getDouble();
        CONFIG_PORTAL_RANGE = 40.0 * CONFIG_RANGE_FACTOR;

        Config.save();

    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

        GameRegistry.registerItem(ScannerItem.setCreativeTab(CreativeTab), ScannerItem.NAME);
        GameRegistry.registerItem(PortalItem.setCreativeTab(CreativeTab), PortalItem.NAME);
        GameRegistry.registerItem(PortalKeyItem.setCreativeTab(CreativeTab), PortalKeyItem.NAME);
        GameRegistry.registerItem(L1ResonatorItem.setCreativeTab(CreativeTab), "l1" + ResonatorItem.NAME);
        GameRegistry.registerItem(L2ResonatorItem.setCreativeTab(CreativeTab), "l2" + ResonatorItem.NAME);
        GameRegistry.registerItem(L3ResonatorItem.setCreativeTab(CreativeTab), "l3" + ResonatorItem.NAME);
        GameRegistry.registerItem(L4ResonatorItem.setCreativeTab(CreativeTab), "l4" + ResonatorItem.NAME);
        GameRegistry.registerItem(L5ResonatorItem.setCreativeTab(CreativeTab), "l5" + ResonatorItem.NAME);
        GameRegistry.registerItem(L6ResonatorItem.setCreativeTab(CreativeTab), "l6" + ResonatorItem.NAME);
        GameRegistry.registerItem(L7ResonatorItem.setCreativeTab(CreativeTab), "l7" + ResonatorItem.NAME);
        GameRegistry.registerItem(L8ResonatorItem.setCreativeTab(CreativeTab), "l8" + ResonatorItem.NAME);
        GameRegistry.registerItem(L1XMPBursterItem.setCreativeTab(CreativeTab), "l1" + XMPBursterItem.NAME);
        GameRegistry.registerItem(L2XMPBursterItem.setCreativeTab(CreativeTab), "l2" + XMPBursterItem.NAME);
        GameRegistry.registerItem(L3XMPBursterItem.setCreativeTab(CreativeTab), "l3" + XMPBursterItem.NAME);
        GameRegistry.registerItem(L4XMPBursterItem.setCreativeTab(CreativeTab), "l4" + XMPBursterItem.NAME);
        GameRegistry.registerItem(L5XMPBursterItem.setCreativeTab(CreativeTab), "l5" + XMPBursterItem.NAME);
        GameRegistry.registerItem(L6XMPBursterItem.setCreativeTab(CreativeTab), "l6" + XMPBursterItem.NAME);
        GameRegistry.registerItem(L7XMPBursterItem.setCreativeTab(CreativeTab), "l7" + XMPBursterItem.NAME);
        GameRegistry.registerItem(L8XMPBursterItem.setCreativeTab(CreativeTab), "l8" + XMPBursterItem.NAME);

        GameRegistry.registerBlock(new XMBlock(), XMBlock.NAME);

        int portalEntityId = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(PortalEntity.class, PortalEntity.NAME, portalEntityId);
        EntityRegistry.registerModEntity(PortalEntity.class, PortalEntity.NAME, portalEntityId, Instance, 64, 1, true);

        int resonatorEntityId = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(ResonatorEntity.class, ResonatorEntity.NAME, resonatorEntityId);
        EntityRegistry.registerModEntity(ResonatorEntity.class, ResonatorEntity.NAME, resonatorEntityId, Instance, 64, 1, true);

        Proxy.RegisterRenderers();

        NetworkRegistry.INSTANCE.registerGuiHandler(Instance, Proxy);

        SyncScannerChannel.registerMessage(SyncScannerMessage.Handler.class, SyncScannerMessage.class, 0, Side.SERVER);
        SyncPortalChannel.registerMessage(SyncPortalMessage.Handler.class, SyncPortalMessage.class, 1, Side.SERVER);

    }

}
