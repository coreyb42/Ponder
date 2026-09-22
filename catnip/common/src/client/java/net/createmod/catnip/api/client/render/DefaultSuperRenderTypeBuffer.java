package net.createmod.catnip.api.client.render;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/** Frame-local custom geometry collector backed by Minecraft's 26.2 render graph. */
public final class DefaultSuperRenderTypeBuffer implements SuperRenderTypeBuffer {
	private final SubmitNodeCollector submitNodes;
	private final SuperRenderTypeBufferPhase earlyBuffer = new SuperRenderTypeBufferPhase();
	private final SuperRenderTypeBufferPhase defaultBuffer = new SuperRenderTypeBufferPhase();
	private final SuperRenderTypeBufferPhase lateBuffer = new SuperRenderTypeBufferPhase();

	public DefaultSuperRenderTypeBuffer(SubmitNodeCollector submitNodes) {
		this.submitNodes = submitNodes;
	}

	@Override public SubmitNodeCollector submitNodes() { return submitNodes; }
	@Override public VertexConsumer getEarlyBuffer(RenderType type) { return earlyBuffer.get(type); }
	@Override public VertexConsumer getBuffer(RenderType type) { return defaultBuffer.get(type); }
	@Override public VertexConsumer getLateBuffer(RenderType type) { return lateBuffer.get(type); }

	@Override
	public void draw() {
		earlyBuffer.submit(submitNodes);
		defaultBuffer.submit(submitNodes);
		lateBuffer.submit(submitNodes);
	}

	@Override
	public void draw(RenderType type) {
		earlyBuffer.submit(submitNodes, type);
		defaultBuffer.submit(submitNodes, type);
		lateBuffer.submit(submitNodes, type);
	}

	public static final class SuperRenderTypeBufferPhase {
		private final Map<RenderType, DeferredVertexConsumer> buffers = new IdentityHashMap<>();

		private VertexConsumer get(RenderType type) {
			return buffers.computeIfAbsent(type, $ -> new DeferredVertexConsumer());
		}

		private void submit(SubmitNodeCollector submitNodes) {
			buffers.forEach((type, vertices) -> submit(submitNodes, type, vertices));
			buffers.clear();
		}

		private void submit(SubmitNodeCollector submitNodes, RenderType type) {
			DeferredVertexConsumer vertices = buffers.remove(type);
			if (vertices != null)
				submit(submitNodes, type, vertices);
		}

		private static void submit(SubmitNodeCollector submitNodes, RenderType type, DeferredVertexConsumer vertices) {
			if (!vertices.isEmpty())
				submitNodes.submitCustomGeometry(new PoseStack(), type, (pose, output) -> vertices.replay(output));
		}
	}

	/** Records fully transformed vertex attributes before the render graph consumes them. */
	private static final class DeferredVertexConsumer implements VertexConsumer {
		private final List<Vertex> vertices = new ArrayList<>();
		private Vertex current;

		boolean isEmpty() { return vertices.isEmpty(); }

		void replay(VertexConsumer output) {
			for (Vertex vertex : vertices) {
				output.addVertex(vertex.x, vertex.y, vertex.z)
					.setColor(vertex.r, vertex.g, vertex.b, vertex.a)
					.setUv(vertex.u, vertex.v)
					.setUv1(vertex.overlayU, vertex.overlayV)
					.setUv2(vertex.lightU, vertex.lightV)
					.setNormal(vertex.nx, vertex.ny, vertex.nz)
					.setLineWidth(vertex.lineWidth);
			}
		}

		@Override public VertexConsumer addVertex(float x, float y, float z) { current = new Vertex(x, y, z); vertices.add(current); return this; }
		@Override public VertexConsumer setColor(int r, int g, int b, int a) { current.r = r; current.g = g; current.b = b; current.a = a; return this; }
		@Override public VertexConsumer setColor(int color) { return setColor(color >> 16 & 255, color >> 8 & 255, color & 255, color >>> 24); }
		@Override public VertexConsumer setUv(float u, float v) { current.u = u; current.v = v; return this; }
		@Override public VertexConsumer setUv1(int u, int v) { current.overlayU = u; current.overlayV = v; return this; }
		@Override public VertexConsumer setUv2(int u, int v) { current.lightU = u; current.lightV = v; return this; }
		@Override public VertexConsumer setNormal(float x, float y, float z) { current.nx = x; current.ny = y; current.nz = z; return this; }
		@Override public VertexConsumer setLineWidth(float width) { current.lineWidth = width; return this; }
	}

	private static final class Vertex {
		final float x, y, z;
		int r = 255, g = 255, b = 255, a = 255;
		float u, v;
		int overlayU, overlayV, lightU, lightV;
		float nx, ny, nz, lineWidth = 1;

		Vertex(float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
	}
}
