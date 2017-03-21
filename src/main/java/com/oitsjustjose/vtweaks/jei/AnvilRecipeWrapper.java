package com.oitsjustjose.vtweaks.jei;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

/**
 * 
 * @author mezz
 * 
 *         100% of this code has been ripped from 1.11 JEI
 *
 */

public class AnvilRecipeWrapper extends BlankRecipeWrapper
{
	private final List<List<ItemStack>> inputs;
	private final List<ItemStack> outputs;
	private int xpCost;
	@Nullable
	private Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients = null;
	@Nullable
	private ItemStack lastLeftStack;
	@Nullable
	private ItemStack lastRightStack;
	private int lastCost;

	public AnvilRecipeWrapper(ItemStack leftInput, List<ItemStack> rightInputs, ItemStack output, int xpCost)
	{
		this.xpCost = xpCost;
		this.inputs = Lists.newArrayList();
		this.inputs.add(Collections.singletonList(leftInput));
		this.inputs.add(rightInputs);
		this.outputs = Lists.newArrayList();
		outputs.add(output);
	}

	public AnvilRecipeWrapper(List<ItemStack> leftInput, List<ItemStack> output, int xpCost)
	{
		this.xpCost = xpCost;
		this.inputs = Lists.newArrayList();
		this.inputs.add(leftInput);
		this.outputs = output;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
	{
		if (currentIngredients == null)
		{
			return;
		}

		ItemStack newLeftStack = currentIngredients.get(0).getDisplayedIngredient();
		ItemStack newRightStack = currentIngredients.get(1).getDisplayedIngredient();

		if (lastLeftStack == null || lastRightStack == null || !ItemStack.areItemStacksEqual(lastLeftStack, newLeftStack) || !ItemStack.areItemStacksEqual(lastRightStack, newRightStack))
		{
			lastLeftStack = newLeftStack;
			lastRightStack = newRightStack;
			lastCost = xpCost;
		}

		if (lastCost != 0)
		{
			String costText = lastCost < 0 ? "err" : Integer.toString(lastCost);
			String text = I18n.format("container.repair.cost", costText);

			int mainColor = 0xFF80FF20;
			if ((lastCost >= 40 || lastCost > minecraft.thePlayer.experienceLevel) && !minecraft.thePlayer.capabilities.isCreativeMode)
			{
				// Show red if the player doesn't have enough levels
				mainColor = 0xFFFF6060;
			}

			drawRepairCost(minecraft, text, mainColor, recipeWidth);
		}
	}

	private void drawRepairCost(Minecraft minecraft, String text, int mainColor, int recipeWidth)
	{
		int shadowColor = 0xFF000000 | (mainColor & 0xFCFCFC) >> 2;
		int width = minecraft.fontRendererObj.getStringWidth(text);
		int x = recipeWidth - 2 - width;
		int y = 27;

		if (minecraft.fontRendererObj.getUnicodeFlag())
		{
			Gui.drawRect(x - 2, y - 2, x + width + 2, y + 10, 0xFF000000);
			Gui.drawRect(x - 1, y - 1, x + width + 1, y + 9, 0xFF3B3B3B);
		}
		else
		{
			minecraft.fontRendererObj.drawString(text, x + 1, y, shadowColor);
			minecraft.fontRendererObj.drawString(text, x, y + 1, shadowColor);
			minecraft.fontRendererObj.drawString(text, x + 1, y + 1, shadowColor);
		}

		minecraft.fontRendererObj.drawString(text, x, y, mainColor);
	}

	@Override
	public void getIngredients(IIngredients ingredients)
	{
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutputs(ItemStack.class, outputs);
	}

	public void setCurrentIngredients(Map<Integer, ? extends IGuiIngredient<ItemStack>> currentIngredients)
	{
		this.currentIngredients = currentIngredients;
	}
}