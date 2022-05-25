package me.mcblueparrot.client.mixin.mod;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.mcblueparrot.client.mod.impl.V1_7VisualsMod;
import me.mcblueparrot.client.util.access.AccessEntityLivingBase;
import me.mcblueparrot.client.util.access.AccessMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public abstract class MixinV1_7VisualsMod {

	@Mixin(ItemRenderer.class)
	public static abstract class MixinItemRenderer {

		@Shadow protected abstract void transformFirstPersonItem(float equipProgress, float swingProgress);

		@Shadow @Final private Minecraft mc;

		@Redirect(method = "renderItemInFirstPerson", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;transformFirstPersonItem(FF)V"))
		public void allowUseAndSwing(ItemRenderer itemRenderer, float equipProgress, float swingProgress) {
			transformFirstPersonItem(equipProgress,
					swingProgress == 0.0F && V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine ?
					mc.thePlayer.getSwingProgress(AccessMinecraft.getInstance().getTimerSC().renderPartialTicks) :
							swingProgress);
		}

	}

	@Mixin(EntityRenderer.class)
	public static abstract class MixinEntityRenderer {

		private float eyeHeightSubtractor;
		private long lastEyeHeightUpdate;

		@Inject(method = "renderHand", at = @At(value = "HEAD"))
		public void forceSwing(float partialTicks, int xOffset, CallbackInfo callback) {
			if(mc.thePlayer != null && V1_7VisualsMod.enabled && V1_7VisualsMod.instance.useAndMine
					&& mc.objectMouseOver != null
					&& mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
					&& mc.thePlayer != null
					&& mc.gameSettings.keyBindAttack.isKeyDown() && mc.gameSettings.keyBindUseItem.isKeyDown()
					&& mc.thePlayer.getItemInUseCount() > 0 && (!mc.thePlayer.isSwingInProgress
					|| mc.thePlayer.swingProgressInt >= ((AccessEntityLivingBase) mc.thePlayer).accessArmSwingAnimationEnd()
					/ 2 || mc.thePlayer.swingProgressInt < 0)) {
				mc.thePlayer.swingProgressInt = -1;
				mc.thePlayer.isSwingInProgress = true;
			}
		}

		@Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getEyeHeight()F"))
		public float smoothSneaking(Entity entity) {
			if(V1_7VisualsMod.enabled && V1_7VisualsMod.instance.sneaking
					&& entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				float height = player.getEyeHeight();
				if(player.isSneaking()) {
					height += 0.08F;
				}
				float actualEyeHeightSubtractor = player.isSneaking() ? 0.08F : 0;
				long sinceLastUpdate = System.currentTimeMillis() - lastEyeHeightUpdate;
				lastEyeHeightUpdate = System.currentTimeMillis();
				if(actualEyeHeightSubtractor > eyeHeightSubtractor) {
					eyeHeightSubtractor += sinceLastUpdate / 500f;
					if(actualEyeHeightSubtractor < eyeHeightSubtractor) {
						eyeHeightSubtractor = actualEyeHeightSubtractor;
					}
				}
				else if(actualEyeHeightSubtractor < eyeHeightSubtractor) {
					eyeHeightSubtractor -= sinceLastUpdate / 500f;
					if(actualEyeHeightSubtractor > eyeHeightSubtractor) {
						eyeHeightSubtractor = actualEyeHeightSubtractor;
					}
				}
				return height - eyeHeightSubtractor;
			}
			return entity.getEyeHeight();
		}

		@Shadow private Minecraft mc;

	}

	@Mixin(LayerArmorBase.class)
	public static class MixinLayerArmorBase {

		@Inject(method = "shouldCombineTextures", at = @At("HEAD"), cancellable = true)
		public void oldArmour(CallbackInfoReturnable<Boolean> callback) {
			if(V1_7VisualsMod.enabled && V1_7VisualsMod.instance.armourDamage) {
				callback.setReturnValue(true);
			}
		}

	}

}
