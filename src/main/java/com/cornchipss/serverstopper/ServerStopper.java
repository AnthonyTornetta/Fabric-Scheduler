package com.cornchipss.serverstopper;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import java.time.LocalDateTime;
public class ServerStopper implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		final int HOUR_CLOSING = 23;
		final int MINUTE_CLOSING = 59;

		ServerTickEvents.START_SERVER_TICK.register(new ServerTickEvents.StartTick() {
			private int lastMinute = 0;
			private int lastSecond = 0;

			private boolean itsAlreadyDead = false;

			@Override
			public void onStartTick(MinecraftServer server) {
				LocalDateTime now = LocalDateTime.now();

				if (now.getHour() == HOUR_CLOSING && now.getMinute() < MINUTE_CLOSING && now.getMinute() >= MINUTE_CLOSING - 5) {
					if (now.getSecond() == 59 && lastMinute != now.getMinute()) {
						lastMinute = now.getMinute();
						String s = "";
						if (now.getMinute() != MINUTE_CLOSING - 1)
							s = "s";

						server.getPlayerManager().broadcast(Text.of("Server closing in " + (MINUTE_CLOSING - now.getMinute()) + " minute" + s + "!"), true);
					}
				} else if (now.getHour() == HOUR_CLOSING && now.getMinute() == MINUTE_CLOSING) {
					if (lastSecond != now.getSecond()) {
						lastSecond = now.getSecond();
						server.getPlayerManager().broadcast(Text.of("Server closing in " + (59 - now.getSecond()) + " seconds!"), true);
					}
				} else if (!itsAlreadyDead && now.getHour() == (HOUR_CLOSING + 1) % 24 && now.getMinute() == (MINUTE_CLOSING + 1) % 60) {
					itsAlreadyDead = true;
					server.getPlayerManager().broadcast(Text.of("Bye!"), true);
					server.stop(false);
				}
			}
		});
	}
}
