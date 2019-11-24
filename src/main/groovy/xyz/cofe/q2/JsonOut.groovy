package xyz.cofe.q2

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.StreamingJsonBuilder
import ratpack.http.Response

/**
 * Отправка json ответа
 */
class JsonOut {
    /** Свойства которые необходимо исключить из вывода */
    final List<String> exludeProperties = ['class','metaClass']

    /** Переименование типов */
    final Map<String,String> typeMap = ['java.lang.String':'string']

    public <T> void write( Response output, Class<T> resultType, DataSource<T> ds ){
        if( output == null ) throw new IllegalArgumentException("output==null");
        if( resultType == null ) throw new IllegalArgumentException("resultType==null");
        if( ds == null ) throw new IllegalArgumentException("ds==null");

        StringWriter sw = new StringWriter()
        write(sw,resultType,ds)

        output.send('application/json', sw.toString() )
    }

    public <T> void write( Writer output, Class<T> resultType, DataSource<T> ds ){
        if( output == null ) throw new IllegalArgumentException("output==null");
        if( resultType == null ) throw new IllegalArgumentException("resultType==null");
        if( ds == null ) throw new IllegalArgumentException("ds==null");

        def propFilter = { MetaProperty prop ->
            if( exludeProperties==null )return true
            if( prop.name in exludeProperties )return false
            return true
        }
        def props = resultType.metaClass.properties.findAll( propFilter )

        JsonFactory factory = new JsonFactory()
        factory.setCodec( new ObjectMapper() )

        JsonGenerator out = factory.createGenerator(output)
        out.useDefaultPrettyPrinter()

        def writeMeta = {
            out.writeFieldName("meta")
            out.writeStartObject()

            out.writeFieldName("type")
            out.writeString(resultType.name)

            out.writeFieldName("columns")
            out.writeStartArray()
            for( MetaProperty prop : props ){
                out.writeObject(
                    [column:
                         [name: prop.name
                         ,type: typeMap.getOrDefault( prop.type.name, prop.type.name )
                         ]
                    ]
                )
            }
            out.writeEndArray()
            out.writeEndObject()
        }

        def writeData = {
            out.writeFieldName("data")
            out.writeStartArray()
            ds.fetch({ row ->
                def kv = [:]
                for( MetaProperty prop : props ){
                    def key = prop.name
                    def val = prop.getProperty(row)
                    kv[key] = val
                }
                out.writeObject(kv)
            })
            out.writeEndArray()
        }

        out.writeStartObject()
        writeMeta()
        writeData()
        out.writeEndObject()

        out.flush()
    }
}
