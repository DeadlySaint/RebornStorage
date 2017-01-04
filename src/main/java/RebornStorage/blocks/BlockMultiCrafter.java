package RebornStorage.blocks;

import RebornStorage.RebornStorage;
import RebornStorage.client.CreativeTabRebornStorage;
import RebornStorage.client.GuiHandler;
import RebornStorage.lib.ModInfo;
import RebornStorage.multiblocks.MultiBlockCrafter;
import RebornStorage.tiles.TileMultiCrafter;
import com.google.common.collect.Lists;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.blocks.PropertyString;
import reborncore.common.multiblock.BlockMultiblockBase;
import reborncore.common.util.ArrayUtils;
import reborncore.common.util.ChatUtils;
import reborncore.common.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Mark on 03/01/2017.
 */
public class BlockMultiCrafter extends BlockMultiblockBase{

	public static final String[] types = new String[] { "frame", "heat", "cpu", "storage" };
	private static final List<String> typesList = Lists.newArrayList(ArrayUtils.arrayToLowercase(types));

	public static final PropertyString VARIANTS = new PropertyString("type", types);
	public static final PropertyBool ACTIVE = PropertyBool.create("active");

	public BlockMultiCrafter() {
		super(Material.IRON);
		setCreativeTab(CreativeTabRebornStorage.INSTANCE);
		setUnlocalizedName(ModInfo.MOD_ID + ".multicrafter");
		this.setDefaultState(this.getStateFromMeta(0));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileMultiCrafter(getStateFromMeta(meta).getValue(VARIANTS));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileMultiCrafter tile = (TileMultiCrafter) worldIn.getTileEntity(pos);
		if(tile.getMultiblockController() != null){
			if(!tile.getMultiblockController().isAssembled() && worldIn.isRemote){
				if(tile.getMultiblockController().getLastValidationException() != null){
					ChatUtils.sendNoSpamMessages(42, new TextComponentString(tile.getMultiblockController().getLastValidationException().getMessage()));
				}
			} else if(worldIn.isRemote) {
				playerIn.openGui(RebornStorage.INSTANCE, GuiHandler.MULTI_CRAFTER_BASEPAGE, worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}

		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		boolean active = false;
		if (meta >= types.length) {
			active = true;
		}
		int offset = 0;
		if(active){
			offset = types.length;
		}
		return getBlockState().getBaseState().withProperty(VARIANTS, typesList.get(meta - offset)).withProperty(ACTIVE, active);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return typesList.indexOf(state.getValue(VARIANTS)) + (state.getValue(ACTIVE) ? typesList.size() : 0);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANTS, ACTIVE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTabs, List<ItemStack> list) {
		for (int meta = 0; meta < types.length; meta++) {
			list.add(new ItemStack(item, 1, meta));
		}
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(this, 1, getMetaFromState(state));
	}

}
