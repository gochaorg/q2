package xyz.cofe.q2.model

/** Тестовая сущьность */
class Bar {
    /** Конструктор по умолчанию */
    Bar(){}

    /**
     * Конструктор
     * @param id Идентификатор
     * @param name Имя
     * @param fooId Ссылка на foo
     */
    Bar(int id, String name, int fooId){
        this.id = id
        this.name = name
        this.fooId = fooId
    }

    /** Идентификатор */
    int id

    /** Имя */
    String name

    /** Ссылка на foo */
    int fooId
}
