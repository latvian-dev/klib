package dev.latvian.mods.klib.codec;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;

import java.util.function.Function;

public interface CollectionCodecs {
	Codec<IntList> INT_LIST = Codec.INT.listOf().xmap(IntArrayList::new, Function.identity());
	Codec<IntList> INT_LIST_OR_SELF = Codec.either(Codec.INT, INT_LIST).xmap(either -> either.map(IntArrayList::of, Function.identity()), list -> list.size() == 1 ? Either.left(list.getFirst()) : Either.right(list));
	Codec<ShortList> SHORT_LIST = Codec.SHORT.listOf().xmap(ShortArrayList::new, Function.identity());
	Codec<IntSet> INT_SET = KLibCodecs.setOf(Codec.INT).xmap(IntOpenHashSet::new, Function.identity());
	Codec<IntSet> LINKED_INT_SET = KLibCodecs.setOf(Codec.INT).xmap(IntLinkedOpenHashSet::new, Function.identity());
	Codec<LongSet> LONG_SET = Codec.LONG_STREAM.xmap(LongOpenHashSet::toSet, LongCollection::longStream);
}
