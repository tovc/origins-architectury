package io.github.apace100.origins.networking;

import net.minecraft.text.Text;

public interface INetworkHandler {
	void queue(Runnable runnable);
	void setHandled(boolean handled);
	<MSG> void reply(MSG message);
	void disconnect(Text reason);
}
