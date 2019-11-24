package xyz.cofe.q2

import xyz.cofe.q2.model.Bar
import xyz.cofe.q2.model.Foo

/**
 * Данные
 */
@Singleton
class RootData {
    final DataSource<Foo> foo = new DataSource<>(
        [   new Foo(1,"fooA"),
            new Foo(2,"fooB"),
            new Foo(3,"fooC")
        ]
    )

    final DataSource<Bar> bar = new DataSource<>(
        [
            new Bar( 3,"barA",1), new Bar( 4,"barB",1),
            new Bar( 5,"barC",2), new Bar( 6,"barD",2),
            new Bar( 7,"barE",1), new Bar( 8,"barF",5),
            new Bar( 9,"barG",1), new Bar(10,"barH",5),
            new Bar(11,"barI",2), new Bar(12,"barJ",3),
        ]
    )
}
