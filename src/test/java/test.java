import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * @author cxy
 * @date 2021/01/08
 */
public class test {

    public static Collection<String> collection = new HashSet<>();
    private final static String a = "a";
    private final static String b = "b";
    private final static String c = "c";
    private final static String d = "d";

    public static void main(String[] args) {
        collection.add(a);
        collection.add(b);
        collection.add(c);
        collection.add(d);
//        Iterator it = collection.iterator();
//        while (it.hasNext()) {
//            String str = ((String) it.next());
//            if (str.equals("a")) {
//                collection.remove(a);
//            }
//        }
//        for (String str : collection) {
//            if (str.equals(a)) {
////                collection.removeIf(s -> s.equals(a));
//                remove();
//            }
//        }
        for (int i = 0; i < collection.size(); i++) {
            System.out.println(collection.toArray()[i]);
            if (collection.toArray()[i].equals(b)){
                collection.remove(b);collection.toArray();
                i--;
            }
        }
    }


    public static void remove() {
        collection.removeIf(s -> s.equals(a));
    }
}
