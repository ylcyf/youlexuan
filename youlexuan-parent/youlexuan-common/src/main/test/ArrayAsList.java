import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/11/4 15:14
 */
public class ArrayAsList {

    @Test
    public void test(){
        Long[] id = new Long[5];
        for(int i = 0; i < 5; i++){
            id[i] = i + 10L;
        }
        List<Long> list = new ArrayList<>();
        Collections.addAll(list, id);
        System.out.println(list);
    }

}
