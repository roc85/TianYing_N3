package com.xyxl.tianyingn3.global;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by Administrator on 2017/11/14 15:58
 * Version : V1.0
 * Introductions : otto总线事件对象
 */

public class AppBus {
    private static Bus bus;

    public static Bus getInstance() {
        if (bus == null) {
            bus = new Bus(ThreadEnforcer.ANY);

        }
        return bus;
    }
}
