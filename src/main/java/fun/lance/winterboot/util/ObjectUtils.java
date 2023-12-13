package fun.lance.winterboot.util;

import fun.lance.winterboot.lang.Nullable;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.util.*;

public class ObjectUtils {
    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";
    private static final String ARRAY_START = "{";
    private static final String ARRAY_END = "}";
    private static final String EMPTY_ARRAY = "{}";
    private static final String ARRAY_ELEMENT_SEPARATOR = ", ";
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final String NON_EMPTY_ARRAY = "{...}";
    private static final String COLLECTION = "[...]";
    private static final String MAP = "{...}";

    public ObjectUtils() {
    }

    public static boolean isCheckedException(Throwable ex) {
        return !(ex instanceof RuntimeException) && !(ex instanceof Error);
    }

    public static boolean isCompatibleWithThrowsClause(Throwable ex, @Nullable Class<?>... declaredExceptions) {
        if (!isCheckedException(ex)) {
            return true;
        } else {
            if (declaredExceptions != null) {
                Class[] var2 = declaredExceptions;
                int var3 = declaredExceptions.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    Class<?> declaredException = var2[var4];
                    if (declaredException.isInstance(ex)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public static boolean isArray(@Nullable Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    public static boolean isEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            Optional<?> optional = (Optional)obj;
            return optional.isEmpty();
        } else if (obj instanceof CharSequence) {
            CharSequence charSequence = (CharSequence)obj;
            return charSequence.isEmpty();
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            Collection<?> collection = (Collection)obj;
            return collection.isEmpty();
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map)obj;
            return map.isEmpty();
        } else {
            return false;
        }
    }

    @Nullable
    public static Object unwrapOptional(@Nullable Object obj) {
        if (obj instanceof Optional<?> optional) {
            if (optional.isEmpty()) {
                return null;
            } else {
                Object result = optional.get();
                Assert.isTrue(!(result instanceof Optional), "Multi-level Optional usage not supported");
                return result;
            }
        } else {
            return obj;
        }
    }

    public static boolean containsElement(@Nullable Object[] array, Object element) {
        if (array == null) {
            return false;
        } else {
            Object[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object arrayEle = var2[var4];
                if (nullSafeEquals(arrayEle, element)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean containsConstant(Enum<?>[] enumValues, String constant) {
        return containsConstant(enumValues, constant, false);
    }

    public static boolean containsConstant(Enum<?>[] enumValues, String constant, boolean caseSensitive) {
        Enum[] var3 = enumValues;
        int var4 = enumValues.length;
        int var5 = 0;

        while(true) {
            if (var5 >= var4) {
                return false;
            }

            Enum<?> candidate = var3[var5];
            if (caseSensitive) {
                if (candidate.toString().equals(constant)) {
                    break;
                }
            } else if (candidate.toString().equalsIgnoreCase(constant)) {
                break;
            }

            ++var5;
        }

        return true;
    }

//    public static <E extends Enum<?>> E caseInsensitiveValueOf(E[] enumValues, String constant) {
//        Enum[] var2 = enumValues;
//        int var3 = enumValues.length;
//
//        for(int var4 = 0; var4 < var3; ++var4) {
//            E candidate = var2[var4];
//            if (candidate.toString().equalsIgnoreCase(constant)) {
//                return candidate;
//            }
//        }
//
//        throw new IllegalArgumentException("Constant [" + constant + "] does not exist in enum type " + enumValues.getClass().componentType().getName());
//    }

//    public static <A, O extends A> A[] addObjectToArray(@Nullable A[] array, @Nullable O obj) {
//        return addObjectToArray(array, obj, array != null ? array.length : 0);
//    }

//    public static <A, O extends A> A[] addObjectToArray(@Nullable A[] array, @Nullable O obj, int position) {
//        Class<?> componentType = Object.class;
//        if (array != null) {
//            componentType = array.getClass().componentType();
//        } else if (obj != null) {
//            componentType = obj.getClass();
//        }
//
//        int newArrayLength = array != null ? array.length + 1 : 1;
//        A[] newArray = (Object[])Array.newInstance(componentType, newArrayLength);
//        if (array != null) {
//            System.arraycopy(array, 0, newArray, 0, position);
//            System.arraycopy(array, position, newArray, position + 1, array.length - position);
//        }
//
//        newArray[position] = obj;
//        return newArray;
//    }

    public static Object[] toObjectArray(@Nullable Object source) {
        if (source instanceof Object[] objects) {
            return objects;
        } else if (source == null) {
            return EMPTY_OBJECT_ARRAY;
        } else if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("Source is not an array: " + source);
        } else {
            int length = Array.getLength(source);
            if (length == 0) {
                return EMPTY_OBJECT_ARRAY;
            } else {
                Class<?> wrapperType = Array.get(source, 0).getClass();
                Object[] newArray = (Object[])Array.newInstance(wrapperType, length);

                for(int i = 0; i < length; ++i) {
                    newArray[i] = Array.get(source, i);
                }

                return newArray;
            }
        }
    }

    public static boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        } else if (o1 != null && o2 != null) {
            if (o1.equals(o2)) {
                return true;
            } else {
                return o1.getClass().isArray() && o2.getClass().isArray() ? arrayEquals(o1, o2) : false;
            }
        } else {
            return false;
        }
    }

    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] objects1) {
            if (o2 instanceof Object[] objects2) {
                return Arrays.equals(objects1, objects2);
            }
        }

        if (o1 instanceof boolean[] booleans1) {
            if (o2 instanceof boolean[] booleans2) {
                return Arrays.equals(booleans1, booleans2);
            }
        }

        if (o1 instanceof byte[] bytes1) {
            if (o2 instanceof byte[] bytes2) {
                return Arrays.equals(bytes1, bytes2);
            }
        }

        if (o1 instanceof char[] chars1) {
            if (o2 instanceof char[] chars2) {
                return Arrays.equals(chars1, chars2);
            }
        }

        if (o1 instanceof double[] doubles1) {
            if (o2 instanceof double[] doubles2) {
                return Arrays.equals(doubles1, doubles2);
            }
        }

        if (o1 instanceof float[] floats1) {
            if (o2 instanceof float[] floats2) {
                return Arrays.equals(floats1, floats2);
            }
        }

        if (o1 instanceof int[] ints1) {
            if (o2 instanceof int[] ints2) {
                return Arrays.equals(ints1, ints2);
            }
        }

        if (o1 instanceof long[] longs1) {
            if (o2 instanceof long[] longs2) {
                return Arrays.equals(longs1, longs2);
            }
        }

        if (o1 instanceof short[] shorts1) {
            if (o2 instanceof short[] shorts2) {
                return Arrays.equals(shorts1, shorts2);
            }
        }

        return false;
    }

    public static int nullSafeHash(@Nullable Object... elements) {
        if (elements == null) {
            return 0;
        } else {
            int result = 1;
            Object[] var2 = elements;
            int var3 = elements.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object element = var2[var4];
                result = 31 * result + nullSafeHashCode(element);
            }

            return result;
        }
    }

    public static int nullSafeHashCode(@Nullable Object obj) {
        if (obj == null) {
            return 0;
        } else {
            if (obj.getClass().isArray()) {
                if (obj instanceof Object[]) {
                    Object[] objects = (Object[])obj;
                    return Arrays.hashCode(objects);
                }

                if (obj instanceof boolean[]) {
                    boolean[] booleans = (boolean[])obj;
                    return Arrays.hashCode(booleans);
                }

                if (obj instanceof byte[]) {
                    byte[] bytes = (byte[])obj;
                    return Arrays.hashCode(bytes);
                }

                if (obj instanceof char[]) {
                    char[] chars = (char[])obj;
                    return Arrays.hashCode(chars);
                }

                if (obj instanceof double[]) {
                    double[] doubles = (double[])obj;
                    return Arrays.hashCode(doubles);
                }

                if (obj instanceof float[]) {
                    float[] floats = (float[])obj;
                    return Arrays.hashCode(floats);
                }

                if (obj instanceof int[]) {
                    int[] ints = (int[])obj;
                    return Arrays.hashCode(ints);
                }

                if (obj instanceof long[]) {
                    long[] longs = (long[])obj;
                    return Arrays.hashCode(longs);
                }

                if (obj instanceof short[]) {
                    short[] shorts = (short[])obj;
                    return Arrays.hashCode(shorts);
                }
            }

            return obj.hashCode();
        }
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable Object[] array) {
        return Arrays.hashCode(array);
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable boolean[] array) {
        return Arrays.hashCode(array);
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable byte[] array) {
        return Arrays.hashCode(array);
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable char[] array) {
        return Arrays.hashCode(array);
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable double[] array) {
        return Arrays.hashCode(array);
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable float[] array) {
        return Arrays.hashCode(array);
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable int[] array) {
        return Arrays.hashCode(array);
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable long[] array) {
        return Arrays.hashCode(array);
    }

    /** @deprecated */
    @Deprecated(
            since = "6.1"
    )
    public static int nullSafeHashCode(@Nullable short[] array) {
        return Arrays.hashCode(array);
    }

    public static String identityToString(@Nullable Object obj) {
        if (obj == null) {
            return "";
        } else {
            String var10000 = obj.getClass().getName();
            return var10000 + "@" + getIdentityHexString(obj);
        }
    }

    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    public static String getDisplayString(@Nullable Object obj) {
        return obj == null ? "" : nullSafeToString(obj);
    }

    public static String nullSafeClassName(@Nullable Object obj) {
        return obj != null ? obj.getClass().getName() : "null";
    }

    public static String nullSafeToString(@Nullable Object obj) {
        if (obj == null) {
            return "null";
        } else {
            String str;
            if (obj instanceof String) {
                str = (String)obj;
                return str;
            } else if (obj instanceof Object[]) {
                Object[] objects = (Object[])obj;
                return nullSafeToString(objects);
            } else if (obj instanceof boolean[]) {
                boolean[] booleans = (boolean[])obj;
                return nullSafeToString(booleans);
            } else if (obj instanceof byte[]) {
                byte[] bytes = (byte[])obj;
                return nullSafeToString(bytes);
            } else if (obj instanceof char[]) {
                char[] chars = (char[])obj;
                return nullSafeToString(chars);
            } else if (obj instanceof double[]) {
                double[] doubles = (double[])obj;
                return nullSafeToString(doubles);
            } else if (obj instanceof float[]) {
                float[] floats = (float[])obj;
                return nullSafeToString(floats);
            } else if (obj instanceof int[]) {
                int[] ints = (int[])obj;
                return nullSafeToString(ints);
            } else if (obj instanceof long[]) {
                long[] longs = (long[])obj;
                return nullSafeToString(longs);
            } else if (obj instanceof short[]) {
                short[] shorts = (short[])obj;
                return nullSafeToString(shorts);
            } else {
                str = obj.toString();
                return str != null ? str : "";
            }
        }
    }

    public static String nullSafeToString(@Nullable Object[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                Object[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Object o = var3[var5];
                    stringJoiner.add(String.valueOf(o));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable boolean[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                boolean[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    boolean b = var3[var5];
                    stringJoiner.add(String.valueOf(b));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable byte[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                byte[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    byte b = var3[var5];
                    stringJoiner.add(String.valueOf(b));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable char[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                char[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    char c = var3[var5];
                    stringJoiner.add("'" + String.valueOf(c) + "'");
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable double[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                double[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    double d = var3[var5];
                    stringJoiner.add(String.valueOf(d));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable float[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                float[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    float f = var3[var5];
                    stringJoiner.add(String.valueOf(f));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable int[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                int[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    int i = var3[var5];
                    stringJoiner.add(String.valueOf(i));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable long[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                long[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    long l = var3[var5];
                    stringJoiner.add(String.valueOf(l));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeToString(@Nullable short[] array) {
        if (array == null) {
            return "null";
        } else {
            int length = array.length;
            if (length == 0) {
                return "{}";
            } else {
                StringJoiner stringJoiner = new StringJoiner(", ", "{", "}");
                short[] var3 = array;
                int var4 = array.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    short s = var3[var5];
                    stringJoiner.add(String.valueOf(s));
                }

                return stringJoiner.toString();
            }
        }
    }

    public static String nullSafeConciseToString(@Nullable Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Optional) {
            Optional<?> optional = (Optional)obj;
            return optional.isEmpty() ? "Optional.empty" : "Optional[%s]".formatted(nullSafeConciseToString(optional.get()));
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0 ? "{}" : "{...}";
        } else if (obj instanceof Collection) {
            return "[...]";
        } else if (obj instanceof Map) {
            return "{...}";
        } else {
            Class type;
            if (obj instanceof Class) {
                type = (Class)obj;
                return type.getName();
            } else if (obj instanceof Charset) {
                Charset charset = (Charset)obj;
                return charset.name();
            } else if (obj instanceof TimeZone) {
                TimeZone timeZone = (TimeZone)obj;
                return timeZone.getID();
            } else if (obj instanceof ZoneId) {
                ZoneId zoneId = (ZoneId)obj;
                return zoneId.getId();
            } else if (obj instanceof CharSequence) {
                CharSequence charSequence = (CharSequence)obj;
                return StringUtils.truncate(charSequence);
            } else {
                type = obj.getClass();
                if (ClassUtils.isSimpleValueType(type)) {
                    String str = obj.toString();
                    if (str != null) {
                        return StringUtils.truncate(str);
                    }
                }

                String var10000 = type.getTypeName();
                return var10000 + "@" + getIdentityHexString(obj);
            }
        }
    }
}
