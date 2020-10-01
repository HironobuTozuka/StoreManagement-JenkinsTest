package inc.roms.rcs.service.configuration;

public interface ConfigConverter<T> {

    T convert(String s);

    String convert(T t);

    class IntegerConverter implements ConfigConverter<Integer> {

        @Override
        public Integer convert(String s) {
            return Integer.valueOf(s);
        }

        @Override
        public String convert(Integer integer) {
            return String.valueOf(integer);
        }
    }
}
