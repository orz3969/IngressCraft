package info.evshiron.ingresscraft.entities;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import info.evshiron.ingresscraft.Constants;
import info.evshiron.ingresscraft.IngressCraft;
import info.evshiron.ingresscraft.client.gui.PortalGUI;
import info.evshiron.ingresscraft.items.ScannerItem;
import info.evshiron.ingresscraft.items.XMPBursterItem;
import info.evshiron.ingresscraft.utils.IngressHelper;
import info.evshiron.ingresscraft.utils.IngressNotifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

/**
 * Created by evshiron on 5/26/15.
 */
public class PortalEntity extends IngressEntityBase implements IEntityAdditionalSpawnData {

    public static final String NAME = "portal";

    public String Uuid = "";
    public String Name = "-UNKNOWN-";
    public int Faction = Constants.Faction.NEUTRAL;
    public String Owner = "-UNKNOWN-";

    public EntityPlayer AttackingAgent = null;

    public PortalEntity(World world) {

        super(world);

        Uuid = UUID.randomUUID().toString();

        setSize(1, 3);

    }

    public void SetName(String name) {

        Name = name;

    }

    public void SetFaction(int faction) {

        Faction = faction;

    }

    public void SetOwner(String owner) {

        Owner = owner;

    }

    public int GetLevel() {

        int levels = 0;

        List<ResonatorEntity> resonators = IngressHelper.GetEntitiesAround(worldObj, ResonatorEntity.class, this, IngressCraft.CONFIG_PORTAL_RANGE);

        for(int i = 0; i < resonators.size(); i++) {

            ResonatorEntity resonator = resonators.get(i);

            levels += resonator.Level;

        }

        return resonators.size() == 0 ? 0 : levels / resonators.size();

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {

        nbt.setString("uuid", Uuid);
        nbt.setString("name", Name);
        nbt.setInteger("faction", Faction);
        nbt.setString("owner", Owner);

        super.writeEntityToNBT(nbt);

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {

        super.readEntityFromNBT(nbt);

        Uuid = nbt.getString("uuid");
        Name = nbt.getString("name");
        Faction = nbt.getInteger("faction");
        Owner = nbt.getString("owner");

    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {

        ByteBufUtils.writeUTF8String(buffer, Uuid);
        ByteBufUtils.writeUTF8String(buffer, Name);
        ByteBufUtils.writeUTF8String(buffer, String.valueOf(Faction));
        ByteBufUtils.writeUTF8String(buffer, Owner);

    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {

        Uuid = ByteBufUtils.readUTF8String(additionalData);
        Name = ByteBufUtils.readUTF8String(additionalData);
        Faction = Integer.parseInt(ByteBufUtils.readUTF8String(additionalData));
        Owner = ByteBufUtils.readUTF8String(additionalData);

    }

    @Override
    public boolean shouldRenderInPass(int pass) {

        // 0 = Opaque, 1 = Translucent.
        return pass == 1;

    }

    @Override
    public void onLivingUpdate() {

        List<ResonatorEntity> resonators = IngressHelper.GetEntitiesAround(worldObj, ResonatorEntity.class, this, IngressCraft.CONFIG_PORTAL_RANGE);

        if (Faction != Constants.Faction.NEUTRAL && AttackingAgent != null && resonators.size() == 0) {

            onDeath(new EntityDamageSource(IngressCraft.MODID + ":xmpBurster", AttackingAgent));

        }
        else if(resonators.size() == 0) {

            SetFaction(Constants.Faction.NEUTRAL);
            SetOwner("-UNKNOWN-");

        }
        else if(Faction == Constants.Faction.NEUTRAL && resonators.size() > 0) {

            int faction = resonators.get(0).Faction;

            for(int i = 1; i < resonators.size(); i++) {

                ResonatorEntity resonator = resonators.get(i);

                if(resonator.Faction != faction) {

                    return;

                }

            }

            Faction = faction;

        }
        else {



        }

    }

    @Override
    protected boolean interact(EntityPlayer player) {

        if(player.getCurrentEquippedItem().getItem() instanceof XMPBursterItem) {

            return false;

        }
        else {

            player.openGui(IngressCraft.Instance, PortalGUI.ID, worldObj, (int) posX, (int) posY, getEntityId());

            return true;

        }

    }

    public boolean attackEntityFrom(DamageSource source, float damage) {

        if(source.getEntity() instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) source.getEntity();

            ItemStack scanner = player.getCurrentArmor(3);

            if(scanner == null || !(scanner.getItem() instanceof ScannerItem)) {

                return false;

            }

            if(source.getDamageType().contentEquals(IngressCraft.MODID + ":xmpBurster")) {

                AttackingAgent = player;

                return true;

            }
            else {

                if(worldObj.isRemote) return false;

                NBTTagCompound nbt = scanner.getTagCompound();

                int times = rand.nextInt(4);
                for(int i = 0; i < times; i++) {

                    int type = rand.nextInt(2);

                    int amount;
                    int level;
                    ItemStack itemStack;

                    switch(type) {
                        case 0: // Resonator.

                            amount = rand.nextInt(3 + GetLevel() / 2 + (nbt.getInteger("faction") == Faction ? 2 : 0));

                            // Math.max let level >= 1, Math.min etc. let level in [x-2, x+2].
                            level = Math.max(1, Math.min(nbt.getInteger("level"), GetLevel()) - 2 + rand.nextInt(5));

                            entityDropItem(new ItemStack(IngressCraft.GetResonatorItem(level), amount, 0), 0);
                            break;

                        case 1: // XMPBurster.

                            amount = rand.nextInt(3 + GetLevel() / 2 + (nbt.getInteger("faction") != Faction ? 2 : 0));

                            // Math.max let level >= 1, Math.min etc. let level in [x-2, x+2].
                            level = Math.max(1, Math.min(nbt.getInteger("level"), GetLevel()) - 2 + rand.nextInt(5));

                            entityDropItem(new ItemStack(IngressCraft.GetXMPBursterItem(level), amount, 0), 0);
                            break;

                    }

                }

                // Portal Key.
                ItemStack portalKey = new ItemStack(IngressCraft.PortalKeyItem, 1, 0);

                NBTTagCompound nbt1 = new NBTTagCompound();
                nbt1.setString("uuid", Uuid);
                nbt1.setString("name", Name);
                nbt1.setInteger("level", GetLevel());
                nbt1.setDouble("x", posX);
                nbt1.setDouble("y", posY);
                nbt1.setDouble("z", posZ);

                portalKey.setTagCompound(nbt1);
                if(!IngressHelper.HasPortalKey(player, portalKey)) entityDropItem(portalKey, 0);

                return true;

            }

        }

        return false;

    }

    @Override
    public void onDeath(DamageSource source) {

        if(source.getEntity() instanceof EntityPlayer) {

            ItemStack scanner = ((EntityPlayer) source.getEntity()).getCurrentArmor(3);

            if(scanner.getItem() instanceof ScannerItem) {

                if(!worldObj.isRemote) IngressNotifier.BroadcastNeutralizing(scanner);

                // Show effects.

                SetFaction(Constants.Faction.NEUTRAL);
                SetOwner("-UNKNOWN-");

                AttackingAgent = null;

            }

        }
        else if(source.getEntity() == null) {

            SetFaction(Constants.Faction.NEUTRAL);
            SetOwner("-UNKNOWN-");

        }

    }

}
