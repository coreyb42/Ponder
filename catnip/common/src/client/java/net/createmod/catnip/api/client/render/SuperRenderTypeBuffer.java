package net.createmod.catnip.api.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Collects custom level geometry for submission through Minecraft's 26.2 render graph.
 *
 * <p>This deliberately is not a {@code MultiBufferSource}. Immediate render buffers were
 * removed with the render-state extraction work: geometry must be handed to an
 * {@link net.minecraft.client.renderer.OrderedSubmitNodeCollector} while a level frame is
 * being assembled.</p>
 */
public interface SuperRenderTypeBuffer {
	VertexConsumer getEarlyBuffer(RenderType type);

	VertexConsumer getBuffer(RenderType type);

	VertexConsumer getLateBuffer(RenderType type);

	default VertexConsumer getEarlyBuffer(ChunkSectionLayer layer) {
		return getEarlyBuffer(RenderHelper.convertLayerToType(layer));
	}

	default VertexConsumer getBuffer(ChunkSectionLayer layer) {
		return getBuffer(RenderHelper.convertLayerToType(layer));
	}

	default VertexConsumer getLateBuffer(ChunkSectionLayer layer) {
		return getLateBuffer(RenderHelper.convertLayerToType(layer));
	}

	void draw();

	void draw(RenderType type);
}
