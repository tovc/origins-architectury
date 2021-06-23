package io.github.apace100.origins.mixin;

import io.github.apace100.origins.api.component.OriginComponent;
import io.github.apace100.origins.api.power.ConfiguredFactory;
import io.github.apace100.origins.power.InvisibilityPower;
import io.github.apace100.origins.power.configuration.ColorConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer<LivingEntity> {

	protected LivingEntityRendererMixin(EntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Inject(method = "isShaking", at = @At("HEAD"), cancellable = true)
	private void letPlayersShakeTheirBodies(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (OriginComponent.hasPower(entity, ModPowers.SHAKING.get())) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
	private void preventPumpkinRendering(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
		if (InvisibilityPower.isArmorHidden(livingEntity))
			info.cancel();
	}

	@ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;", shift = At.Shift.BEFORE))
	private RenderLayer changeRenderLayerWhenTranslucent(RenderLayer original, LivingEntity entity) {
		if (entity instanceof PlayerEntity player && OriginComponent.getPowers(player, ModPowers.MODEL_COLOR.get()).stream().map(ConfiguredFactory::getConfiguration).anyMatch(x -> x.alpha() < 1.0F))
			return RenderLayer.getItemEntityTranslucentCull(getTexture(entity));
		return original;
	}

	@Environment(EnvType.CLIENT)
	@ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", ordinal = 0))
	private <T extends LivingEntity> void renderColorChangedModel(Args args, LivingEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (player instanceof PlayerEntity) {
			Optional<ColorConfiguration> configuration = ColorConfiguration.forPower(player, ModPowers.MODEL_COLOR.get());
			if (configuration.isPresent()) {
				//Mixin is being weird.
				//Basically: if there is a redirect, args[0] is a Model
				// otherwise args[0] is the MatrixStack
				int red = args.size() - 4;
				int green = args.size() - 3;
				int blue = args.size() - 2;
				int alpha = args.size() - 1;
				args.set(red, args.<Float>get(red) * configuration.get().red());
				args.set(green, args.<Float>get(green) * configuration.get().green());
				args.set(blue, args.<Float>get(blue) * configuration.get().blue());
				args.set(alpha, args.<Float>get(alpha) * configuration.get().alpha());
			}
		}
	}
}
