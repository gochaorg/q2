package xyz.cofe.q2.proto

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
//import ratpack.http.Response
import xyz.cofe.q2.DataSource
import xyz.cofe.q2.meta.Column

/**
 * Отправка json ответа
 */
class JsonOut {
    /** Переименование типов */
    final Map<String,String> typeMap = ['java.lang.String':'string']

    /**
     * Запись данных в writer
     * @param output куда записывать json данные
     * @param ds источник данных
     */
    public <T> void write( Writer output, DataSource<T> ds ){
        if( output == null ) throw new IllegalArgumentException("output==null");
        if( ds == null ) throw new IllegalArgumentException("ds==null");

        JsonFactory factory = new JsonFactory()
        factory.setCodec( new ObjectMapper() )

        JsonGenerator out = factory.createGenerator(output)
        out.useDefaultPrettyPrinter()

        def writeMeta = {
            out.writeFieldName("meta")
            out.writeStartObject()

            out.writeFieldName("columns")
            out.writeStartArray()
            for( Column col: ds.columnsInstance ){
                out.writeObject(
                    [column:
                         [name: col.name
                         ,type: typeMap.getOrDefault( col.type.name, col.type.name )
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
            ds.pick({ row ->
                def kv = [:]
                for( Column col : ds.columnsInstance ){
                    def key = col.name
                    def val = row[key]
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
