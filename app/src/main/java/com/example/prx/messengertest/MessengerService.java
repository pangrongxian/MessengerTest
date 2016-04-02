package com.example.prx.messengertest;

import com.example.prx.messengertest.utile.MyConstants;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


/**
 * Created by Administrator on 2016/3/31.
 */
public class MessengerService extends Service {

    private static final String TAG = "MessengerService";

    /**
     * 1.创建一个Handler
     */
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstants.MSG_FROM_CLIENT:
                    Log.d(TAG, "receive msg from Client:"+msg.getData().get("msg"));
                    Log.d(TAG, "收到客户端发送过来的消息");

                    /**
                     * 收到客户端的消息后，回复客户端
                     */
                    Messenger messenger = msg.replyTo;
                    Message message = new Message().obtain(null,MyConstants.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply","嗯，你的消息我已经收到了，稍后会回复你。");
                    message.setData(bundle);
                    try {
                        Log.d(TAG, "收到客户端的消息后，回复客户端");
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * 2.通过Handler创建Messenger对象
     *
     * Messenger的作用是：将客户端发送的消息传递给MessengerHandler处理
     */
    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    /**
     *
     * @param intent 3.在Service的onBind（）方法中返回这个Messenger对象底层的Binder即可
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}

