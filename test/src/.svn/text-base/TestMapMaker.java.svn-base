import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Joiner;
import com.google.common.collect.MapMaker;

public class TestMapMaker {

  /**
   * @param args
   */
  public static void main(String[] args) {
    ConcurrentMap<String, String> makeMap = new MapMaker().weakValues().maximumSize(10).makeMap();
    for (int i = 0; i < 7; i++) {
      makeMap.put("a" + i, "V" + i);
    }
    System.out.println(Joiner.on(", ").withKeyValueSeparator("=").join(makeMap));
    for (int i = 0; i < 1; i++) {
      makeMap.put("b" + i, "V" + i);
    }
    System.out.println(Joiner.on(", ").withKeyValueSeparator("=").join(makeMap));
    System.out.println(makeMap.containsKey("a1"));
    System.out.println(makeMap.containsKey("a4"));
    System.out.println(makeMap.containsKey("a5"));
    System.out.println(makeMap.get("a1"));
    System.out.println(makeMap.get("a4"));
    System.out.println(makeMap.get("a5"));

  }

}
