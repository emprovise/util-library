package com.emprovise.util.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Map;

/**
 * Properties Utility'.
 *
 */
public class PropertiesUtil {

    public Collection<String> values(Properties props) {
        if (props == null) {
            return new ArrayList<String>(0);
        }
        List<String> values = new ArrayList<String>();
        for (String propertyName : props.stringPropertyNames()) {
            String value = props.getProperty(propertyName);
            values.add(value);
        }
        return values;
    }

    public boolean isEmpty(Properties props) {
        if (props == null) {
            return true;
        }
        if (props.isEmpty()) {
            return true;
        }
        return false;
    }

    public static Properties loadProperties(URL url) {
        try {
            return loadProperties(url.openStream());
        } catch (IOException e) {
            throw new RuntimeException("error reading file", e);
        }
    }

    public static Properties loadProperties(InputStream is) {

        try {
            Properties properties = new Properties();

            // Make sure the properties stream is valid
            if (is != null) {
                properties.load(is);
            }

            return properties;

        } catch (IOException e) {
            throw new RuntimeException("error reading file", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("error reading file", e);
            }
        }
    }


    /**
     * Loads the Properties from Property file in the classpath
     *
     * @param propsName Name of the property file in the class path
     * @return Properties fetched from the specified property file
     * @throws IOException
     */
    public static Properties loadProperties(String propsName) throws IOException {
        Properties props = new Properties();
        URL url = ClassLoader.getSystemResource(propsName);
        props.load(url.openStream());
        return props;
    }

    /**
     * Loads the Properties from Property file using the specified class loader
     *
     * @param propsName Name of the property file to load
     * @param loader    Instance of the class loader relative to which the property file needs to be loaded
     * @return Properties fetched from the specified property file
     * @throws IOException
     */
    public static Properties loadProperties(String propsName, ClassLoader loader) throws IOException {

        Properties props = new Properties();
        InputStream in = loader.getResourceAsStream(propsName);
        props.load(in);
        return props;
    }

    /**
     * Loads the Properties from Property File
     *
     * @param propsFile Name of the property file to load
     * @return Properties
     * Properties fetched from the specified property file
     * @throws IOException
     */
    public static Properties loadProperties(File propsFile) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(propsFile);
        props.load(fis);
        fis.close();
        return props;
    }

    /**
     * Gets all the System and Environment properties in a formatted string.
     *
     * @return Formatted {@link String} containing all the system and environment properties.
     */
    public static String printAllProperties() {

        StringBuilder builder = new StringBuilder("\n\n");
        builder.append("  ------------------------------------------------------------------------ \n");
        builder.append("                              PROPERTIES                                   \n");
        builder.append("  ------------------------------------------------------------------------ \n\n");
        builder.append(" System Properties: \n\n");

        Properties p = System.getProperties();
        Enumeration<Object> keys = p.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = (String) p.get(key);
            builder.append(key + ": " + value);
            builder.append("\n");
        }

        builder.append("\n------------------------------------------------------------------------ \n");
        builder.append("\n Environment Properties: \n\n");
        Map<String, String> variables = System.getenv();

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            builder.append(name + ": " + value);
            builder.append("\n");
        }

        builder.append("\n------------------------------------------------------------------------ \n");
        return builder.toString();
    }
}
