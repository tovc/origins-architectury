package io.github.apace100.origins.mixin;

import io.github.apace100.origins.power.configuration.ColorConfiguration;
import io.github.apace100.origins.registry.ModPowers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

	private static void act(ModelPart modelPart, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, VertexConsumerProvider vertexConsumers, AbstractClientPlayerEntity player) {
		Optional<ColorConfiguration> configuration = ColorConfiguration.forPower(player, ModPowers.MODEL_COLOR.get());
		if (configuration.isPresent())
			modelPart.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, overlay, configuration.get().red(), configuration.get().green(), configuration.get().blue(), configuration.get().alpha());
		else
			modelPart.render(matrices, vertices, light, overlay);
	}

	@Environment(EnvType.CLIENT)
	@Redirect(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 0))
	private void makeArmTranslucent(ModelPart modelPart, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, MatrixStack matrices2, VertexConsumerProvider vertexConsumers, int light2, AbstractClientPlayerEntity player) {
		act(modelPart, matrices, vertices, light, overlay, vertexConsumers, player);
	}

	@Environment(EnvType.CLIENT)
	@Redirect(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 1))
	private void makeSleeveTranslucent(ModelPart modelPart, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, MatrixStack matrices2, VertexConsumerProvider vertexConsumers, int light2, AbstractClientPlayerEntity player) {
		act(modelPart, matrices, vertices, light, overlay, vertexConsumers, player);
	}
}
