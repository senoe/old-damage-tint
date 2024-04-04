package dev.senoe.olddamagetint.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {

    @Unique
    private final float REDUCTION_FACTOR = 0.3F;

    @Unique
    private T entity;

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void hookRenderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
        this.entity = entity;
    }

    @ModifyVariable(method = "renderArmorParts", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float hookRenderArmorPartsRed(float red) {
        if (entity.hurtTime == 0) {
            return red;
        }
        return Math.min(1, red + ((1 - REDUCTION_FACTOR) * getDamageFactor()));
    }

    @ModifyVariable(method = "renderArmorParts", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private float hookRenderArmorPartsGreen(float green) {
        if (entity.hurtTime == 0) {
            return green;
        }
        return green * (1 - (getDamageFactor() * REDUCTION_FACTOR));
    }

    @ModifyVariable(method = "renderArmorParts", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private float hookRenderArmorPartsBlue(float blue) {
        if (entity.hurtTime == 0) {
            return blue;
        }
        return blue * (1 - (getDamageFactor() * REDUCTION_FACTOR));
    }

    @Unique
    private float getDamageFactor() {
        return entity.hurtTime / (float) entity.maxHurtTime;
    }

}
