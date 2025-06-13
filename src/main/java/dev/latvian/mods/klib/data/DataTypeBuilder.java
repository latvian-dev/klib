package dev.latvian.mods.klib.data;

import com.mojang.datafixers.util.Function10;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function12;
import com.mojang.datafixers.util.Function13;
import com.mojang.datafixers.util.Function14;
import com.mojang.datafixers.util.Function15;
import com.mojang.datafixers.util.Function16;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class DataTypeBuilder<C> {
	private final Class<C> typeClass;
	private final List<DataTypeField<C, ?>> fields;

	DataTypeBuilder(Class<C> typeClass) {
		this.typeClass = Objects.requireNonNull(typeClass);
		this.fields = new ArrayList<>();
	}

	public <T> DataTypeBuilder<C> add(DataTypeField<C, T> field) {
		fields.add(field);
		return this;
	}

	public DataType<C> buildRaw(int argumentCount, Function<Object[], C> constructor) {
		if (argumentCount != fields.size()) {
			throw new IllegalArgumentException("Expected " + argumentCount + " arguments, but got " + fields.size());
		}

		var list = List.copyOf(fields);
		return DataType.of(
			new DataTypeBuilderCodec<>(list, constructor).codec(),
			new DataTypeBuilderStreamCodec<>(list, constructor),
			typeClass
		);
	}

	public <T1> DataType<C> build(Function<T1, C> constructor) {
		return buildRaw(1, args -> constructor.apply(
			(T1) args[0]
		));
	}

	public <T1, T2> DataType<C> build(BiFunction<T1, T2, C> constructor) {
		return buildRaw(2, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1]
		));
	}

	public <T1, T2, T3> DataType<C> build(Function3<T1, T2, T3, C> constructor) {
		return buildRaw(3, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2]
		));
	}

	public <T1, T2, T3, T4> DataType<C> build(Function4<T1, T2, T3, T4, C> constructor) {
		return buildRaw(4, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3]
		));
	}

	public <T1, T2, T3, T4, T5> DataType<C> build(Function5<T1, T2, T3, T4, T5, C> constructor) {
		return buildRaw(5, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4]
		));
	}

	public <T1, T2, T3, T4, T5, T6> DataType<C> build(Function6<T1, T2, T3, T4, T5, T6, C> constructor) {
		return buildRaw(6, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7> DataType<C> build(Function7<T1, T2, T3, T4, T5, T6, T7, C> constructor) {
		return buildRaw(7, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8> DataType<C> build(Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> constructor) {
		return buildRaw(8, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9> DataType<C> build(Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, C> constructor) {
		return buildRaw(9, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7],
			(T9) args[8]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> DataType<C> build(Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, C> constructor) {
		return buildRaw(10, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7],
			(T9) args[8],
			(T10) args[9]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> DataType<C> build(Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, C> constructor) {
		return buildRaw(11, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7],
			(T9) args[8],
			(T10) args[9],
			(T11) args[10]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> DataType<C> build(Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, C> constructor) {
		return buildRaw(12, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7],
			(T9) args[8],
			(T10) args[9],
			(T11) args[10],
			(T12) args[11]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> DataType<C> build(Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, C> constructor) {
		return buildRaw(13, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7],
			(T9) args[8],
			(T10) args[9],
			(T11) args[10],
			(T12) args[11],
			(T13) args[12]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> DataType<C> build(Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, C> constructor) {
		return buildRaw(14, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7],
			(T9) args[8],
			(T10) args[9],
			(T11) args[10],
			(T12) args[11],
			(T13) args[12],
			(T14) args[13]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> DataType<C> build(Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, C> constructor) {
		return buildRaw(15, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7],
			(T9) args[8],
			(T10) args[9],
			(T11) args[10],
			(T12) args[11],
			(T13) args[12],
			(T14) args[13],
			(T15) args[14]
		));
	}

	public <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> DataType<C> build(Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, C> constructor) {
		return buildRaw(16, args -> constructor.apply(
			(T1) args[0],
			(T2) args[1],
			(T3) args[2],
			(T4) args[3],
			(T5) args[4],
			(T6) args[5],
			(T7) args[6],
			(T8) args[7],
			(T9) args[8],
			(T10) args[9],
			(T11) args[10],
			(T12) args[11],
			(T13) args[12],
			(T14) args[13],
			(T15) args[14],
			(T16) args[15]
		));
	}
}
