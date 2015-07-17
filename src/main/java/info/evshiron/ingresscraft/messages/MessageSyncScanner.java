package info.evshiron.ingresscraft.messages;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import info.evshiron.ingresscraft.Constants;
import info.evshiron.ingresscraft.items.ItemScanner;
import info.evshiron.ingresscraft.utils.IngressNotifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by evshiron on 6/12/15.
 */
public class MessageSyncScanner implements IMessage, IMessageHandler<MessageSyncScanner, IMessage> {

    public String Codename;
    public int Faction;
    public int Level;
    public int AP;
    public int XM;

    public MessageSyncScanner() {}

    public MessageSyncScanner(ItemStack scanner) {

        NBTTagCompound nbt = scanner.getTagCompound();

        Codename = nbt.getString("codename");
        Faction = nbt.getInteger("faction");
        Level = nbt.getInteger("level");
        AP = nbt.getInteger("ap");
        XM = nbt.getInteger("xm");

    }

    @Override
    public void fromBytes(ByteBuf buf) {

        Codename = ByteBufUtils.readUTF8String(buf);
        Faction = Integer.parseInt(ByteBufUtils.readUTF8String(buf));
        Level = Integer.parseInt(ByteBufUtils.readUTF8String(buf));
        AP = Integer.parseInt(ByteBufUtils.readUTF8String(buf));
        XM = Integer.parseInt(ByteBufUtils.readUTF8String(buf));

    }

    @Override
    public void toBytes(ByteBuf buf) {

        ByteBufUtils.writeUTF8String(buf, Codename);
        ByteBufUtils.writeUTF8String(buf, String.valueOf(Faction));
        ByteBufUtils.writeUTF8String(buf, String.valueOf(Level));
        ByteBufUtils.writeUTF8String(buf, String.valueOf(AP));
        ByteBufUtils.writeUTF8String(buf, String.valueOf(XM));

    }

    @Override
    public IMessage onMessage(MessageSyncScanner message, MessageContext ctx) {

        if(ctx.side.isServer()) {

            ItemStack itemStack = ctx.getServerHandler().playerEntity.getCurrentArmor(3);

            if(itemStack != null && itemStack.getItem() instanceof ItemScanner) {

                NBTTagCompound nbt = itemStack.getTagCompound();

                boolean isNew = false;

                if(nbt.getInteger("faction") == Constants.Faction.NEUTRAL) isNew = true;

                nbt.setString("codename", message.Codename);
                nbt.setInteger("faction", message.Faction);
                nbt.setInteger("level", message.Level);
                nbt.setInteger("ap", message.AP);
                nbt.setInteger("xm", message.XM);

                itemStack.setTagCompound(nbt);

                if(isNew) IngressNotifier.BroadcastJoining(itemStack);

            }

        }
        else {

            ItemStack itemStack = Minecraft.getMinecraft().thePlayer.getCurrentArmor(3);

            if(itemStack != null && itemStack.getItem() instanceof ItemScanner) {

                NBTTagCompound nbt = itemStack.getTagCompound();

                nbt.setString("codename", message.Codename);
                nbt.setInteger("faction", message.Faction);
                nbt.setInteger("level", message.Level);
                nbt.setInteger("ap", message.AP);
                nbt.setInteger("xm", message.XM);

                itemStack.setTagCompound(nbt);

            }

        }

        return null;

    }

}
