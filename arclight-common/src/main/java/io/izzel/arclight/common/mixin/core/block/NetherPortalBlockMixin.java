package io.izzel.arclight.common.mixin.core.block;

import io.izzel.arclight.common.bridge.block.NetherPortalBlockBridge;
import io.izzel.arclight.common.bridge.entity.EntityBridge;
import io.izzel.arclight.common.bridge.entity.EntityTypeBridge;
import io.izzel.arclight.common.bridge.world.WorldBridge;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;spawn(Lnet/minecraft/world/World;Lnet/minecraft/nbt/CompoundNBT;Lnet/minecraft/util/text/ITextComponent;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/SpawnReason;ZZ)Lnet/minecraft/entity/Entity;"))
    public Entity arclight$spawn(EntityType<?> entityType, World worldIn, CompoundNBT compound, ITextComponent customName, PlayerEntity playerIn, BlockPos pos, SpawnReason reason, boolean flag, boolean flag1) {
        return ((EntityTypeBridge<?>) entityType).bridge$spawnCreature(worldIn, compound, customName, playerIn, pos, reason, flag, flag1, CreatureSpawnEvent.SpawnReason.NETHER_PORTAL);
    }

    @Inject(method = "trySpawnPortal", cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/NetherPortalBlock$Size;placePortalBlocks()V"))
    public void arclight$spawnPortal(IWorld worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir, NetherPortalBlock.Size size) {
        cir.setReturnValue(((NetherPortalBlockBridge.SizeBridge) size).bridge$createPortal());
    }

    @Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setPortal(Lnet/minecraft/util/math/BlockPos;)V"))
    public void arclight$portalEnter(BlockState state, World worldIn, BlockPos pos, Entity entityIn, CallbackInfo ci) {
        EntityPortalEnterEvent event = new EntityPortalEnterEvent(((EntityBridge) entityIn).bridge$getBukkitEntity(),
            new Location(((WorldBridge) worldIn).bridge$getWorld(), pos.getX(), pos.getY(), pos.getZ()));
        Bukkit.getPluginManager().callEvent(event);
    }
}
