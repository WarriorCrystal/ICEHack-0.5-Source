//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.util;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlabNew;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.ItemModelMesherForge;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.pipeline.LightUtil;

public class ICERenderItem implements IResourceManagerReloadListener {
  private void draw(BufferBuilder paramBufferBuilder, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) {
    paramBufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
    paramBufferBuilder.pos((paramInt1 + 0), (paramInt2 + 0), 0.0D).color(paramInt5, paramInt6, paramInt7, paramInt8).endVertex();
    paramBufferBuilder.pos((paramInt1 + 0), (paramInt2 + paramInt4), 0.0D).color(paramInt5, paramInt6, paramInt7, paramInt8).endVertex();
    paramBufferBuilder.pos((paramInt1 + paramInt3), (paramInt2 + paramInt4), 0.0D).color(paramInt5, paramInt6, paramInt7, paramInt8).endVertex();
    paramBufferBuilder.pos((paramInt1 + paramInt3), (paramInt2 + 0), 0.0D).color(paramInt5, paramInt6, paramInt7, paramInt8).endVertex();
    Tessellator.getInstance().draw();
  }
  
  private void registerBlock(Block paramBlock, String paramString) {
    registerBlock(paramBlock, 0, paramString);
  }
  
  public ItemModelMesher getItemModelMesher() {
    return this.itemModelMesher;
  }
  
  private void registerItem(Item paramItem, String paramString) {
    registerItem(paramItem, 0, paramString);
  }
  
  public void renderItemAndEffectIntoGUI(ItemStack paramItemStack, int paramInt1, int paramInt2) {
    renderItemAndEffectIntoGUI((EntityLivingBase)(Minecraft.getMinecraft()).player, paramItemStack, paramInt1, paramInt2);
  }
  
  private void renderQuad(BufferBuilder paramBufferBuilder, BakedQuad paramBakedQuad, int paramInt) {
    paramBufferBuilder.addVertexData(paramBakedQuad.getVertexData());
    paramBufferBuilder.putColor4(paramInt);
    putQuadNormal(paramBufferBuilder, paramBakedQuad);
  }
  
  public void renderItem(ItemStack paramItemStack, IBakedModel paramIBakedModel) {
    if (!paramItemStack.isEmpty()) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      if (paramIBakedModel.isBuiltInRenderer()) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        TileEntityItemStackRenderer.instance.renderByItem(paramItemStack);
      } else {
        renderModel(paramIBakedModel, paramItemStack);
        if (paramItemStack.hasEffect())
          renderEffect(paramIBakedModel); 
      } 
      GlStateManager.popMatrix();
    } 
  }
  
  public void renderItem(ItemStack paramItemStack, ItemCameraTransforms.TransformType paramTransformType) {
    if (!paramItemStack.isEmpty()) {
      IBakedModel iBakedModel = getItemModelWithOverrides(paramItemStack, (World)null, (EntityLivingBase)null);
      renderItemModel(paramItemStack, iBakedModel, paramTransformType, false);
    } 
  }
  
  public boolean shouldRenderItemIn3D(ItemStack paramItemStack) {
    IBakedModel iBakedModel = this.itemModelMesher.getItemModel(paramItemStack);
    return (iBakedModel == null) ? false : iBakedModel.isGui3d();
  }
  
  public void renderItemIntoGUI(ItemStack paramItemStack, int paramInt1, int paramInt2) {
    renderItemModelIntoGUI(paramItemStack, paramInt1, paramInt2, getItemModelWithOverrides(paramItemStack, (World)null, (EntityLivingBase)null));
  }
  
  private void putQuadNormal(BufferBuilder paramBufferBuilder, BakedQuad paramBakedQuad) {
    Vec3i vec3i = paramBakedQuad.getFace().getDirectionVec();
    paramBufferBuilder.putNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
  }
  
  private void registerItems() {
    registerBlock(Blocks.ANVIL, "anvil_intact");
    registerBlock(Blocks.ANVIL, 1, "anvil_slightly_damaged");
    registerBlock(Blocks.ANVIL, 2, "anvil_very_damaged");
    registerBlock(Blocks.CARPET, EnumDyeColor.BLACK.getMetadata(), "black_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.BLUE.getMetadata(), "blue_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.BROWN.getMetadata(), "brown_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.CYAN.getMetadata(), "cyan_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.GRAY.getMetadata(), "gray_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.GREEN.getMetadata(), "green_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.LIGHT_BLUE.getMetadata(), "light_blue_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.LIME.getMetadata(), "lime_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.MAGENTA.getMetadata(), "magenta_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.ORANGE.getMetadata(), "orange_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.PINK.getMetadata(), "pink_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.PURPLE.getMetadata(), "purple_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.RED.getMetadata(), "red_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.SILVER.getMetadata(), "silver_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.WHITE.getMetadata(), "white_carpet");
    registerBlock(Blocks.CARPET, EnumDyeColor.YELLOW.getMetadata(), "yellow_carpet");
    registerBlock(Blocks.COBBLESTONE_WALL, BlockWall.EnumType.MOSSY.getMetadata(), "mossy_cobblestone_wall");
    registerBlock(Blocks.COBBLESTONE_WALL, BlockWall.EnumType.NORMAL.getMetadata(), "cobblestone_wall");
    registerBlock(Blocks.DIRT, BlockDirt.DirtType.COARSE_DIRT.getMetadata(), "coarse_dirt");
    registerBlock(Blocks.DIRT, BlockDirt.DirtType.DIRT.getMetadata(), "dirt");
    registerBlock(Blocks.DIRT, BlockDirt.DirtType.PODZOL.getMetadata(), "podzol");
    registerBlock((Block)Blocks.DOUBLE_PLANT, BlockDoublePlant.EnumPlantType.FERN.getMeta(), "double_fern");
    registerBlock((Block)Blocks.DOUBLE_PLANT, BlockDoublePlant.EnumPlantType.GRASS.getMeta(), "double_grass");
    registerBlock((Block)Blocks.DOUBLE_PLANT, BlockDoublePlant.EnumPlantType.PAEONIA.getMeta(), "paeonia");
    registerBlock((Block)Blocks.DOUBLE_PLANT, BlockDoublePlant.EnumPlantType.ROSE.getMeta(), "double_rose");
    registerBlock((Block)Blocks.DOUBLE_PLANT, BlockDoublePlant.EnumPlantType.SUNFLOWER.getMeta(), "sunflower");
    registerBlock((Block)Blocks.DOUBLE_PLANT, BlockDoublePlant.EnumPlantType.SYRINGA.getMeta(), "syringa");
    registerBlock((Block)Blocks.LEAVES, BlockPlanks.EnumType.BIRCH.getMetadata(), "birch_leaves");
    registerBlock((Block)Blocks.LEAVES, BlockPlanks.EnumType.JUNGLE.getMetadata(), "jungle_leaves");
    registerBlock((Block)Blocks.LEAVES, BlockPlanks.EnumType.OAK.getMetadata(), "oak_leaves");
    registerBlock((Block)Blocks.LEAVES, BlockPlanks.EnumType.SPRUCE.getMetadata(), "spruce_leaves");
    registerBlock((Block)Blocks.LEAVES2, BlockPlanks.EnumType.ACACIA.getMetadata() - 4, "acacia_leaves");
    registerBlock((Block)Blocks.LEAVES2, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4, "dark_oak_leaves");
    registerBlock(Blocks.LOG, BlockPlanks.EnumType.BIRCH.getMetadata(), "birch_log");
    registerBlock(Blocks.LOG, BlockPlanks.EnumType.JUNGLE.getMetadata(), "jungle_log");
    registerBlock(Blocks.LOG, BlockPlanks.EnumType.OAK.getMetadata(), "oak_log");
    registerBlock(Blocks.LOG, BlockPlanks.EnumType.SPRUCE.getMetadata(), "spruce_log");
    registerBlock(Blocks.LOG2, BlockPlanks.EnumType.ACACIA.getMetadata() - 4, "acacia_log");
    registerBlock(Blocks.LOG2, BlockPlanks.EnumType.DARK_OAK.getMetadata() - 4, "dark_oak_log");
    registerBlock(Blocks.MONSTER_EGG, BlockSilverfish.EnumType.CHISELED_STONEBRICK.getMetadata(), "chiseled_brick_monster_egg");
    registerBlock(Blocks.MONSTER_EGG, BlockSilverfish.EnumType.COBBLESTONE.getMetadata(), "cobblestone_monster_egg");
    registerBlock(Blocks.MONSTER_EGG, BlockSilverfish.EnumType.CRACKED_STONEBRICK.getMetadata(), "cracked_brick_monster_egg");
    registerBlock(Blocks.MONSTER_EGG, BlockSilverfish.EnumType.MOSSY_STONEBRICK.getMetadata(), "mossy_brick_monster_egg");
    registerBlock(Blocks.MONSTER_EGG, BlockSilverfish.EnumType.STONE.getMetadata(), "stone_monster_egg");
    registerBlock(Blocks.MONSTER_EGG, BlockSilverfish.EnumType.STONEBRICK.getMetadata(), "stone_brick_monster_egg");
    registerBlock(Blocks.PLANKS, BlockPlanks.EnumType.ACACIA.getMetadata(), "acacia_planks");
    registerBlock(Blocks.PLANKS, BlockPlanks.EnumType.BIRCH.getMetadata(), "birch_planks");
    registerBlock(Blocks.PLANKS, BlockPlanks.EnumType.DARK_OAK.getMetadata(), "dark_oak_planks");
    registerBlock(Blocks.PLANKS, BlockPlanks.EnumType.JUNGLE.getMetadata(), "jungle_planks");
    registerBlock(Blocks.PLANKS, BlockPlanks.EnumType.OAK.getMetadata(), "oak_planks");
    registerBlock(Blocks.PLANKS, BlockPlanks.EnumType.SPRUCE.getMetadata(), "spruce_planks");
    registerBlock(Blocks.PRISMARINE, BlockPrismarine.EnumType.BRICKS.getMetadata(), "prismarine_bricks");
    registerBlock(Blocks.PRISMARINE, BlockPrismarine.EnumType.DARK.getMetadata(), "dark_prismarine");
    registerBlock(Blocks.PRISMARINE, BlockPrismarine.EnumType.ROUGH.getMetadata(), "prismarine");
    registerBlock(Blocks.QUARTZ_BLOCK, BlockQuartz.EnumType.CHISELED.getMetadata(), "chiseled_quartz_block");
    registerBlock(Blocks.QUARTZ_BLOCK, BlockQuartz.EnumType.DEFAULT.getMetadata(), "quartz_block");
    registerBlock(Blocks.QUARTZ_BLOCK, BlockQuartz.EnumType.LINES_Y.getMetadata(), "quartz_column");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.ALLIUM.getMeta(), "allium");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.BLUE_ORCHID.getMeta(), "blue_orchid");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.HOUSTONIA.getMeta(), "houstonia");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.ORANGE_TULIP.getMeta(), "orange_tulip");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.OXEYE_DAISY.getMeta(), "oxeye_daisy");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.PINK_TULIP.getMeta(), "pink_tulip");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.POPPY.getMeta(), "poppy");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.RED_TULIP.getMeta(), "red_tulip");
    registerBlock((Block)Blocks.RED_FLOWER, BlockFlower.EnumFlowerType.WHITE_TULIP.getMeta(), "white_tulip");
    registerBlock((Block)Blocks.SAND, BlockSand.EnumType.RED_SAND.getMetadata(), "red_sand");
    registerBlock((Block)Blocks.SAND, BlockSand.EnumType.SAND.getMetadata(), "sand");
    registerBlock(Blocks.SANDSTONE, BlockSandStone.EnumType.CHISELED.getMetadata(), "chiseled_sandstone");
    registerBlock(Blocks.SANDSTONE, BlockSandStone.EnumType.DEFAULT.getMetadata(), "sandstone");
    registerBlock(Blocks.SANDSTONE, BlockSandStone.EnumType.SMOOTH.getMetadata(), "smooth_sandstone");
    registerBlock(Blocks.RED_SANDSTONE, BlockRedSandstone.EnumType.CHISELED.getMetadata(), "chiseled_red_sandstone");
    registerBlock(Blocks.RED_SANDSTONE, BlockRedSandstone.EnumType.DEFAULT.getMetadata(), "red_sandstone");
    registerBlock(Blocks.RED_SANDSTONE, BlockRedSandstone.EnumType.SMOOTH.getMetadata(), "smooth_red_sandstone");
    registerBlock(Blocks.SAPLING, BlockPlanks.EnumType.ACACIA.getMetadata(), "acacia_sapling");
    registerBlock(Blocks.SAPLING, BlockPlanks.EnumType.BIRCH.getMetadata(), "birch_sapling");
    registerBlock(Blocks.SAPLING, BlockPlanks.EnumType.DARK_OAK.getMetadata(), "dark_oak_sapling");
    registerBlock(Blocks.SAPLING, BlockPlanks.EnumType.JUNGLE.getMetadata(), "jungle_sapling");
    registerBlock(Blocks.SAPLING, BlockPlanks.EnumType.OAK.getMetadata(), "oak_sapling");
    registerBlock(Blocks.SAPLING, BlockPlanks.EnumType.SPRUCE.getMetadata(), "spruce_sapling");
    registerBlock(Blocks.SPONGE, 0, "sponge");
    registerBlock(Blocks.SPONGE, 1, "sponge_wet");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.BLACK.getMetadata(), "black_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.BLUE.getMetadata(), "blue_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.BROWN.getMetadata(), "brown_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.CYAN.getMetadata(), "cyan_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.GRAY.getMetadata(), "gray_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.GREEN.getMetadata(), "green_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.LIGHT_BLUE.getMetadata(), "light_blue_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.LIME.getMetadata(), "lime_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.MAGENTA.getMetadata(), "magenta_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.ORANGE.getMetadata(), "orange_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.PINK.getMetadata(), "pink_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.PURPLE.getMetadata(), "purple_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.RED.getMetadata(), "red_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.SILVER.getMetadata(), "silver_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.WHITE.getMetadata(), "white_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS, EnumDyeColor.YELLOW.getMetadata(), "yellow_stained_glass");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.BLACK.getMetadata(), "black_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.BLUE.getMetadata(), "blue_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.BROWN.getMetadata(), "brown_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.CYAN.getMetadata(), "cyan_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.GRAY.getMetadata(), "gray_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.GREEN.getMetadata(), "green_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.LIGHT_BLUE.getMetadata(), "light_blue_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.LIME.getMetadata(), "lime_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.MAGENTA.getMetadata(), "magenta_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.ORANGE.getMetadata(), "orange_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.PINK.getMetadata(), "pink_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.PURPLE.getMetadata(), "purple_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.RED.getMetadata(), "red_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.SILVER.getMetadata(), "silver_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.WHITE.getMetadata(), "white_stained_glass_pane");
    registerBlock((Block)Blocks.STAINED_GLASS_PANE, EnumDyeColor.YELLOW.getMetadata(), "yellow_stained_glass_pane");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.BLACK.getMetadata(), "black_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.BLUE.getMetadata(), "blue_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.BROWN.getMetadata(), "brown_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.CYAN.getMetadata(), "cyan_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.GRAY.getMetadata(), "gray_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.GREEN.getMetadata(), "green_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.LIGHT_BLUE.getMetadata(), "light_blue_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.LIME.getMetadata(), "lime_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.MAGENTA.getMetadata(), "magenta_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.ORANGE.getMetadata(), "orange_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.PINK.getMetadata(), "pink_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.PURPLE.getMetadata(), "purple_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.RED.getMetadata(), "red_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.SILVER.getMetadata(), "silver_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.WHITE.getMetadata(), "white_stained_hardened_clay");
    registerBlock(Blocks.STAINED_HARDENED_CLAY, EnumDyeColor.YELLOW.getMetadata(), "yellow_stained_hardened_clay");
    registerBlock(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata(), "andesite");
    registerBlock(Blocks.STONE, BlockStone.EnumType.ANDESITE_SMOOTH.getMetadata(), "andesite_smooth");
    registerBlock(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata(), "diorite");
    registerBlock(Blocks.STONE, BlockStone.EnumType.DIORITE_SMOOTH.getMetadata(), "diorite_smooth");
    registerBlock(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata(), "granite");
    registerBlock(Blocks.STONE, BlockStone.EnumType.GRANITE_SMOOTH.getMetadata(), "granite_smooth");
    registerBlock(Blocks.STONE, BlockStone.EnumType.STONE.getMetadata(), "stone");
    registerBlock(Blocks.STONEBRICK, BlockStoneBrick.EnumType.CRACKED.getMetadata(), "cracked_stonebrick");
    registerBlock(Blocks.STONEBRICK, BlockStoneBrick.EnumType.DEFAULT.getMetadata(), "stonebrick");
    registerBlock(Blocks.STONEBRICK, BlockStoneBrick.EnumType.CHISELED.getMetadata(), "chiseled_stonebrick");
    registerBlock(Blocks.STONEBRICK, BlockStoneBrick.EnumType.MOSSY.getMetadata(), "mossy_stonebrick");
    registerBlock((Block)Blocks.STONE_SLAB, BlockStoneSlab.EnumType.BRICK.getMetadata(), "brick_slab");
    registerBlock((Block)Blocks.STONE_SLAB, BlockStoneSlab.EnumType.COBBLESTONE.getMetadata(), "cobblestone_slab");
    registerBlock((Block)Blocks.STONE_SLAB, BlockStoneSlab.EnumType.WOOD.getMetadata(), "old_wood_slab");
    registerBlock((Block)Blocks.STONE_SLAB, BlockStoneSlab.EnumType.NETHERBRICK.getMetadata(), "nether_brick_slab");
    registerBlock((Block)Blocks.STONE_SLAB, BlockStoneSlab.EnumType.QUARTZ.getMetadata(), "quartz_slab");
    registerBlock((Block)Blocks.STONE_SLAB, BlockStoneSlab.EnumType.SAND.getMetadata(), "sandstone_slab");
    registerBlock((Block)Blocks.STONE_SLAB, BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata(), "stone_brick_slab");
    registerBlock((Block)Blocks.STONE_SLAB, BlockStoneSlab.EnumType.STONE.getMetadata(), "stone_slab");
    registerBlock((Block)Blocks.STONE_SLAB2, BlockStoneSlabNew.EnumType.RED_SANDSTONE.getMetadata(), "red_sandstone_slab");
    registerBlock((Block)Blocks.TALLGRASS, BlockTallGrass.EnumType.DEAD_BUSH.getMeta(), "dead_bush");
    registerBlock((Block)Blocks.TALLGRASS, BlockTallGrass.EnumType.FERN.getMeta(), "fern");
    registerBlock((Block)Blocks.TALLGRASS, BlockTallGrass.EnumType.GRASS.getMeta(), "tall_grass");
    registerBlock((Block)Blocks.WOODEN_SLAB, BlockPlanks.EnumType.ACACIA.getMetadata(), "acacia_slab");
    registerBlock((Block)Blocks.WOODEN_SLAB, BlockPlanks.EnumType.BIRCH.getMetadata(), "birch_slab");
    registerBlock((Block)Blocks.WOODEN_SLAB, BlockPlanks.EnumType.DARK_OAK.getMetadata(), "dark_oak_slab");
    registerBlock((Block)Blocks.WOODEN_SLAB, BlockPlanks.EnumType.JUNGLE.getMetadata(), "jungle_slab");
    registerBlock((Block)Blocks.WOODEN_SLAB, BlockPlanks.EnumType.OAK.getMetadata(), "oak_slab");
    registerBlock((Block)Blocks.WOODEN_SLAB, BlockPlanks.EnumType.SPRUCE.getMetadata(), "spruce_slab");
    registerBlock(Blocks.WOOL, EnumDyeColor.BLACK.getMetadata(), "black_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.BLUE.getMetadata(), "blue_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.BROWN.getMetadata(), "brown_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.CYAN.getMetadata(), "cyan_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.GRAY.getMetadata(), "gray_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.GREEN.getMetadata(), "green_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.LIGHT_BLUE.getMetadata(), "light_blue_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.LIME.getMetadata(), "lime_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.MAGENTA.getMetadata(), "magenta_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.ORANGE.getMetadata(), "orange_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.PINK.getMetadata(), "pink_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.PURPLE.getMetadata(), "purple_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.RED.getMetadata(), "red_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.SILVER.getMetadata(), "silver_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.WHITE.getMetadata(), "white_wool");
    registerBlock(Blocks.WOOL, EnumDyeColor.YELLOW.getMetadata(), "yellow_wool");
    registerBlock(Blocks.FARMLAND, "farmland");
    registerBlock(Blocks.ACACIA_STAIRS, "acacia_stairs");
    registerBlock(Blocks.ACTIVATOR_RAIL, "activator_rail");
    registerBlock((Block)Blocks.BEACON, "beacon");
    registerBlock(Blocks.BEDROCK, "bedrock");
    registerBlock(Blocks.BIRCH_STAIRS, "birch_stairs");
    registerBlock(Blocks.BOOKSHELF, "bookshelf");
    registerBlock(Blocks.BRICK_BLOCK, "brick_block");
    registerBlock(Blocks.BRICK_BLOCK, "brick_block");
    registerBlock(Blocks.BRICK_STAIRS, "brick_stairs");
    registerBlock((Block)Blocks.BROWN_MUSHROOM, "brown_mushroom");
    registerBlock((Block)Blocks.CACTUS, "cactus");
    registerBlock(Blocks.CLAY, "clay");
    registerBlock(Blocks.COAL_BLOCK, "coal_block");
    registerBlock(Blocks.COAL_ORE, "coal_ore");
    registerBlock(Blocks.COBBLESTONE, "cobblestone");
    registerBlock(Blocks.CRAFTING_TABLE, "crafting_table");
    registerBlock(Blocks.DARK_OAK_STAIRS, "dark_oak_stairs");
    registerBlock((Block)Blocks.DAYLIGHT_DETECTOR, "daylight_detector");
    registerBlock((Block)Blocks.DEADBUSH, "dead_bush");
    registerBlock(Blocks.DETECTOR_RAIL, "detector_rail");
    registerBlock(Blocks.DIAMOND_BLOCK, "diamond_block");
    registerBlock(Blocks.DIAMOND_ORE, "diamond_ore");
    registerBlock(Blocks.DISPENSER, "dispenser");
    registerBlock(Blocks.DROPPER, "dropper");
    registerBlock(Blocks.EMERALD_BLOCK, "emerald_block");
    registerBlock(Blocks.EMERALD_ORE, "emerald_ore");
    registerBlock(Blocks.ENCHANTING_TABLE, "enchanting_table");
    registerBlock(Blocks.END_PORTAL_FRAME, "end_portal_frame");
    registerBlock(Blocks.END_STONE, "end_stone");
    registerBlock(Blocks.OAK_FENCE, "oak_fence");
    registerBlock(Blocks.SPRUCE_FENCE, "spruce_fence");
    registerBlock(Blocks.BIRCH_FENCE, "birch_fence");
    registerBlock(Blocks.JUNGLE_FENCE, "jungle_fence");
    registerBlock(Blocks.DARK_OAK_FENCE, "dark_oak_fence");
    registerBlock(Blocks.ACACIA_FENCE, "acacia_fence");
    registerBlock(Blocks.OAK_FENCE_GATE, "oak_fence_gate");
    registerBlock(Blocks.SPRUCE_FENCE_GATE, "spruce_fence_gate");
    registerBlock(Blocks.BIRCH_FENCE_GATE, "birch_fence_gate");
    registerBlock(Blocks.JUNGLE_FENCE_GATE, "jungle_fence_gate");
    registerBlock(Blocks.DARK_OAK_FENCE_GATE, "dark_oak_fence_gate");
    registerBlock(Blocks.ACACIA_FENCE_GATE, "acacia_fence_gate");
    registerBlock(Blocks.FURNACE, "furnace");
    registerBlock(Blocks.GLASS, "glass");
    registerBlock(Blocks.GLASS_PANE, "glass_pane");
    registerBlock(Blocks.GLOWSTONE, "glowstone");
    registerBlock(Blocks.GOLDEN_RAIL, "golden_rail");
    registerBlock(Blocks.GOLD_BLOCK, "gold_block");
    registerBlock(Blocks.GOLD_ORE, "gold_ore");
    registerBlock((Block)Blocks.GRASS, "grass");
    registerBlock(Blocks.GRASS_PATH, "grass_path");
    registerBlock(Blocks.GRAVEL, "gravel");
    registerBlock(Blocks.HARDENED_CLAY, "hardened_clay");
    registerBlock(Blocks.HAY_BLOCK, "hay_block");
    registerBlock(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, "heavy_weighted_pressure_plate");
    registerBlock((Block)Blocks.HOPPER, "hopper");
    registerBlock(Blocks.ICE, "ice");
    registerBlock(Blocks.IRON_BARS, "iron_bars");
    registerBlock(Blocks.IRON_BLOCK, "iron_block");
    registerBlock(Blocks.IRON_ORE, "iron_ore");
    registerBlock(Blocks.IRON_TRAPDOOR, "iron_trapdoor");
    registerBlock(Blocks.JUKEBOX, "jukebox");
    registerBlock(Blocks.JUNGLE_STAIRS, "jungle_stairs");
    registerBlock(Blocks.LADDER, "ladder");
    registerBlock(Blocks.LAPIS_BLOCK, "lapis_block");
    registerBlock(Blocks.LAPIS_ORE, "lapis_ore");
    registerBlock(Blocks.LEVER, "lever");
    registerBlock(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, "light_weighted_pressure_plate");
    registerBlock(Blocks.LIT_PUMPKIN, "lit_pumpkin");
    registerBlock(Blocks.MELON_BLOCK, "melon_block");
    registerBlock(Blocks.MOSSY_COBBLESTONE, "mossy_cobblestone");
    registerBlock((Block)Blocks.MYCELIUM, "mycelium");
    registerBlock(Blocks.NETHERRACK, "netherrack");
    registerBlock(Blocks.NETHER_BRICK, "nether_brick");
    registerBlock(Blocks.NETHER_BRICK_FENCE, "nether_brick_fence");
    registerBlock(Blocks.NETHER_BRICK_STAIRS, "nether_brick_stairs");
    registerBlock(Blocks.NOTEBLOCK, "noteblock");
    registerBlock(Blocks.OAK_STAIRS, "oak_stairs");
    registerBlock(Blocks.OBSIDIAN, "obsidian");
    registerBlock(Blocks.PACKED_ICE, "packed_ice");
    registerBlock((Block)Blocks.PISTON, "piston");
    registerBlock(Blocks.PUMPKIN, "pumpkin");
    registerBlock(Blocks.QUARTZ_ORE, "quartz_ore");
    registerBlock(Blocks.QUARTZ_STAIRS, "quartz_stairs");
    registerBlock(Blocks.RAIL, "rail");
    registerBlock(Blocks.REDSTONE_BLOCK, "redstone_block");
    registerBlock(Blocks.REDSTONE_LAMP, "redstone_lamp");
    registerBlock(Blocks.REDSTONE_ORE, "redstone_ore");
    registerBlock(Blocks.REDSTONE_TORCH, "redstone_torch");
    registerBlock((Block)Blocks.RED_MUSHROOM, "red_mushroom");
    registerBlock(Blocks.SANDSTONE_STAIRS, "sandstone_stairs");
    registerBlock(Blocks.RED_SANDSTONE_STAIRS, "red_sandstone_stairs");
    registerBlock(Blocks.SEA_LANTERN, "sea_lantern");
    registerBlock(Blocks.SLIME_BLOCK, "slime");
    registerBlock(Blocks.SNOW, "snow");
    registerBlock(Blocks.SNOW_LAYER, "snow_layer");
    registerBlock(Blocks.SOUL_SAND, "soul_sand");
    registerBlock(Blocks.SPRUCE_STAIRS, "spruce_stairs");
    registerBlock((Block)Blocks.STICKY_PISTON, "sticky_piston");
    registerBlock(Blocks.STONE_BRICK_STAIRS, "stone_brick_stairs");
    registerBlock(Blocks.STONE_BUTTON, "stone_button");
    registerBlock(Blocks.STONE_PRESSURE_PLATE, "stone_pressure_plate");
    registerBlock(Blocks.STONE_STAIRS, "stone_stairs");
    registerBlock(Blocks.TNT, "tnt");
    registerBlock(Blocks.TORCH, "torch");
    registerBlock(Blocks.TRAPDOOR, "trapdoor");
    registerBlock((Block)Blocks.TRIPWIRE_HOOK, "tripwire_hook");
    registerBlock(Blocks.VINE, "vine");
    registerBlock(Blocks.WATERLILY, "waterlily");
    registerBlock(Blocks.WEB, "web");
    registerBlock(Blocks.WOODEN_BUTTON, "wooden_button");
    registerBlock(Blocks.WOODEN_PRESSURE_PLATE, "wooden_pressure_plate");
    registerBlock((Block)Blocks.YELLOW_FLOWER, BlockFlower.EnumFlowerType.DANDELION.getMeta(), "dandelion");
    registerBlock(Blocks.END_ROD, "end_rod");
    registerBlock(Blocks.CHORUS_PLANT, "chorus_plant");
    registerBlock(Blocks.CHORUS_FLOWER, "chorus_flower");
    registerBlock(Blocks.PURPUR_BLOCK, "purpur_block");
    registerBlock(Blocks.PURPUR_PILLAR, "purpur_pillar");
    registerBlock(Blocks.PURPUR_STAIRS, "purpur_stairs");
    registerBlock((Block)Blocks.PURPUR_SLAB, "purpur_slab");
    registerBlock((Block)Blocks.PURPUR_DOUBLE_SLAB, "purpur_double_slab");
    registerBlock(Blocks.END_BRICKS, "end_bricks");
    registerBlock(Blocks.MAGMA, "magma");
    registerBlock(Blocks.NETHER_WART_BLOCK, "nether_wart_block");
    registerBlock(Blocks.RED_NETHER_BRICK, "red_nether_brick");
    registerBlock(Blocks.BONE_BLOCK, "bone_block");
    registerBlock(Blocks.STRUCTURE_VOID, "structure_void");
    registerBlock(Blocks.OBSERVER, "observer");
    registerBlock(Blocks.WHITE_SHULKER_BOX, "white_shulker_box");
    registerBlock(Blocks.ORANGE_SHULKER_BOX, "orange_shulker_box");
    registerBlock(Blocks.MAGENTA_SHULKER_BOX, "magenta_shulker_box");
    registerBlock(Blocks.LIGHT_BLUE_SHULKER_BOX, "light_blue_shulker_box");
    registerBlock(Blocks.YELLOW_SHULKER_BOX, "yellow_shulker_box");
    registerBlock(Blocks.LIME_SHULKER_BOX, "lime_shulker_box");
    registerBlock(Blocks.PINK_SHULKER_BOX, "pink_shulker_box");
    registerBlock(Blocks.GRAY_SHULKER_BOX, "gray_shulker_box");
    registerBlock(Blocks.SILVER_SHULKER_BOX, "silver_shulker_box");
    registerBlock(Blocks.CYAN_SHULKER_BOX, "cyan_shulker_box");
    registerBlock(Blocks.PURPLE_SHULKER_BOX, "purple_shulker_box");
    registerBlock(Blocks.BLUE_SHULKER_BOX, "blue_shulker_box");
    registerBlock(Blocks.BROWN_SHULKER_BOX, "brown_shulker_box");
    registerBlock(Blocks.GREEN_SHULKER_BOX, "green_shulker_box");
    registerBlock(Blocks.RED_SHULKER_BOX, "red_shulker_box");
    registerBlock(Blocks.BLACK_SHULKER_BOX, "black_shulker_box");
    registerBlock(Blocks.WHITE_GLAZED_TERRACOTTA, "white_glazed_terracotta");
    registerBlock(Blocks.ORANGE_GLAZED_TERRACOTTA, "orange_glazed_terracotta");
    registerBlock(Blocks.MAGENTA_GLAZED_TERRACOTTA, "magenta_glazed_terracotta");
    registerBlock(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, "light_blue_glazed_terracotta");
    registerBlock(Blocks.YELLOW_GLAZED_TERRACOTTA, "yellow_glazed_terracotta");
    registerBlock(Blocks.LIME_GLAZED_TERRACOTTA, "lime_glazed_terracotta");
    registerBlock(Blocks.PINK_GLAZED_TERRACOTTA, "pink_glazed_terracotta");
    registerBlock(Blocks.GRAY_GLAZED_TERRACOTTA, "gray_glazed_terracotta");
    registerBlock(Blocks.SILVER_GLAZED_TERRACOTTA, "silver_glazed_terracotta");
    registerBlock(Blocks.CYAN_GLAZED_TERRACOTTA, "cyan_glazed_terracotta");
    registerBlock(Blocks.PURPLE_GLAZED_TERRACOTTA, "purple_glazed_terracotta");
    registerBlock(Blocks.BLUE_GLAZED_TERRACOTTA, "blue_glazed_terracotta");
    registerBlock(Blocks.BROWN_GLAZED_TERRACOTTA, "brown_glazed_terracotta");
    registerBlock(Blocks.GREEN_GLAZED_TERRACOTTA, "green_glazed_terracotta");
    registerBlock(Blocks.RED_GLAZED_TERRACOTTA, "red_glazed_terracotta");
    registerBlock(Blocks.BLACK_GLAZED_TERRACOTTA, "black_glazed_terracotta");
    for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
      registerBlock(Blocks.CONCRETE, enumDyeColor.getMetadata(), String.valueOf((new StringBuilder()).append(enumDyeColor.getDyeColorName()).append("_concrete")));
      registerBlock(Blocks.CONCRETE_POWDER, enumDyeColor.getMetadata(), String.valueOf((new StringBuilder()).append(enumDyeColor.getDyeColorName()).append("_concrete_powder")));
    } 
    registerBlock((Block)Blocks.CHEST, "chest");
    registerBlock(Blocks.TRAPPED_CHEST, "trapped_chest");
    registerBlock(Blocks.ENDER_CHEST, "ender_chest");
    registerItem(Items.IRON_SHOVEL, "iron_shovel");
    registerItem(Items.IRON_PICKAXE, "iron_pickaxe");
    registerItem(Items.IRON_AXE, "iron_axe");
    registerItem(Items.FLINT_AND_STEEL, "flint_and_steel");
    registerItem(Items.APPLE, "apple");
    registerItem((Item)Items.BOW, "bow");
    registerItem(Items.ARROW, "arrow");
    registerItem(Items.SPECTRAL_ARROW, "spectral_arrow");
    registerItem(Items.TIPPED_ARROW, "tipped_arrow");
    registerItem(Items.COAL, 0, "coal");
    registerItem(Items.COAL, 1, "charcoal");
    registerItem(Items.DIAMOND, "diamond");
    registerItem(Items.IRON_INGOT, "iron_ingot");
    registerItem(Items.GOLD_INGOT, "gold_ingot");
    registerItem(Items.IRON_SWORD, "iron_sword");
    registerItem(Items.WOODEN_SWORD, "wooden_sword");
    registerItem(Items.WOODEN_SHOVEL, "wooden_shovel");
    registerItem(Items.WOODEN_PICKAXE, "wooden_pickaxe");
    registerItem(Items.WOODEN_AXE, "wooden_axe");
    registerItem(Items.STONE_SWORD, "stone_sword");
    registerItem(Items.STONE_SHOVEL, "stone_shovel");
    registerItem(Items.STONE_PICKAXE, "stone_pickaxe");
    registerItem(Items.STONE_AXE, "stone_axe");
    registerItem(Items.DIAMOND_SWORD, "diamond_sword");
    registerItem(Items.DIAMOND_SHOVEL, "diamond_shovel");
    registerItem(Items.DIAMOND_PICKAXE, "diamond_pickaxe");
    registerItem(Items.DIAMOND_AXE, "diamond_axe");
    registerItem(Items.STICK, "stick");
    registerItem(Items.BOWL, "bowl");
    registerItem(Items.MUSHROOM_STEW, "mushroom_stew");
    registerItem(Items.GOLDEN_SWORD, "golden_sword");
    registerItem(Items.GOLDEN_SHOVEL, "golden_shovel");
    registerItem(Items.GOLDEN_PICKAXE, "golden_pickaxe");
    registerItem(Items.GOLDEN_AXE, "golden_axe");
    registerItem(Items.STRING, "string");
    registerItem(Items.FEATHER, "feather");
    registerItem(Items.GUNPOWDER, "gunpowder");
    registerItem(Items.WOODEN_HOE, "wooden_hoe");
    registerItem(Items.STONE_HOE, "stone_hoe");
    registerItem(Items.IRON_HOE, "iron_hoe");
    registerItem(Items.DIAMOND_HOE, "diamond_hoe");
    registerItem(Items.GOLDEN_HOE, "golden_hoe");
    registerItem(Items.WHEAT_SEEDS, "wheat_seeds");
    registerItem(Items.WHEAT, "wheat");
    registerItem(Items.BREAD, "bread");
    registerItem((Item)Items.LEATHER_HELMET, "leather_helmet");
    registerItem((Item)Items.LEATHER_CHESTPLATE, "leather_chestplate");
    registerItem((Item)Items.LEATHER_LEGGINGS, "leather_leggings");
    registerItem((Item)Items.LEATHER_BOOTS, "leather_boots");
    registerItem((Item)Items.CHAINMAIL_HELMET, "chainmail_helmet");
    registerItem((Item)Items.CHAINMAIL_CHESTPLATE, "chainmail_chestplate");
    registerItem((Item)Items.CHAINMAIL_LEGGINGS, "chainmail_leggings");
    registerItem((Item)Items.CHAINMAIL_BOOTS, "chainmail_boots");
    registerItem((Item)Items.IRON_HELMET, "iron_helmet");
    registerItem((Item)Items.IRON_CHESTPLATE, "iron_chestplate");
    registerItem((Item)Items.IRON_LEGGINGS, "iron_leggings");
    registerItem((Item)Items.IRON_BOOTS, "iron_boots");
    registerItem((Item)Items.DIAMOND_HELMET, "diamond_helmet");
    registerItem((Item)Items.DIAMOND_CHESTPLATE, "diamond_chestplate");
    registerItem((Item)Items.DIAMOND_LEGGINGS, "diamond_leggings");
    registerItem((Item)Items.DIAMOND_BOOTS, "diamond_boots");
    registerItem((Item)Items.GOLDEN_HELMET, "golden_helmet");
    registerItem((Item)Items.GOLDEN_CHESTPLATE, "golden_chestplate");
    registerItem((Item)Items.GOLDEN_LEGGINGS, "golden_leggings");
    registerItem((Item)Items.GOLDEN_BOOTS, "golden_boots");
    registerItem(Items.FLINT, "flint");
    registerItem(Items.PORKCHOP, "porkchop");
    registerItem(Items.COOKED_PORKCHOP, "cooked_porkchop");
    registerItem(Items.PAINTING, "painting");
    registerItem(Items.GOLDEN_APPLE, "golden_apple");
    registerItem(Items.GOLDEN_APPLE, 1, "golden_apple");
    registerItem(Items.SIGN, "sign");
    registerItem(Items.OAK_DOOR, "oak_door");
    registerItem(Items.SPRUCE_DOOR, "spruce_door");
    registerItem(Items.BIRCH_DOOR, "birch_door");
    registerItem(Items.JUNGLE_DOOR, "jungle_door");
    registerItem(Items.ACACIA_DOOR, "acacia_door");
    registerItem(Items.DARK_OAK_DOOR, "dark_oak_door");
    registerItem(Items.BUCKET, "bucket");
    registerItem(Items.WATER_BUCKET, "water_bucket");
    registerItem(Items.LAVA_BUCKET, "lava_bucket");
    registerItem(Items.MINECART, "minecart");
    registerItem(Items.SADDLE, "saddle");
    registerItem(Items.IRON_DOOR, "iron_door");
    registerItem(Items.REDSTONE, "redstone");
    registerItem(Items.SNOWBALL, "snowball");
    registerItem(Items.BOAT, "oak_boat");
    registerItem(Items.SPRUCE_BOAT, "spruce_boat");
    registerItem(Items.BIRCH_BOAT, "birch_boat");
    registerItem(Items.JUNGLE_BOAT, "jungle_boat");
    registerItem(Items.ACACIA_BOAT, "acacia_boat");
    registerItem(Items.DARK_OAK_BOAT, "dark_oak_boat");
    registerItem(Items.LEATHER, "leather");
    registerItem(Items.MILK_BUCKET, "milk_bucket");
    registerItem(Items.BRICK, "brick");
    registerItem(Items.CLAY_BALL, "clay_ball");
    registerItem(Items.REEDS, "reeds");
    registerItem(Items.PAPER, "paper");
    registerItem(Items.BOOK, "book");
    registerItem(Items.SLIME_BALL, "slime_ball");
    registerItem(Items.CHEST_MINECART, "chest_minecart");
    registerItem(Items.FURNACE_MINECART, "furnace_minecart");
    registerItem(Items.EGG, "egg");
    registerItem(Items.COMPASS, "compass");
    registerItem((Item)Items.FISHING_ROD, "fishing_rod");
    registerItem(Items.CLOCK, "clock");
    registerItem(Items.GLOWSTONE_DUST, "glowstone_dust");
    registerItem(Items.FISH, ItemFishFood.FishType.COD.getMetadata(), "cod");
    registerItem(Items.FISH, ItemFishFood.FishType.SALMON.getMetadata(), "salmon");
    registerItem(Items.FISH, ItemFishFood.FishType.CLOWNFISH.getMetadata(), "clownfish");
    registerItem(Items.FISH, ItemFishFood.FishType.PUFFERFISH.getMetadata(), "pufferfish");
    registerItem(Items.COOKED_FISH, ItemFishFood.FishType.COD.getMetadata(), "cooked_cod");
    registerItem(Items.COOKED_FISH, ItemFishFood.FishType.SALMON.getMetadata(), "cooked_salmon");
    registerItem(Items.DYE, EnumDyeColor.BLACK.getDyeDamage(), "dye_black");
    registerItem(Items.DYE, EnumDyeColor.RED.getDyeDamage(), "dye_red");
    registerItem(Items.DYE, EnumDyeColor.GREEN.getDyeDamage(), "dye_green");
    registerItem(Items.DYE, EnumDyeColor.BROWN.getDyeDamage(), "dye_brown");
    registerItem(Items.DYE, EnumDyeColor.BLUE.getDyeDamage(), "dye_blue");
    registerItem(Items.DYE, EnumDyeColor.PURPLE.getDyeDamage(), "dye_purple");
    registerItem(Items.DYE, EnumDyeColor.CYAN.getDyeDamage(), "dye_cyan");
    registerItem(Items.DYE, EnumDyeColor.SILVER.getDyeDamage(), "dye_silver");
    registerItem(Items.DYE, EnumDyeColor.GRAY.getDyeDamage(), "dye_gray");
    registerItem(Items.DYE, EnumDyeColor.PINK.getDyeDamage(), "dye_pink");
    registerItem(Items.DYE, EnumDyeColor.LIME.getDyeDamage(), "dye_lime");
    registerItem(Items.DYE, EnumDyeColor.YELLOW.getDyeDamage(), "dye_yellow");
    registerItem(Items.DYE, EnumDyeColor.LIGHT_BLUE.getDyeDamage(), "dye_light_blue");
    registerItem(Items.DYE, EnumDyeColor.MAGENTA.getDyeDamage(), "dye_magenta");
    registerItem(Items.DYE, EnumDyeColor.ORANGE.getDyeDamage(), "dye_orange");
    registerItem(Items.DYE, EnumDyeColor.WHITE.getDyeDamage(), "dye_white");
    registerItem(Items.BONE, "bone");
    registerItem(Items.SUGAR, "sugar");
    registerItem(Items.CAKE, "cake");
    registerItem(Items.REPEATER, "repeater");
    registerItem(Items.COOKIE, "cookie");
    registerItem((Item)Items.SHEARS, "shears");
    registerItem(Items.MELON, "melon");
    registerItem(Items.PUMPKIN_SEEDS, "pumpkin_seeds");
    registerItem(Items.MELON_SEEDS, "melon_seeds");
    registerItem(Items.BEEF, "beef");
    registerItem(Items.COOKED_BEEF, "cooked_beef");
    registerItem(Items.CHICKEN, "chicken");
    registerItem(Items.COOKED_CHICKEN, "cooked_chicken");
    registerItem(Items.RABBIT, "rabbit");
    registerItem(Items.COOKED_RABBIT, "cooked_rabbit");
    registerItem(Items.MUTTON, "mutton");
    registerItem(Items.COOKED_MUTTON, "cooked_mutton");
    registerItem(Items.RABBIT_FOOT, "rabbit_foot");
    registerItem(Items.RABBIT_HIDE, "rabbit_hide");
    registerItem(Items.RABBIT_STEW, "rabbit_stew");
    registerItem(Items.ROTTEN_FLESH, "rotten_flesh");
    registerItem(Items.ENDER_PEARL, "ender_pearl");
    registerItem(Items.BLAZE_ROD, "blaze_rod");
    registerItem(Items.GHAST_TEAR, "ghast_tear");
    registerItem(Items.GOLD_NUGGET, "gold_nugget");
    registerItem(Items.NETHER_WART, "nether_wart");
    registerItem(Items.BEETROOT, "beetroot");
    registerItem(Items.BEETROOT_SEEDS, "beetroot_seeds");
    registerItem(Items.BEETROOT_SOUP, "beetroot_soup");
    registerItem(Items.TOTEM_OF_UNDYING, "totem");
    registerItem((Item)Items.POTIONITEM, "bottle_drinkable");
    registerItem((Item)Items.SPLASH_POTION, "bottle_splash");
    registerItem((Item)Items.LINGERING_POTION, "bottle_lingering");
    registerItem(Items.GLASS_BOTTLE, "glass_bottle");
    registerItem(Items.DRAGON_BREATH, "dragon_breath");
    registerItem(Items.SPIDER_EYE, "spider_eye");
    registerItem(Items.FERMENTED_SPIDER_EYE, "fermented_spider_eye");
    registerItem(Items.BLAZE_POWDER, "blaze_powder");
    registerItem(Items.MAGMA_CREAM, "magma_cream");
    registerItem(Items.BREWING_STAND, "brewing_stand");
    registerItem(Items.CAULDRON, "cauldron");
    registerItem(Items.ENDER_EYE, "ender_eye");
    registerItem(Items.SPECKLED_MELON, "speckled_melon");
    this.itemModelMesher.register(Items.SPAWN_EGG, new ItemMeshDefinition() {
          public ModelResourceLocation getModelLocation(ItemStack param1ItemStack) {
            return new ModelResourceLocation("spawn_egg", "inventory");
          }
        });
    registerItem(Items.EXPERIENCE_BOTTLE, "experience_bottle");
    registerItem(Items.FIRE_CHARGE, "fire_charge");
    registerItem(Items.WRITABLE_BOOK, "writable_book");
    registerItem(Items.EMERALD, "emerald");
    registerItem(Items.ITEM_FRAME, "item_frame");
    registerItem(Items.FLOWER_POT, "flower_pot");
    registerItem(Items.CARROT, "carrot");
    registerItem(Items.POTATO, "potato");
    registerItem(Items.BAKED_POTATO, "baked_potato");
    registerItem(Items.POISONOUS_POTATO, "poisonous_potato");
    registerItem((Item)Items.MAP, "map");
    registerItem(Items.GOLDEN_CARROT, "golden_carrot");
    registerItem(Items.SKULL, 0, "skull_skeleton");
    registerItem(Items.SKULL, 1, "skull_wither");
    registerItem(Items.SKULL, 2, "skull_zombie");
    registerItem(Items.SKULL, 3, "skull_char");
    registerItem(Items.SKULL, 4, "skull_creeper");
    registerItem(Items.SKULL, 5, "skull_dragon");
    registerItem(Items.CARROT_ON_A_STICK, "carrot_on_a_stick");
    registerItem(Items.NETHER_STAR, "nether_star");
    registerItem(Items.END_CRYSTAL, "end_crystal");
    registerItem(Items.PUMPKIN_PIE, "pumpkin_pie");
    registerItem(Items.FIREWORK_CHARGE, "firework_charge");
    registerItem(Items.COMPARATOR, "comparator");
    registerItem(Items.NETHERBRICK, "netherbrick");
    registerItem(Items.QUARTZ, "quartz");
    registerItem(Items.TNT_MINECART, "tnt_minecart");
    registerItem(Items.HOPPER_MINECART, "hopper_minecart");
    registerItem((Item)Items.ARMOR_STAND, "armor_stand");
    registerItem(Items.IRON_HORSE_ARMOR, "iron_horse_armor");
    registerItem(Items.GOLDEN_HORSE_ARMOR, "golden_horse_armor");
    registerItem(Items.DIAMOND_HORSE_ARMOR, "diamond_horse_armor");
    registerItem(Items.LEAD, "lead");
    registerItem(Items.NAME_TAG, "name_tag");
    this.itemModelMesher.register(Items.BANNER, new ItemMeshDefinition() {
          public ModelResourceLocation getModelLocation(ItemStack param1ItemStack) {
            return new ModelResourceLocation("banner", "inventory");
          }
        });
    this.itemModelMesher.register(Items.BED, new ItemMeshDefinition() {
          public ModelResourceLocation getModelLocation(ItemStack param1ItemStack) {
            return new ModelResourceLocation("bed", "inventory");
          }
        });
    this.itemModelMesher.register(Items.SHIELD, new ItemMeshDefinition() {
          public ModelResourceLocation getModelLocation(ItemStack param1ItemStack) {
            return new ModelResourceLocation("shield", "inventory");
          }
        });
    registerItem(Items.ELYTRA, "elytra");
    registerItem(Items.CHORUS_FRUIT, "chorus_fruit");
    registerItem(Items.CHORUS_FRUIT_POPPED, "chorus_fruit_popped");
    registerItem(Items.SHULKER_SHELL, "shulker_shell");
    registerItem(Items.IRON_NUGGET, "iron_nugget");
    registerItem(Items.RECORD_13, "record_13");
    registerItem(Items.RECORD_CAT, "record_cat");
    registerItem(Items.RECORD_BLOCKS, "record_blocks");
    registerItem(Items.RECORD_CHIRP, "record_chirp");
    registerItem(Items.RECORD_FAR, "record_far");
    registerItem(Items.RECORD_MALL, "record_mall");
    registerItem(Items.RECORD_MELLOHI, "record_mellohi");
    registerItem(Items.RECORD_STAL, "record_stal");
    registerItem(Items.RECORD_STRAD, "record_strad");
    registerItem(Items.RECORD_WARD, "record_ward");
    registerItem(Items.RECORD_11, "record_11");
    registerItem(Items.RECORD_WAIT, "record_wait");
    registerItem(Items.PRISMARINE_SHARD, "prismarine_shard");
    registerItem(Items.PRISMARINE_CRYSTALS, "prismarine_crystals");
    registerItem(Items.KNOWLEDGE_BOOK, "knowledge_book");
    this.itemModelMesher.register(Items.ENCHANTED_BOOK, new ItemMeshDefinition() {
          public ModelResourceLocation getModelLocation(ItemStack param1ItemStack) {
            return new ModelResourceLocation("enchanted_book", "inventory");
          }
        });
    this.itemModelMesher.register((Item)Items.FILLED_MAP, new ItemMeshDefinition() {
          public ModelResourceLocation getModelLocation(ItemStack param1ItemStack) {
            return new ModelResourceLocation("filled_map", "inventory");
          }
        });
    registerBlock(Blocks.COMMAND_BLOCK, "command_block");
    registerItem(Items.FIREWORKS, "fireworks");
    registerItem(Items.COMMAND_BLOCK_MINECART, "command_block_minecart");
    registerBlock(Blocks.BARRIER, "barrier");
    registerBlock(Blocks.MOB_SPAWNER, "mob_spawner");
    registerItem(Items.WRITTEN_BOOK, "written_book");
    registerBlock(Blocks.BROWN_MUSHROOM_BLOCK, BlockHugeMushroom.EnumType.ALL_INSIDE.getMetadata(), "brown_mushroom_block");
    registerBlock(Blocks.RED_MUSHROOM_BLOCK, BlockHugeMushroom.EnumType.ALL_INSIDE.getMetadata(), "red_mushroom_block");
    registerBlock(Blocks.DRAGON_EGG, "dragon_egg");
    registerBlock(Blocks.REPEATING_COMMAND_BLOCK, "repeating_command_block");
    registerBlock(Blocks.CHAIN_COMMAND_BLOCK, "chain_command_block");
    registerBlock(Blocks.STRUCTURE_BLOCK, TileEntityStructure.Mode.SAVE.getModeId(), "structure_block");
    registerBlock(Blocks.STRUCTURE_BLOCK, TileEntityStructure.Mode.LOAD.getModeId(), "structure_block");
    registerBlock(Blocks.STRUCTURE_BLOCK, TileEntityStructure.Mode.CORNER.getModeId(), "structure_block");
    registerBlock(Blocks.STRUCTURE_BLOCK, TileEntityStructure.Mode.DATA.getModeId(), "structure_block");
    ModelLoader.onRegisterItems(this.itemModelMesher);
  }
  
  protected void renderItemModel(ItemStack paramItemStack, IBakedModel paramIBakedModel, ItemCameraTransforms.TransformType paramTransformType, boolean paramBoolean) {
    if (!paramItemStack.isEmpty()) {
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      paramIBakedModel = ForgeHooksClient.handleCameraTransforms(paramIBakedModel, paramTransformType, paramBoolean);
      renderItem(paramItemStack, paramIBakedModel);
      GlStateManager.cullFace(GlStateManager.CullFace.BACK);
      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    } 
  }
  
  protected void renderItemModelIntoGUI(ItemStack paramItemStack, int paramInt1, int paramInt2, IBakedModel paramIBakedModel) {
    GlStateManager.pushMatrix();
    this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
    GlStateManager.enableRescaleNormal();
    GlStateManager.enableAlpha();
    GlStateManager.alphaFunc(516, 0.1F);
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    setupGuiTransform(paramInt1, paramInt2, paramIBakedModel.isGui3d());
    paramIBakedModel = ForgeHooksClient.handleCameraTransforms(paramIBakedModel, ItemCameraTransforms.TransformType.GUI, false);
    renderItem(paramItemStack, paramIBakedModel);
    GlStateManager.disableAlpha();
    GlStateManager.disableRescaleNormal();
    GlStateManager.disableLighting();
    GlStateManager.popMatrix();
    this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    this.textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
  }
  
  private void renderEffect(IBakedModel paramIBakedModel) {
    GlStateManager.depthMask(false);
    GlStateManager.depthFunc(514);
    GlStateManager.disableLighting();
    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
    this.textureManager.bindTexture(RES_ITEM_GLINT);
    GlStateManager.matrixMode(5890);
    GlStateManager.pushMatrix();
    GlStateManager.scale(8.0F, 8.0F, 8.0F);
    float f1 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
    GlStateManager.translate(f1, 0.0F, 0.0F);
    GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
    renderModel(paramIBakedModel, -8372020);
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.scale(8.0F, 8.0F, 8.0F);
    float f2 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
    GlStateManager.translate(-f2, 0.0F, 0.0F);
    GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
    renderModel(paramIBakedModel, -8372020);
    GlStateManager.popMatrix();
    GlStateManager.matrixMode(5888);
    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    GlStateManager.enableLighting();
    GlStateManager.depthFunc(515);
    GlStateManager.depthMask(true);
    this.textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
  }
  
  private void renderModel(IBakedModel paramIBakedModel, ItemStack paramItemStack) {
    renderModel(paramIBakedModel, -1, paramItemStack);
  }
  
  public void renderItemOverlayIntoGUI(FontRenderer paramFontRenderer, ItemStack paramItemStack, int paramInt1, int paramInt2, @Nullable String paramString) {
    if (!paramItemStack.isEmpty()) {
      if (paramItemStack.getCount() != 1 || paramString != null) {
        String str = (paramString == null) ? String.valueOf(paramItemStack.getCount()) : paramString;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        paramFontRenderer.drawStringWithShadow(str, (paramInt1 + 19 - 2 - paramFontRenderer.getStringWidth(str)), (paramInt2 + 6 + 3), 16777215);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
      } 
      if (paramItemStack.getItem().showDurabilityBar(paramItemStack)) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        double d = paramItemStack.getItem().getDurabilityForDisplay(paramItemStack);
        int i = paramItemStack.getItem().getRGBDurabilityForDisplay(paramItemStack);
        int j = Math.round(13.0F - (float)d * 13.0F);
        int k = i;
        draw(bufferBuilder, paramInt1 + 2, paramInt2 + 13, 13, 2, 0, 0, 0, 255);
        draw(bufferBuilder, paramInt1 + 2, paramInt2 + 13, j, 1, k >> 16 & 0xFF, k >> 8 & 0xFF, k & 0xFF, 255);
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
      } 
      EntityPlayerSP entityPlayerSP = (Minecraft.getMinecraft()).player;
      float f = (entityPlayerSP == null) ? 0.0F : entityPlayerSP.getCooldownTracker().getCooldown(paramItemStack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());
      if (f > 0.0F) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        draw(bufferBuilder, paramInt1, paramInt2 + MathHelper.floor(16.0F * (1.0F - f)), 16, MathHelper.ceil(16.0F * f), 255, 255, 255, 127);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
      } 
    } 
  }
  
  private void renderQuads(BufferBuilder paramBufferBuilder, List<BakedQuad> paramList, int paramInt, ItemStack paramItemStack) {
    boolean bool = (paramInt == -1 && !paramItemStack.isEmpty()) ? true : false;
    byte b = 0;
    int i = paramList.size();
    while (b < i) {
      BakedQuad bakedQuad = paramList.get(b);
      int j = paramInt;
      if (bool && bakedQuad.hasTintIndex()) {
        j = this.itemColors.colorMultiplier(paramItemStack, bakedQuad.getTintIndex());
        if (EntityRenderer.anaglyphEnable)
          j = TextureUtil.anaglyphColor(j); 
        j |= 0xFF000000;
      } 
      LightUtil.renderQuadColor(paramBufferBuilder, bakedQuad, j);
      b++;
    } 
  }
  
  public void renderItemOverlays(FontRenderer paramFontRenderer, ItemStack paramItemStack, int paramInt1, int paramInt2) {
    renderItemOverlayIntoGUI(paramFontRenderer, paramItemStack, paramInt1, paramInt2, (String)null);
  }
  
  private boolean isThereOneNegativeScale(ItemTransformVec3f paramItemTransformVec3f) {
    return ((paramItemTransformVec3f.scale.x < 0.0F)) ^ ((paramItemTransformVec3f.scale.y < 0.0F)) ^ ((paramItemTransformVec3f.scale.z < 0.0F) ? 1 : 0);
  }
  
  public ICERenderItem(TextureManager paramTextureManager, ModelManager paramModelManager, ItemColors paramItemColors) {
    this.textureManager = paramTextureManager;
    this.itemModelMesher = (ItemModelMesher)new ItemModelMesherForge(paramModelManager);
    registerItems();
    this.itemColors = paramItemColors;
  }
  
  protected void registerBlock(Block paramBlock, int paramInt, String paramString) {
    registerItem(Item.getItemFromBlock(paramBlock), paramInt, paramString);
  }
  
  public void renderItemAndEffectIntoGUI(@Nullable EntityLivingBase paramEntityLivingBase, final ItemStack p_184391_2_, int paramInt1, int paramInt2) {
    if (!p_184391_2_.isEmpty()) {
      this.zLevel += 50.0F;
      try {
        renderItemModelIntoGUI(p_184391_2_, paramInt1, paramInt2, getItemModelWithOverrides(p_184391_2_, (World)null, paramEntityLivingBase));
      } catch (Throwable throwable) {
        CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Rendering item");
        CrashReportCategory crashReportCategory = crashReport.makeCategory("Item being rendered");
        crashReportCategory.addDetail("Item Type", new ICrashReportDetail<String>() {
              public String call() throws Exception {
                return String.valueOf(p_184391_2_.getItem());
              }
            });
        crashReportCategory.addDetail("Item Aux", new ICrashReportDetail<String>() {
              public String call() throws Exception {
                return String.valueOf(p_184391_2_.getMetadata());
              }
            });
        crashReportCategory.addDetail("Item NBT", new ICrashReportDetail<String>() {
              public String call() throws Exception {
                return String.valueOf(p_184391_2_.getTagCompound());
              }
            });
        crashReportCategory.addDetail("Item Foil", new ICrashReportDetail<String>() {
              public String call() throws Exception {
                return String.valueOf(p_184391_2_.hasEffect());
              }
            });
        throw new ReportedException(crashReport);
      } 
      this.zLevel -= 50.0F;
    } 
  }
  
  public void renderItem(ItemStack paramItemStack, EntityLivingBase paramEntityLivingBase, ItemCameraTransforms.TransformType paramTransformType, boolean paramBoolean) {
    if (!paramItemStack.isEmpty() && paramEntityLivingBase != null) {
      IBakedModel iBakedModel = getItemModelWithOverrides(paramItemStack, paramEntityLivingBase.world, paramEntityLivingBase);
      renderItemModel(paramItemStack, iBakedModel, paramTransformType, paramBoolean);
    } 
  }
  
  private void setupGuiTransform(int paramInt1, int paramInt2, boolean paramBoolean) {
    GlStateManager.translate(paramInt1, paramInt2, 100.0F + this.zLevel);
    GlStateManager.translate(8.0F, 8.0F, 0.0F);
    GlStateManager.scale(1.0F, -1.0F, 1.0F);
    GlStateManager.scale(16.0F, 16.0F, 16.0F);
    if (paramBoolean) {
      GlStateManager.enableLighting();
    } else {
      GlStateManager.disableLighting();
    } 
  }
  
  protected void registerItem(Item paramItem, int paramInt, String paramString) {
    this.itemModelMesher.register(paramItem, paramInt, new ModelResourceLocation(paramString, "inventory"));
  }
  
  private void renderModel(IBakedModel paramIBakedModel, int paramInt) {
    renderModel(paramIBakedModel, paramInt, ItemStack.EMPTY);
  }
  
  public IBakedModel getItemModelWithOverrides(ItemStack paramItemStack, @Nullable World paramWorld, @Nullable EntityLivingBase paramEntityLivingBase) {
    IBakedModel iBakedModel = this.itemModelMesher.getItemModel(paramItemStack);
    return iBakedModel.getOverrides().handleItemState(iBakedModel, paramItemStack, paramWorld, paramEntityLivingBase);
  }
  
  public void onResourceManagerReload(IResourceManager paramIResourceManager) {
    this.itemModelMesher.rebuildCache();
  }
  
  private void renderModel(IBakedModel paramIBakedModel, int paramInt, ItemStack paramItemStack) {
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();
    bufferBuilder.begin(7, DefaultVertexFormats.ITEM);
    for (EnumFacing enumFacing : EnumFacing.values())
      renderQuads(bufferBuilder, paramIBakedModel.getQuads((IBlockState)null, enumFacing, 0L), paramInt, paramItemStack); 
    renderQuads(bufferBuilder, paramIBakedModel.getQuads((IBlockState)null, (EnumFacing)null, 0L), paramInt, paramItemStack);
    tessellator.draw();
  }
}
