package com.jayway.jsonpath.internal.spi.converter;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.converter.ConversionProvider;
import com.jayway.jsonpath.spi.converter.Converter;

import java.util.HashMap;

public class DefaultConversionProvider implements ConversionProvider {

    private HashMap<Class<?>, HashMap<Class<?>, Converter>> converters = new HashMap<Class<?>, HashMap<Class<?>, Converter>>();

    public DefaultConversionProvider(){
        addConverters(new NumberConverter());
        addConverters(new StringConverter());
        addConverters(new DateConverter());
    }

    public void addConverters(ConverterBase converter) {
        for (Converter.ConvertiblePair convertible : converter.getConvertibleTypes()) {
            if(!converters.containsKey(convertible.getTargetType())){
                converters.put(convertible.getTargetType(), new HashMap<Class<?>, Converter>());
            }
            converters.get(convertible.getTargetType()).put(convertible.getSourceType(), converter);
        }
    }

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        HashMap<Class<?>, Converter> targetConverters = converters.get(targetType);
        return targetConverters != null && targetConverters.containsKey(sourceType);
    }

    @Override
    public <T> T convert(Object source, Class<T> targetType, Configuration configuration) {
        if(source == null){
            return null;
        }
        HashMap<Class<?>, Converter> targetConverters = converters.get(targetType);
        if(targetConverters != null){
            Converter converter = targetConverters.get(source.getClass());
            if(converter != null){
                return (T)converter.convert(source, source.getClass(), targetType, configuration);
            }
            converter = targetConverters.get(Object.class);
            if(converter != null){
                return (T)converter.convert(source, source.getClass(), targetType, configuration);
            }
        }
        return (T)source;
    }
}
