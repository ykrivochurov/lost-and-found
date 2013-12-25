package ru.poteriashki.laf.web.config.jsonview;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;

/**
 * @author y.bulkin
 */
public class ViewAwareJsonMessageConverter extends MappingJackson2HttpMessageConverter {

    public ViewAwareJsonMessageConverter() {

        super();
        ObjectMapper defaultMapper = new ObjectMapper();
        defaultMapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        setObjectMapper(defaultMapper);
    }

    @Override
    protected void writeInternal(Object object,
                                 HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        if (object instanceof DataView && ((DataView) object).hasView()) {
            writeView((DataView) object, outputMessage);
        } else {
            super.writeInternal(object, outputMessage);
        }
    }

    protected void writeView(DataView view,
                             HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
        ObjectMapper mapper = getMapperForView(view.getView());

        JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
        try {
            mapper.writerWithView(view.getView()).writeValue(jsonGenerator, view.getData());
        } catch (IOException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    private ObjectMapper getMapperForView(Class<?> view) {

        ObjectMapper mapper = getObjectMapper();
        mapper = mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        return mapper;
    }

}
