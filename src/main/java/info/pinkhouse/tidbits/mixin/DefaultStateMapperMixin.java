package info.pinkhouse.tidbits.mixin;

import info.pinkhouse.tidbits.TidbitsInitKt;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(DefaultStateMapper.class)
public class DefaultStateMapperMixin {
    @Inject(method = "loadModelFromJson", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getResource(Lnet/minecraft/util/Identifier;)Lnet/minecraft/resource/Resource;"), cancellable = true)
    public void loadModelFromJson(Identifier id, CallbackInfoReturnable<JsonUnbakedModel> cir) {
        if (!"tidbits".equals(id.getNamespace())) return;
        Map<Identifier, JsonUnbakedModel> models = TidbitsInitKt.getMODELS();
        JsonUnbakedModel model = models.getOrDefault(id, null);
        if (model != null) {
            cir.setReturnValue(model);
            cir.cancel();
        }
    }
}
