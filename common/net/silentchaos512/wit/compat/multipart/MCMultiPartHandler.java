package net.silentchaos512.wit.compat.multipart;

import mcmultipart.multipart.MultipartRegistry;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.BlockInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.info.BlockStackInfo;

@SideOnly(Side.CLIENT)
public class MCMultiPartHandler {

  public static HudRenderObject getForMultiPart() {

    RayTraceResult hit = Minecraft.getMinecraft().objectMouseOver;
    if (hit != null && hit instanceof PartMOP) {
      PartMOP mop = (PartMOP) hit;
      if (mop.partHit != null) {
        return new HudRenderObject(new MultipartInfo(mop));
      }
    }

    return null;
  }
}
