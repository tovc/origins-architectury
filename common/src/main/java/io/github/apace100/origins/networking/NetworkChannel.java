package io.github.apace100.origins.networking;

import net.minecraft.util.Identifier;

import java.util.function.Predicate;
import java.util.function.Supplier;

public record NetworkChannel(Identifier channel,
							 Predicate<String> acceptedServerVersion,
							 Predicate<String> acceptedClientVersion,
							 Supplier<String> version) {

}
