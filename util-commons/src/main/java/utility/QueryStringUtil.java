package utility;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;


public class QueryStringUtil {
    public static Multimap<String, String> getParams(String uri){
        try {
            List<NameValuePair> params = URLEncodedUtils.parse(new URI(uri), Charset.defaultCharset());
            HashMultimap map = HashMultimap.create();
            for(NameValuePair nvp : params){
                map.put(nvp.getName(), nvp.getValue());
            }
            return map;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
