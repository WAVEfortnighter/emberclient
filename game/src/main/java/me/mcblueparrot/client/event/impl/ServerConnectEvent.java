package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import me.mcblueparrot.client.DetectedServer;
import net.minecraft.client.multiplayer.ServerData;

@AllArgsConstructor
public class ServerConnectEvent {

	public ServerData data;
	public DetectedServer server;

}
