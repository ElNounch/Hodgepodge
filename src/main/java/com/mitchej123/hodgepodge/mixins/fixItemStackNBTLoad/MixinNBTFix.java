package com.mitchej123.hodgepodge.mixins.fixItemStackNBTLoad;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("ALL")
@Mixin(ItemStack.class)
public class MixinNBTFix {

    @Shadow
    private Item field_151002_e;

    @Shadow
    public int stackSize;

    @Shadow
    int itemDamage;

    @Shadow
    public NBTTagCompound stackTagCompound;

    @Inject(method = "writeToNBT", at = @At("HEAD"), cancellable = true)
    public void writeToNBT(NBTTagCompound p_77955_1_, CallbackInfoReturnable<NBTTagCompound> ci) {
        //kept for legacy uses
        p_77955_1_.setShort("id", (short) Item.getIdFromItem(this.field_151002_e));
        p_77955_1_.setString("registryName", Item.itemRegistry.getNameForObject(this.field_151002_e));
        p_77955_1_.setByte("Count", (byte)this.stackSize);
        p_77955_1_.setShort("Damage", (short)this.itemDamage);

        if (this.stackTagCompound != null)
        {
            p_77955_1_.setTag("tag", this.stackTagCompound);
        }
        ci.setReturnValue(p_77955_1_);
        ci.cancel();
    }

    @Shadow
    public void func_150996_a(Item p_150996_1_){}

    @Inject(method = "readFromNBT", at = @At("HEAD"), cancellable = true)
    public void readFromNBT(NBTTagCompound p_77963_1_, CallbackInfo ci) {
        if (p_77963_1_.hasKey("registryName")) {
            this.func_150996_a((Item) Item.itemRegistry.getObject(p_77963_1_.getString("registryName")));
        } else {
            this.func_150996_a(Item.getItemById(p_77963_1_.getShort("id")));
        }

        this.stackSize = p_77963_1_.getByte("Count");
        this.itemDamage = p_77963_1_.getShort("Damage");

        if (this.itemDamage < 0) {
            this.itemDamage = 0;
        }

        if (p_77963_1_.hasKey("tag", 10)) {
            this.stackTagCompound = p_77963_1_.getCompoundTag("tag");
        }
        ci.cancel();
    }

}
