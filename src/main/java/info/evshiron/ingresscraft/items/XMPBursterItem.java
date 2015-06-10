package info.evshiron.ingresscraft.items;

import info.evshiron.ingresscraft.Constants;
import info.evshiron.ingresscraft.IngressCraft;
import info.evshiron.ingresscraft.entities.PortalEntity;
import info.evshiron.ingresscraft.entities.ResonatorEntity;
import info.evshiron.ingresscraft.utils.IngressHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by evshiron on 5/25/15
 */
public class XMPBursterItem extends Item {

    public static final String NAME = "xmpBurster";

    public int Level = 0;

    public XMPBursterItem(int level){

        super();

        Level = level;

        setUnlocalizedName(NAME);
        setCreativeTab(IngressCraft.CreativeTab);
        setTextureName(IngressCraft.MODID + ":" + NAME);

    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List lines, boolean p_77624_4_) {

        lines.add(String.format("Level: %d", Level));

    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {

        ItemStack scanner = player.getCurrentArmor(3);

        if(scanner == null || !(scanner.getItem() instanceof ScannerItem)) {

            String broadcast = String.format("XMP Burster can't be fired without Scanner.");
            player.addChatMessage(new ChatComponentText(broadcast));

            return itemStack;

        }

        NBTTagCompound nbt = scanner.getTagCompound();

        if(nbt.getInteger("level") < Level) {

            String broadcast = String.format("You don't have the access to fire this XMP Burster.");
            player.addChatMessage(new ChatComponentText(broadcast));

            return itemStack;

        }

        double range = IngressHelper.GetXMPBursterRange(Level);
        double damage = IngressHelper.GetXMPBursterDamage(Level);

        List<ResonatorEntity> resonators = IngressHelper.GetEntitiesAround(world, ResonatorEntity.class, player, range);

        for(int i = 0; i < resonators.size(); i++) {

            ResonatorEntity resonator = resonators.get(i);

            resonator.attackEntityFrom(new EntityDamageSource(IngressCraft.MODID + ":xmpBurster", player), (float) damage);

        }

        List<PortalEntity> portals = IngressHelper.GetEntitiesAround(world, PortalEntity.class, player, range);

        for(int i = 0; i < portals.size(); i++) {

            PortalEntity portal = portals.get(i);

            portal.attackEntityFrom(new EntityDamageSource(IngressCraft.MODID + ":xmpBurster", player), (float) damage);

        }

        world.spawnParticle("largeexplode", player.posX, player.posY, player.posZ, 1.0D, 0.0D, 0.0D);
        world.spawnParticle("hugeexplosion", player.posX, player.posY, player.posZ, 1.0D, 0.0D, 0.0D);

        //OExplosion explosion = new OExplosion(world, player, player.posX, player.posY, player.posZ, 10.0F);
        //explosion.doExplosionA();
        //explosion.doExplosionB(true);

        player.inventory.consumeInventoryItem(player.getCurrentEquippedItem().getItem());

        return itemStack;

    }

}
