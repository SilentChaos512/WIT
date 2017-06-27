package net.silentchaos512.wit.compat.multipart;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.wit.client.HudRenderObject;

@SideOnly(Side.CLIENT)
public class MCMultiPartHandler {

  public static HudRenderObject getForMultiPart() {

    // FIXME

//    RayTraceResult hit = Minecraft.getMinecraft().objectMouseOver;
//    if (hit != null && hit instanceof PartMOP) {
//      PartMOP mop = (PartMOP) hit;
//      if (mop.partHit != null) {
//        return new HudRenderObject(new MultipartInfo(mop));
//      }
//    }

    return null;
  }
}
