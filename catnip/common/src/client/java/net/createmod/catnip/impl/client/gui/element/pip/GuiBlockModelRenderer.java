package net.createmod.catnip.impl.client.gui.element.pip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.createmod.catnip.api.client.gui.render.pip.GuiBlockModelRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class GuiBlockModelRenderer extends PictureInPictureRenderer<GuiBlockModelRenderState> {
	@Override
	public Class<GuiBlockModelRenderState> getRenderStateClass() {
		return GuiBlockModelRenderState.class;
	}

	@Override
	protected void renderToTexture(GuiBlockModelRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
		var model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(renderState.state());
		List<BlockStateModelPart> parts = new ArrayList<>();
		model.collectParts(RandomSource.create(renderState.state().getSeed(net.minecraft.core.BlockPos.ZERO)), parts);

		int[] tints = new int[32];
		Arrays.fill(tints, renderState.color());
		RenderType type = model.hasMaterialFlag(BakedQuad.FLAG_TRANSLUCENT)
			? Sheets.translucentBlockItemSheet()
			: Sheets.cutoutBlockItemSheet();
		submitNodeCollector.submitBlockModel(poseStack, type, parts, tints,
			LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
	}

	@Override
	protected String getTextureLabel() {
		return "catnip:gui_block_model";
	}
}
