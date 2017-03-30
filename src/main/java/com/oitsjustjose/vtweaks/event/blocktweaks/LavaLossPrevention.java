package com.oitsjustjose.vtweaks.event.blocktweaks;

import java.util.Iterator;

import com.oitsjustjose.vtweaks.VTweaks;
import com.oitsjustjose.vtweaks.util.HelperFunctions;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LavaLossPrevention
{
	@SubscribeEvent
	public void registerTweak(HarvestDropsEvent event)
	{
		// Checks to see if feature is enabled
		if (!VTweaks.config.enableLavaLossPrevention)
			return;
		// Confirming that player exists
		if (event.getHarvester() == null || event.getState() == null || event.getState().getBlock() == null)
			return;

		EntityPlayer player = event.getHarvester();

		// Checks if the block broken is what I consider "valuable"
		if (shouldPreventLoss(event.getState()))
		{
			// Confirms if it's above lava
			if (shouldSnag(event.getWorld(), event.getPos()))
			{
				// Captures all drops
				Iterator<ItemStack> iter = event.getDrops().iterator();
				while (iter.hasNext())
				{
					ItemStack drop = iter.next().copy();
					if (!event.getWorld().isRemote)
					{
						// And attempts to put them in inventory
						if (!player.inventory.addItemStackToInventory(drop))
						{
							// Otherwise player's feet it is
							event.getWorld().spawnEntityInWorld(HelperFunctions.createItemEntity(event.getWorld(), player.getPosition(), drop));
						}
					}
					iter.remove();
				}
			}
		}
	}

	private boolean shouldSnag(World world, BlockPos pos)
	{
		Fluid fluid = FluidRegistry.lookupFluidForBlock(world.getBlockState(pos).getBlock());
		System.out.println("Fluid null=" + (fluid == null));
		return fluid != null ? fluid.getTemperature() > 1000 : false;
	}

	private boolean shouldPreventLoss(IBlockState state)
	{
		ItemStack compare = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
		for (ItemStack i : VTweaks.config.lavaLossBlockList)
			if (i.getItem() == compare.getItem() && i.getMetadata() == compare.getMetadata())
				return true;

		return false;
	}
}
