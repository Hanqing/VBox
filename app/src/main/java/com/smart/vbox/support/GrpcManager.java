package com.smart.vbox.support;

import com.grpc.vbox.VBox_ServiceGrpc;

import io.grpc.ChannelImpl;
import io.grpc.transport.okhttp.OkHttpChannelBuilder;

/**
 * Created by Hanqing on 2015/11/24.
 */
public class GrpcManager {

    public static String HOST = "172.24.1.208";
    public static int PORT = 8202;

    private ChannelImpl mChannel;
    private VBox_ServiceGrpc.VBox_ServiceBlockingStub vBoxStub;

    private static class GrpcManagerHolder {
        private static final GrpcManager INSTANCE = new GrpcManager();
    }

    private GrpcManager() {
        if (mChannel == null) {
            mChannel = OkHttpChannelBuilder.forAddress(HOST, PORT).build();
            vBoxStub = VBox_ServiceGrpc.newBlockingStub(mChannel);
        }
    }

    public static final GrpcManager getInstance() {
        return GrpcManagerHolder.INSTANCE;
    }

    public VBox_ServiceGrpc.VBox_ServiceBlockingStub getStub() {
        if (mChannel == null) {
            mChannel = OkHttpChannelBuilder.forAddress(HOST, PORT).build();
        }
        if (vBoxStub == null) {
            vBoxStub = VBox_ServiceGrpc.newBlockingStub(mChannel);
        }
        return vBoxStub;
    }

}
