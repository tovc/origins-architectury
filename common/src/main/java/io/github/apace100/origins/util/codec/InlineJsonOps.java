package io.github.apace100.origins.util.codec;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.dynamic.ForwardingDynamicOps;

public class InlineJsonOps extends ForwardingDynamicOps<JsonElement> implements InlineOps<JsonElement> {
	public static final InlineJsonOps INSTANCE = new InlineJsonOps();

	public InlineJsonOps() {
		super(JsonOps.INSTANCE);
	}
}
