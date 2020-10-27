package info.pinkhouse.tidbits.mixin;

import info.pinkhouse.tidbits.TidbitsInitKt;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
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

    @Redirect(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceManager;getAllResources(Lnet/minecraft/util/Identifier;)Ljava/util/List;"))
    public List<Resource> loadModel_hackGetAllResources(ResourceManager resourceManager, Identifier id) throws IOException {
        if (id.getNamespace() == "tidbits") {
            List<Resource> out;
            out.add(new ResourceImpl(resourcePack.getName(), id, this.open(id, resourcePack), inputStream))
        } else {
            return resourceManager.getAllResources(id);
        }
    }
}
