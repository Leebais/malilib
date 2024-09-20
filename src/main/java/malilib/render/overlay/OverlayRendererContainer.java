package malilib.render.overlay;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;

import malilib.render.RenderContext;
import malilib.util.BackupUtils;
import malilib.util.data.json.JsonUtils;
import malilib.util.game.wrap.EntityWrap;
import malilib.util.game.wrap.GameWrap;
import malilib.util.game.wrap.RenderWrap;
import malilib.util.position.Vec3d;

public class OverlayRendererContainer
{
    public static final OverlayRendererContainer INSTANCE = new OverlayRendererContainer();

    protected final List<BaseOverlayRenderer> renderers = new ArrayList<>();
    protected final List<BaseOverlayRenderer> enabledRenderers = new ArrayList<>();
    protected boolean resourcesAllocated;
    protected boolean useVbo;
    protected int countActive;

    private boolean canRender;
    private boolean enabledRenderersNeedUpdate;
    private long loginTime;

    public void addRenderer(BaseOverlayRenderer renderer)
    {
        if (this.resourcesAllocated)
        {
            renderer.deleteGlResources();
            renderer.allocateGlResources();
        }

        this.renderers.add(renderer);
        this.setEnabledRenderersNeedUpdate();
    }

    public void removeRenderer(BaseOverlayRenderer renderer)
    {
        this.renderers.remove(renderer);
        this.setEnabledRenderersNeedUpdate();

        if (this.resourcesAllocated)
        {
            renderer.deleteGlResources();
        }
    }

    public void setEnabledRenderersNeedUpdate()
    {
        this.enabledRenderersNeedUpdate = true;
    }

    protected Vec3d getCameraPos(Entity cameraEntity, float partialTicks)
    {
        double x = EntityWrap.lerpX(cameraEntity, partialTicks);
        double y = EntityWrap.lerpY(cameraEntity, partialTicks);
        double z = EntityWrap.lerpZ(cameraEntity, partialTicks);

        return new Vec3d(x, y, z);
    }

    public void resetRenderTimeout()
    {
        this.canRender = false;
        this.loginTime = System.nanoTime();
    }

    protected void updateEnabledRenderersList()
    {
        this.enabledRenderers.clear();

        for (BaseOverlayRenderer renderer : this.renderers)
        {
            if (renderer.isEnabled())
            {
                this.enabledRenderers.add(renderer);
            }
        }

        this.enabledRenderersNeedUpdate = false;
    }

    public void render(RenderContext ctx, float tickDelta)
    {
        Entity cameraEntity = GameWrap.getCameraEntity();

        if (cameraEntity == null)
        {
            return;
        }

        if (this.canRender == false)
        {
            // Don't render before the player has been placed in the actual proper position,
            // otherwise some renderers will mess up.
            // The magic 8.5, 65, 8.5 comes from the ClientWorld constructor
            if (System.nanoTime() - this.loginTime >= 5000000000L ||
                EntityWrap.getX(cameraEntity) != 8.5 ||
                EntityWrap.getY(cameraEntity) != 65 ||
                EntityWrap.getZ(cameraEntity) != 8.5)
            {
                this.canRender = true;
            }
            else
            {
                return;
            }
        }

        Vec3d cameraPos = this.getCameraPos(cameraEntity, tickDelta);

        GameWrap.profilerPush("update");
        this.update(cameraPos, cameraEntity);
        GameWrap.profilerPop();

        GameWrap.profilerPush("draw");
        this.draw(cameraPos, ctx);
        GameWrap.profilerPop();
    }

    protected void update(Vec3d cameraPos, Entity entity)
    {
        if (this.enabledRenderersNeedUpdate)
        {
            this.updateEnabledRenderersList();
        }

        this.checkVideoSettings();
        this.countActive = 0;

        for (BaseOverlayRenderer renderer : this.enabledRenderers)
        {
            GameWrap.profilerPush(() -> renderer.getClass().getName());

            if (renderer.shouldRender())
            {
                if (renderer.needsUpdate(entity))
                {
                    renderer.setLastUpdatePos(EntityWrap.getEntityBlockPos(entity));
                    renderer.setUpdatePosition(cameraPos);
                    renderer.update(cameraPos, entity);
                }

                ++this.countActive;
            }

            GameWrap.profilerPop();
        }
    }

    protected void draw(Vec3d cameraPos, RenderContext ctx)
    {
        if (this.resourcesAllocated && this.countActive > 0)
        {
            RenderWrap.pushMatrix(ctx);

            RenderWrap.disableTexture2D();
            RenderWrap.alphaFunc(GL11.GL_GREATER, 0.01F);
            RenderWrap.disableCull();
            RenderWrap.disableLighting();
            RenderWrap.depthMask(false);
            RenderWrap.polygonOffset(-3f, -3f);
            RenderWrap.enablePolygonOffset();

            RenderWrap.color(1f, 1f, 1f, 1f);
            RenderWrap.setupBlendSeparate();

            if (RenderWrap.useVbo())
            {
                RenderWrap.enableClientState(GL11.GL_VERTEX_ARRAY);
                RenderWrap.enableClientState(GL11.GL_COLOR_ARRAY);
            }

            double cx = cameraPos.x;
            double cy = cameraPos.y;
            double cz = cameraPos.z;

            for (BaseOverlayRenderer renderer : this.enabledRenderers)
            {
                GameWrap.profilerPush(() -> renderer.getClass().getName());

                if (renderer.shouldRender())
                {
                    Vec3d updatePos = renderer.getUpdatePosition();
                    RenderWrap.pushMatrix(ctx);
                    //RenderWrap.translate(updatePos.x - cx, updatePos.y - cy, updatePos.z - cz, ctx);
                    RenderWrap.translate(updatePos.x, updatePos.y, updatePos.z, ctx); // Indev works differently...

                    renderer.draw();

                    RenderWrap.popMatrix(ctx);
                }

                GameWrap.profilerPop();
            }

            if (RenderWrap.useVbo())
            {
                RenderWrap.bindBuffer(RenderWrap.GL_ARRAY_BUFFER, 0);
                //RenderWrap.resetColor();

                RenderWrap.disableClientState(GL11.GL_VERTEX_ARRAY);
                RenderWrap.disableClientState(GL11.GL_COLOR_ARRAY);
            }

            RenderWrap.color(1f, 1f, 1f, 1f);

            RenderWrap.polygonOffset(0f, 0f);
            RenderWrap.disablePolygonOffset();
            RenderWrap.disableBlend();
            RenderWrap.enableDepthTest();
            RenderWrap.enableCull();
            RenderWrap.depthMask(true);
            RenderWrap.enableTexture2D();

            RenderWrap.popMatrix(ctx);
        }
    }

    protected void checkVideoSettings()
    {
        boolean vboLast = this.useVbo;
        this.useVbo = RenderWrap.useVbo();

        if (vboLast != this.useVbo || this.resourcesAllocated == false)
        {
            this.deleteGlResources();
            this.allocateGlResources();
        }
    }

    protected void allocateGlResources()
    {
        if (this.resourcesAllocated == false)
        {
            for (BaseOverlayRenderer renderer : this.renderers)
            {
                renderer.deleteGlResources();
                renderer.allocateGlResources();
            }

            this.resourcesAllocated = true;
        }
    }

    protected void deleteGlResources()
    {
        if (this.resourcesAllocated)
        {
            for (BaseOverlayRenderer renderer : this.renderers)
            {
                renderer.deleteGlResources();
            }

            this.resourcesAllocated = false;
        }
    }

    protected ArrayListMultimap<Path, BaseOverlayRenderer> getModGroupedRenderersForSerialization(boolean isDimensionChangeOnly)
    {
        ArrayListMultimap<Path, BaseOverlayRenderer> map = ArrayListMultimap.create();

        for (BaseOverlayRenderer renderer : this.renderers)
        {
            String id = renderer.getSaveId();
            Path file = renderer.getSaveFile(isDimensionChangeOnly);

            if (file != null && StringUtils.isBlank(id) == false)
            {
                map.put(file, renderer);
            }
        }

        return map;
    }

    public void saveToFile(boolean isDimensionChangeOnly)
    {
        ArrayListMultimap<Path, BaseOverlayRenderer> map = this.getModGroupedRenderersForSerialization(isDimensionChangeOnly);

        for (Path file : map.keySet())
        {
            JsonObject obj = new JsonObject();

            for (BaseOverlayRenderer renderer : map.get(file))
            {
                obj.add(renderer.getSaveId(), renderer.toJson());
            }

            if (BackupUtils.createRegularBackup(file, file.getParent().resolve("backups")))
            {
                JsonUtils.writeJsonToFile(obj, file);
            }
        }
    }

    public void loadFromFile(boolean isDimensionChangeOnly)
    {
        ArrayListMultimap<Path, BaseOverlayRenderer> map = this.getModGroupedRenderersForSerialization(isDimensionChangeOnly);

        for (Path file : map.keySet())
        {
            if (Files.isRegularFile(file) == false || Files.isReadable(file) == false)
            {
                continue;
            }

            JsonElement element = JsonUtils.parseJsonFile(file);

            if (element == null || element.isJsonObject() == false)
            {
                continue;
            }

            JsonObject obj = element.getAsJsonObject();

            for (BaseOverlayRenderer renderer : map.get(file))
            {
                String id = renderer.getSaveId();

                if (StringUtils.isBlank(id) == false && JsonUtils.hasObject(obj, id))
                {
                    renderer.fromJson(obj.get(id).getAsJsonObject());
                }
            }
        }
    }
}
