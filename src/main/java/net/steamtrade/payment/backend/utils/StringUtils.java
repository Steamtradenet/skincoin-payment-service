package net.steamtrade.payment.backend.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.steamtrade.payment.backend.exceptions.*;
import net.steamtrade.payment.backend.exceptions.Error;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by sasha on 09.12.15.
 */
public class StringUtils {

    private static final int STACK_TRACE_MAX_LENGTH = 10000;

    public static String stackTraceToString(Throwable th) {
        if(th != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            th.printStackTrace(pw);
            return abbreviateString(sw.toString(), STACK_TRACE_MAX_LENGTH);
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        if(str == null) {
            return true;
        }
        return str.trim().isEmpty();
    }

    public static boolean isAnyEmpty(String... str) {
        for (String s: str) {
            if(s == null || s.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static String getEmptyIfNull(String str) {
        return isEmpty(str)?"":str;
    }


    public static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> List<T> fromArrayJson(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        try {
            return mapper.readValue(json, TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> fromArrayJson(String path, String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        String[] parts = path.split("/");

        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonNode node = rootNode;
        for (int i=0; i<parts.length; i++) {
            if (node.has(parts[i])) {
                node = node.path(parts[i]);
            } else {
                throw new RuntimeException("Incorrect path '"+path+"'");
            }
        }
        try {
            return mapper.readValue(node.toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static String getJsonAttributeValue(String attributeName, String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        JsonNode node = rootNode.path(attributeName);
        if (node != null) {
            return node.textValue();
        }
        return null;
    }

    public static <T> T getJsonAttributeValue(String path, String json, Class<T> clazz) {

        ObjectMapper mapper = new ObjectMapper();
        String[] parts = path.split("/");

        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonNode node = rootNode;
        for (int i=0; i<parts.length; i++) {
            if (node.has(parts[i])) {
                node = node.path(parts[i]);
            } else {
                throw new RuntimeException("Incorrect path '"+path+"'");
            }
        }

        if (clazz == String.class) {
            return clazz.cast(node.textValue());
        } else if (clazz == Boolean.class) {
            return clazz.cast(node.booleanValue());
        } else {
            throw new RuntimeException("Can't convert result to '"+clazz.getName()+"'");
        }
    }

    public static <T> T mapJsonAttributeToObject(String path, String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        String[] parts = path.split("/");

        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonNode node = rootNode;
        for (int i=0; i<parts.length; i++) {
            if (node.has(parts[i])) {
                node = node.path(parts[i]);
            } else {
                throw new RuntimeException("Incorrect path '"+path+"'");
            }
        }
        try {
            return mapper.readValue(node.toString(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String abbreviateString(String input, int maxLength) {
        if (isEmpty(input) || input.length() <= maxLength)
            return input;
        else
            return input.substring(0, maxLength-2) + "..";
    }

    public static String join(String delimiter, String ... values) {
        return String.join(delimiter, values);
    }

    public static String join(String delimiter, List<String> values) {
        return String.join(delimiter, values);
    }



    public static String getSqlParameters(int count) {
        if (count > 0) {
            String result = new String(new char[count]).replace("\0", "?,");
            return result.substring(0, result.length()-1);
        }
        return null;
    }

    public static String toString(Object object) {
        return object != null ? object.toString() : "";
    }

    public static List<Integer> parseAsListOfIntegers(String str, String delimiter) {
        return Arrays.asList(str.split(delimiter)).stream().map(Integer::valueOf).collect(Collectors.toList());
    }

    public static List<Long> parseAsListOfLong(String str, String delimiter) {
        return Arrays.asList(str.split(delimiter)).stream().map(Long::valueOf).collect(Collectors.toList());
    }


    public static List<String> parseAsList(String str, String delimiter) {
        return Arrays.asList(str.split(delimiter));
    }


    public static String toString(byte[] bytes) {
        if (bytes == null) return null;
        Scanner scanner = new Scanner(new ByteArrayInputStream(bytes));
        scanner.useDelimiter("\\Z"); //To read all scanner content in one String
        String data = "";
        if (scanner.hasNext())
            data = scanner.next();

        return data;
    }

    public static <T> T parseValue(String value, Class<T> clazz, T def) {
        if (value != null) {
            if (clazz == Boolean.class) {
                return clazz.cast(Boolean.valueOf(value));
            } else if (clazz == BigDecimal.class) {
                return clazz.cast(new BigDecimal(value));
            } else if (clazz == BigInteger.class) {
                return clazz.cast(new BigInteger(value));
            } else if (clazz == Double.class) {
                return clazz.cast(new BigDecimal(value).doubleValue());
            } else if (clazz == String.class) {
                return clazz.cast(value);
            } else if (clazz == Integer.class) {
                return clazz.cast(Integer.valueOf(value));
            } else if (clazz == Long.class) {
                return clazz.cast(Long.valueOf(value));
            } else {
                throw new RuntimeException("Unsupported class cast exception to " + clazz.getCanonicalName());
            }
        }
        return def;
    }

    public static <T> T getRequiredParamValue(String paramName, Map<String, String[]> filter, Class<T> clazz) throws AppException {
        if (filter.get(paramName) != null) {
            return parseValue(filter.get(paramName)[0], clazz, null);
        }
        throw new AppException(Error.INCORRECT_FORMAT_JSON, "Parameter '"+paramName+"' is not found");
    }

    public static <T> T getOptionalParamValue(String paramName, Map<String, String[]> filter, Class<T> clazz) throws AppException {
        if (filter.get(paramName) != null) {
            return parseValue(filter.get(paramName)[0], clazz, null);
        }
        return null;
    }

}
