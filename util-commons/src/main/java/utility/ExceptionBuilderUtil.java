package utility;

public class ExceptionBuilderUtil {
/*import com.deere.api.axiom.generated.v3.Errors;
import com.deere.axiom.commons.exceptions.DiagnosticException;
import com.deere.axiom.platform.exception.rest.ServiceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

import static com.deere.axiom.platform.exception.ValidationErrorMessageUtility.createValidationErrorMessageType;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

public class ExceptionBuilderUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionBuilderUtil.class);

    public static DiagnosticException addDetailsToException(String invalidValue, DiagnosticException exception, String field, String code) {
        exception.getValidationErrorMessageTypes().add(createValidationErrorMessageType(field, code, invalidValue));
        return exception;
    }

    public static String buildUriWithValue(String uriTemplate, String value) {
        return fromUriString(uriTemplate).buildAndExpand(value).toString();
    }

    public static RuntimeException runtimeCauseOf(RuntimeException e) {
        if (e.getCause() instanceof RuntimeException) {
            return (RuntimeException) e.getCause();
        } else {
            return e;
        }
    }

    public static Errors getErrorsObject(RestClientException rce) {
        try {
            String xmlString = ((HttpClientErrorException) rce).getResponseBodyAsString();
            JAXBContext context = JAXBContext.newInstance(Errors.class);
            Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
            return (Errors) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

        } catch (JAXBException jaxbException) {
            LOGGER.error(jaxbException.getMessage());
            throw new ServiceUnavailableException();
        }
    }*/
}
