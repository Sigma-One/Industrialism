package util;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import sigmaone.industrialism.Industrialism;

import java.util.OptionalDouble;

// Based on code from the Clothesline mod by JamiesWhiteShirt
// https://github.com/JamiesWhiteShirt/clothesline-fabric
// Modified by Sigma-One

public class WiringRenderLayer extends RenderLayer {
    public WiringRenderLayer(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    private static final VertexFormat vertexFormat = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
            .add(VertexFormats.POSITION_ELEMENT)
            .add(VertexFormats.COLOR_ELEMENT)
            .add(VertexFormats.LIGHT_ELEMENT)
            .build()
    );

    private static final Identifier TEXTURE = new Identifier(Industrialism.MOD_ID, "textures/wire.png");

    private static final RenderLayer WIRING = RenderLayer.of("wiring", vertexFormat, GL11.GL_QUADS, 256, RenderLayer.MultiPhaseParameters.builder()
            .transparency(RenderPhase.NO_TRANSPARENCY)
            .lightmap(ENABLE_LIGHTMAP)
            .diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
            .build(false)
    );

    public static RenderLayer getWiring() {
        return WIRING;
    }
}