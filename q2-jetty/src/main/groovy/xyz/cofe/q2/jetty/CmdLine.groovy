package xyz.cofe.q2.jetty

/**
 * Парсинг параметров командной строки и представление их ввиде key/valye. <br>
 * Каждый ключ начинаеться со знака минус, за которым следует имя ключа <br>
 * Следующий параметр интерпретируется как значение ключа <br><br>
 *
 * Пример
 * <pre>
 * -debug true
 * </pre>
 *
 * Данный объект будет содержать значение debug = 'true'
 *
 * <pre>
 * def cmdl = new CmdLine( '-debug', 'true' )
 * println ((cmdl.debug ?: false) as boolean)
 * </pre>
 */
class CmdLine {
    private String[] args
    private Map<String,String> map = [:]

    /**
     * Аргументы командной строки
     * @param args аргументы
     */
    CmdLine(String[] args){
        this.args = args!=null ? args : []

        List<String> largs = new ArrayList<>(Arrays.asList(args))
        while( largs.size()>0 ){
            String k = largs.remove(0 as int)
            if( k!=null && k.length()>1 && k[0]=='-' && k[1]!='-' ){
                if( largs.size()>0 ){
                    String v = largs.remove(0 as int)
                    if( v!=null ){
                        map[ k.substring(1) ] = v
                    }
                }
            }else{
                break
            }
        }
    }

    def propertyMissing(String name){
        return map[name]
    }
}
