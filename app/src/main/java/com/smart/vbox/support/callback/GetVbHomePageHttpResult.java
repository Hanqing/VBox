package com.smart.vbox.support.callback;

import com.grpc.xbox.xbox;

import java.util.ArrayList;


/**
 *
 * @author lhq
 * created at 2015/10/31 16:48
 */
public interface GetVbHomePageHttpResult {

    void onHomePageResult(ArrayList<xbox.vObjectGroup> result);

}
