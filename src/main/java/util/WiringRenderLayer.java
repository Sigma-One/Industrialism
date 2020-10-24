package util;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.render.*;
import org.lwjgl.opengl.GL11;

import java.util.OptionalDouble;

public class WiringRenderLayer extends RenderLayer {

    public WiringRenderLayer(String name, VertexFormat vertexFormat, int drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    private static final VertexFormat vertexFormat = new VertexFormat(ImmutableList.<VertexFormatElement>builder()
            .add(VertexFormats.POSITION_ELEMENT)
            .add(VertexFormats.COLOR_ELEMENT)
            .build()
    );

    // TODO: What is a reasonable default buffer size?
    private static final RenderLayer WIRING = RenderLayer.of("wiring", vertexFormat, GL11.GL_LINE_STRIP, 256, RenderLayer.MultiPhaseParameters.builder()
            .lineWidth(new LineWidth(OptionalDouble.of(16)))
            .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
            .alpha(HALF_ALPHA)
            //.depthTest(EQUAL_DEPTH_TEST)
            .build(false)
    );

    public static RenderLayer getWiring() {
        return WIRING;
    }
}