package utility;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Lists.newArrayList;

public class JsonConversionUtil {

    public JsonNode convertXmlToJsonNode(String xmlString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser jsonParser = factory.createJsonParser(xmlString);
        return mapper.readTree(jsonParser);
    }

    public Multimap<String, String> convertXmlToMultiMap(String xmlString, List<String> elements, Map.Entry entry) throws IOException {
        return convertJsonNodeToMap(convertXmlToJsonNode(xmlString), elements, entry);
    }

    public JSONObject convertXmlToJsonObject(String xmlString) throws JSONException {
        return XML.toJSONObject(xmlString);
    }

    public Multimap<String, String> convertJsonNodeToMap(JsonNode jsonNode, List<String> elements, Map.Entry entry) {
        Multimap<String, String> multiMap = HashMultimap.create();
        if (entry == null) {
            return multiMap;
        }

        if (elements == null) {
            elements = newArrayList();
        }

        String key = entry.getKey().toString();
        String value = entry.getValue().toString();

        if (isNullOrEmpty(key) && isNullOrEmpty(value)){
            return multiMap;
        }

        JsonNode features = jsonNode;
        for (String temp: elements) {
            if (features != null) {
                features = features.get(temp);
            }
        }

        if (features == null) {
            return multiMap;
        }

        Iterator<JsonNode> childElements = features.elements();
        while (childElements.hasNext()) {
            JsonNode route = childElements.next();
            if (route.get(key) != null && route.get(value) != null) {
                multiMap.put(route.get(key).toString(), route.get(value).toString());
            }
        }

        return multiMap;
    }
}