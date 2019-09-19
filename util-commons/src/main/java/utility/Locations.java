package utility;

import com.google.common.collect.ImmutableSet;

import java.util.Locale;

public class Locations {

    private static final ImmutableSet<String> COUNTRY_CODES = ImmutableSet.copyOf(Locale.getISOCountries());

    public static boolean isValidCountryCode(String countryCode) {
        return countryCode != null && !countryCode.isEmpty() && COUNTRY_CODES.contains(countryCode.toUpperCase());
    }
}
