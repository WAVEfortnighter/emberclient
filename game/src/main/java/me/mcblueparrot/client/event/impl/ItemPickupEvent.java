package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;

@AllArgsConstructor
public class ItemPickupEvent {

	public EntityPlayer player;
	public EntityItem pickedUp;

}
