package utility;

public class QueryStringBuilderUtil {

    public static final String QUERY_STRING_PREFIX = "?";
    public static final String QUERY_PARAMETER_SEPARATOR = "&";

    public static void addQueryParameterToQueryString(StringBuffer buffer, Object parameter, String parameterName) {
        if (isRequiredToAddToQueryString(parameter)) {
            if (!isQueryStringSeparatorExists(buffer)) {
                buffer.append(QUERY_STRING_PREFIX);
            }
            if (isAnyQueryParameterExists(buffer)) {
                buffer.append(QUERY_PARAMETER_SEPARATOR);
            }
            buffer.append(parameterName + "={" + parameterName + "}");
        }
    }

    public static void addQueryParameterAndValueToQueryString(StringBuffer buffer, Object parameter, String parameterName) {
        if (isRequiredToAddToQueryString(parameter)) {
            if (!isQueryStringSeparatorExists(buffer)) {
                buffer.append(QUERY_STRING_PREFIX);
            }
            if (isAnyQueryParameterExists(buffer)) {
                buffer.append(QUERY_PARAMETER_SEPARATOR);
            }
            buffer.append(parameterName + "=" + parameter + "");
        }
    }

    private static boolean isAnyQueryParameterExists(StringBuffer buffer) {
        return buffer.toString().contains("=");
    }

    private static boolean isQueryStringSeparatorExists(StringBuffer buffer) {
        return buffer.toString().contains(QUERY_STRING_PREFIX);
    }

    private static boolean isRequiredToAddToQueryString(Object parameter) {
        return parameter == null || (parameter instanceof Boolean && !(boolean) parameter) ? false : true;
    }
}
