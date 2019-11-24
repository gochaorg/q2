package xyz.cofe.q2.model

/** Тестовая сущьность */
class Foo {
    /**
     * Конструктор по умолчанию
     */
    Foo(){}

    /**
     * Конструктор
     * @param id Идентификатор
     * @param name Имя
     */
    Foo(int id,String name){
        this.id = id
        this.name = name
    }

    /** Идентификатор */
    int id

    /** Имя */
    String name
}
