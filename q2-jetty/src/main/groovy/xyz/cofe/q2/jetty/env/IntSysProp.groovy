package xyz.cofe.q2.jetty.env

/**
 * Числовое (целое) системное свойство
 */
class IntSysProp {
    /**
     * Имя свойства
     */
    final String name

    /**
     * Значение по умолчанию
     */
    final int defaultValue

    /**
     * Описание
     */
    final String description

    /**
     * Конструктор
     * @param name Имя свойства
     * @param defValue Значение по умолчанию
     * @param desc description
     */
    IntSysProp(String name, int defValue,String desc=null){
        this.name = name
        this.defaultValue = defValue
        this.description = desc
        if( name == null ) throw new IllegalArgumentException("name==null");
    }

    def asType(Class target){
        switch( target ){
            case Number:
            case int:
            case Integer:
                String v = System.getProperty(name,Integer.toString(defaultValue))
                try {
                    return Integer.parseInt(v)
                } catch ( NumberFormatException ex ){
                    System.err.println("can't parse sysproperty: $name as int")
                }
                return defaultValue
        }
    }

    boolean asBoolean(){
        'true'.equalsIgnoreCase(System.getProperty(name,defaultValue ? 'true' : 'false'))
    }
}
