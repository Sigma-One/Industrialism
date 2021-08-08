package util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.render.*;

// Based on code from the Clothesline mod by JamiesWhiteShirt
// https://github.com/JamiesWhiteShirt/clothesline-fabric
// Modified by Sigma-One

public class WiringRenderLayer extends RenderLayer {
    public WiringRenderLayer(
        String name,
        VertexFormat vertexFormat,
        VertexFormat.DrawMode drawMode,
        int expectedBufferSize,
        boolean hasCrumbling,
        boolean translucent,
        Runnable startAction,
        Runnable endAction)
    {
        super(
            name,
            vertexFormat,
            drawMode,
            expectedBufferSize,
            hasCrumbling,
            translucent,
            startAction,
            endAction
        );
    }

    private static final VertexFormat vertexFormat = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
            .put("Position", VertexFormats.POSITION_ELEMENT)
            .put("Color", VertexFormats.COLOR_ELEMENT)
            .put("UV2", VertexFormats.LIGHT_ELEMENT)
            .build()
    );

    private static final RenderLayer WIRING = RenderLayer.of(
        "wiring",
        vertexFormat,
        VertexFormat.DrawMode.QUADS,
        256,
        RenderLayer.MultiPhaseParameters.builder()
            .transparency(NO_TRANSPARENCY)
            .lightmap(ENABLE_LIGHTMAP)
            .shader(RenderPhase.ENTITY_SOLID_SHADER)
        .build(false)
    );

    public static RenderLayer getWiring() {
        return WIRING;
    }
}