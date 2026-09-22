package net.createmod.catnip.api.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Collects custom level geometry for submission through Minecraft's 26.2 render graph.
 *
 * <p>This deliberately is not a {@code MultiBufferSource}. Immediate render buffers were
 * removed with the render-state extraction work: geometry must be handed to an
 * {@link SubmitNodeCollector} while a level frame is
 * being assembled.</p>
 */
public interface SuperRenderTypeBuffer {
	/**
	 * The frame-local render-graph collector backing this buffer.
	 *
	 * <p>Use this for Minecraft render states (such as item render states) which
	 * submit their own feature nodes rather than vertex data.</p>
	 */
	SubmitNodeCollector submitNodes();

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
