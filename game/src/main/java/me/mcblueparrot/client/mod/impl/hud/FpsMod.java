package me.mcblueparrot.client.mod.impl.hud;

import me.mcblueparrot.client.mod.hud.SmoothCounterHudMod;
import net.minecraft.client.Minecraft;

public class FpsMod extends SmoothCounterHudMod {

	public FpsMod() {
		super("FPS", "fps", "Display your frames per seconds.");
	}

	@Override
	public int getIntValue() {
		return Minecraft.getDebugFPS();
	}

	@Override
	public String getSuffix() {
		return "FPS";
	}



}
