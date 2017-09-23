package centercontrolleros.com.icentercontrolleros.ui.untils;

import java.util.ArrayList;
import java.util.List;

import centercontrolleros.com.icentercontrolleros.ui.model.MyTimeOut;

/**
 * Created by VuDuc on 9/8/2017.
 */

public class MyTimeOutData {
    public static List<MyTimeOut> addTimeOutData(){
        List<MyTimeOut> myTimeOuts = new ArrayList<>();

        MyTimeOut myTimeOut1 = new MyTimeOut("15 second",false);
        myTimeOuts.add(myTimeOut1);

        MyTimeOut myTimeOut2 = new MyTimeOut("30 second",false);
        myTimeOuts.add(myTimeOut2);

        MyTimeOut myTimeOut3 = new MyTimeOut("1 minute",false);
        myTimeOuts.add(myTimeOut3);

        MyTimeOut myTimeOut4 = new MyTimeOut("2 minute",false);
        myTimeOuts.add(myTimeOut4);

        MyTimeOut myTimeOut5 = new MyTimeOut("10 minute",false);
        myTimeOuts.add(myTimeOut5);

        MyTimeOut myTimeOut6 = new MyTimeOut("30 minute",false);
        myTimeOuts.add(myTimeOut6);

        return myTimeOuts;
    }
}
