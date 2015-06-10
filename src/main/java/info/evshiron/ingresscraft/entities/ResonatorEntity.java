package info.evshiron.ingresscraft.entities;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import info.evshiron.ingresscraft.Constants;
import info.evshiron.ingresscraft.IngressCraft;
import info.evshiron.ingresscraft.items.ScannerItem;
import info.evshiron.ingresscraft.items.XMPBursterItem;
import info.evshiron.ingresscraft.utils.IngressHelper;
import info.evshiron.ingresscraft.utils.IngressNotifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by evshiron on 5/31/15.
 */
public class ResonatorEntity extends IngressEntityBase implements IEntityAdditionalSpawnData {

    public static String NAME = "resonator";

    public int Level = 0;
    public int Faction = Constants.Faction.NEUTRAL;
    public String Owner = "NIA";
    public EntityPlayer AttackingAgent = null;

    public ResonatorEntity(World world) {

        super(world);

    }

    public void SetLevel(int level) { Level = level; }

    public void SetFaction(int faction) {
        Faction = faction;
    }

    public void SetOwner(String owner) {
        Owner = owner;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {

        nbt.setInteger("level", Level);
        nbt.setInteger("faction", Faction);
        nbt.setString("owner", Owner);

        super.writeEntityToNBT(nbt);

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {

        super.readEntityFromNBT(nbt);

        Level = nbt.getInteger("level");
        Faction = nbt.getInteger("faction");
        Owner = nbt.getString("owner");

        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(IngressHelper.GetResonatorMaxXM(Level));
        // FIXME: XM.
        setHealth(getMaxHealth());

    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {

        ByteBufUtils.writeUTF8String(buffer, String.valueOf(Level));
        ByteBufUtils.writeUTF8String(buffer, String.valueOf(Faction));
        ByteBufUtils.writeUTF8String(buffer, Owner);

    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {

        Level = Integer.parseInt(ByteBufUtils.readUTF8String(additionalData));
        Faction = Integer.parseInt(ByteBufUtils.readUTF8String(additionalData));
        Owner = ByteBufUtils.readUTF8String(additionalData);

    }

    @Override
    public void onLivingUpdate() {

        if(getHealth() <= 0) {

            onDeath(new EntityDamageSource(IngressCraft.MODID + ":xmpBurster", AttackingAgent));

        }

    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {

        if(source.getDamageType().contentEquals(IngressCraft.MODID + ":xmpBurster")) {

            EntityPlayer player = (EntityPlayer) source.getEntity();

            ItemStack scanner = player.getCurrentArmor(3);

            if(scanner.getItem() instanceof ScannerItem && scanner.getTagCompound().getInteger("faction") != Faction) {

                AttackingAgent = player;

                ItemStack xmpBurster;

                if((xmpBurster = player.getCurrentEquippedItem()).getItem() instanceof XMPBursterItem) {

                    double xmpBursterRange = IngressHelper.GetXMPBursterRange(xmpBurster.getTagCompound().getInteger("level"));

                    float newDamage = (float) IngressHelper.GetCalculatedDamage(xmpBursterRange, IngressHelper.GetDistanceBetween(player, this), damage);

                    damageEntity(source, newDamage);

                }

            }

        }

        return true;

    }

    @Override
    protected void damageEntity(DamageSource source, float damage) {

        prevHealth = getHealth();

        setHealth(prevHealth - damage);

        if(getHealth() <= 0) {

            onDeath(source);

        }

    }

    @Override
    public void onDeath(DamageSource source) {

        EntityPlayer player;
        ItemStack scanner;

        if(source.getEntity() instanceof EntityPlayer && (scanner = (player = (EntityPlayer) source.getEntity()).getCurrentArmor(3)).getItem() instanceof ScannerItem) {

            NBTTagCompound nbt = scanner.getTagCompound();

            if(!worldObj.isRemote) IngressNotifier.BroadcastDestroying(nbt);

            nbt.setInteger("ap", nbt.getInteger("ap") + 75);

            if(!worldObj.isRemote) IngressCraft.SyncScannerChannel.sendTo(new IngressCraft.SyncScannerMessage(nbt), (EntityPlayerMP) player);

        }

        isDead = true;

    }


}
