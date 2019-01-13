package net.silentchaos512.wit.compat.multipart;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.wit.api.IInfoObject;

import java.util.List;

// FIXME
public class MultipartInfo implements IInfoObject {

//  @Nonnull
//  protected final PartMOP mop;
//  @Nonnull
//  protected final IBlockState state;
//
//  public MultipartInfo(PartMOP mop) {
//
//    this.mop = mop;
//    state = mop.partHit
//        .getExtendedState(MultipartRegistry.getDefaultState(mop.partHit).getBaseState());
//  }

  @Override
  public void addLines(EntityPlayer player, List<ITextComponent> list) {

//    ModContainer mod = null;
//
//    // list.add(state.toString() + ", " + state.getProperties().entrySet().size()); // mcmultipart:multipart
//
//    for (ItemStack stack : mop.partHit.getDrops()) {
//      if (stack != null) {
//        if (mod == null) {
//          mod = ObjectInfo.getModFromItem(stack);
//        }
//
//        String line = Config.hudObjectName.shouldDisplay(player)
//            ? stack.getRarity().rarityColor + stack.getDisplayName() : "";
//        TileEntity tile = player.world.getTileEntity(mop.getBlockPos());
//        if (tile != null) {
//          line += Config.hudTileEntity.formatString(" (TE)");
//        }
//
//        list.add(line);
//      }
//    }
//
//    if (Config.hudResourceName.shouldDisplay(player)) {
//      String line = mop.partHit.getType().toString();
//      list.add(Config.hudResourceName.formatString(line)); // covers:corner, etc.
//    }
//
//    for (Entry<IProperty<?>, Comparable<?>> entry : state.getProperties().entrySet()) {
//      String str = "%s, %s";
//      list.add(String.format(str, entry.getValue().toString(), entry.getValue()));
//    }
//
//    if (Config.hudModName.shouldDisplay(player)) {
//      String modName = mod != null ? mod.getName() : "Minecraft";
//      list.add(Config.hudModName.formatString(modName));
//    }
  }
}
