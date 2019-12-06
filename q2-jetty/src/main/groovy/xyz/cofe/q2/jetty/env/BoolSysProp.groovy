package xyz.cofe.q2.jetty.env

/**
 * Булево системное свойство
 */
class BoolSysProp {
    /**
     * Имя свойства
     */
    final String name

    /**
     * Значение по умолчанию
     */
    final boolean defaultValue

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
    BoolSysProp(String name, boolean defValue,String desc=null){
        this.name = name
        this.defaultValue = defValue
        this.description = desc
        if( name == null ) throw new IllegalArgumentException("name==null");
    }

    boolean asBoolean(){
        'true'.equalsIgnoreCase(System.getProperty(name,defaultValue ? 'true' : 'false'))
    }
}