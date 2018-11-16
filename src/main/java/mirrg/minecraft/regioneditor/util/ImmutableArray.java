package mirrg.minecraft.regioneditor.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ImmutableArray<T>
{

	private final T[] array;

	private ImmutableArray(T[] array)
	{
		this.array = array;
	}

	//

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> ImmutableArray<T> of(T... array)
	{
		T[] array2 = (T[]) new Object[array.length];
		Class<?> clazz = array.getClass().getComponentType();
		for (int i = 0; i < array.length; i++) {
			if (!clazz.isInstance(array[i])) throw new ClassCastException();
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	//

	public static ImmutableArray<Byte> fromArray(byte[] array)
	{
		Byte[] array2 = new Byte[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	public static ImmutableArray<Character> fromArray(char[] array)
	{
		Character[] array2 = new Character[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	public static ImmutableArray<Short> fromArray(short[] array)
	{
		Short[] array2 = new Short[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	public static ImmutableArray<Integer> fromArray(int[] array)
	{
		Integer[] array2 = new Integer[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	public static ImmutableArray<Long> fromArray(long[] array)
	{
		Long[] array2 = new Long[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	public static ImmutableArray<Float> fromArray(float[] array)
	{
		Float[] array2 = new Float[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	public static ImmutableArray<Double> fromArray(double[] array)
	{
		Double[] array2 = new Double[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	public static ImmutableArray<Boolean> fromArray(boolean[] array)
	{
		Boolean[] array2 = new Boolean[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = array[i];
		}
		return new ImmutableArray<>(array2);
	}

	//

	@SuppressWarnings("unchecked")
	public static <T> ImmutableArray<T> fromList(List<? extends T> array)
	{
		T[] array2 = (T[]) new Object[array.size()];
		for (int i = 0; i < array.size(); i++) {
			array2[i] = array.get(i);
		}
		return new ImmutableArray<>(array2);
	}

	public static <T> ImmutableArray<T> fromStream(Stream<? extends T> stream)
	{
		return fromList((List<? extends T>) stream.collect(Collectors.toCollection(ArrayList::new)));
	}

	public static <T> ImmutableArray<T> fromIterable(Iterable<? extends T> iterable)
	{
		ArrayList<T> list = new ArrayList<>();
		Iterator<? extends T> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return fromList(list);
	}

	public static <T> ImmutableArray<T> fromEnumeration(Enumeration<? extends T> enumeration)
	{
		ArrayList<T> list = new ArrayList<>();
		while (enumeration.hasMoreElements()) {
			list.add(enumeration.nextElement());
		}
		return fromList(list);
	}

	//

	public int length()
	{
		return array.length;
	}

	public T get(int index)
	{
		return array[index];
	}

	//

	public T[] toArray(IntFunction<T[]> arraySupplier)
	{
		T[] array2 = arraySupplier.apply(array.length);
		System.arraycopy(array, 0, array2, 0, array.length);
		return array2;
	}

	public <C extends Collection<T>> C toCollection(Supplier<C> collectionFactory)
	{
		C collection = collectionFactory.get();
		for (int i = 0; i < array.length; i++) {
			collection.add(array[i]);
		}
		return collection;
	}

	public Enumeration<T> values()
	{
		return new Enumeration<T>() {

			private int i = 0;

			@Override
			public boolean hasMoreElements()
			{
				return i < array.length;
			}

			@Override
			public T nextElement()
			{
				return array[i++];
			}

		};
	}

	public Stream<T> stream()
	{
		return Stream.of(array);
	}

	public void forEach(ObjIntConsumer<T> consumer)
	{
		for (int i = 0; i < array.length; i++) {
			consumer.accept(array[i], i);
		}
	}

	//

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int i = 0; i < array.length; i++) {
			if (i != 0) sb.append(", ");
			sb.append(array[i].toString());
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		for (int i = 0; i < array.length; i++) {
			result = prime * result + ((array[i] == null) ? 0 : array[i].hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ImmutableArray<?> other = (ImmutableArray<?>) obj;
		if (array.length != other.array.length) return false;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) {
				if (other.array[i] != null) return false;
			} else if (!array[i].equals(other.array[i])) return false;
		}
		return true;
	}

}
