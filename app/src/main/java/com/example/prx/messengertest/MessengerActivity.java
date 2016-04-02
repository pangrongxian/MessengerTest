package com.example.prx.messengertest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.prx.messengertest.utile.MyConstants;

/**
 * 1.客户端中，首先要绑定服务端的Service
 *
 * 2.绑定服务端成功后，用服务端返回的IBinder对象创建一个Messenger对象
 *
 * 3.通过这个Messenger对象就可以像服务端发送消息了
 */

public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";

    private Messenger mMessenger;

    /**
     * 2.绑定远程服务端成功后，服务端返回IBinder对象
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /**
             * 3.使用IBinder对象创建Messenger对象，并使用Messenger对象向服务端发送消息
             */
            Log.d(TAG, "使用IBinder对象创建Messenger对象");
            mMessenger = new Messenger(service);
            Message message = new Message().obtain(null, MyConstants.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg","Hello this is client");
            Log.d(TAG, "使用Messenger对象向服务端发送消息");
            message.setData(data);

            /**
             * 注意下面这句。
             * 当客户端发送消息的时候，需要把接收服务端的回复消息的Messenger通过replyTo参数传递给服务端
             */
            message.replyTo = mGetMessenger;

            try {
                Log.d(TAG, "向服务端发送msg");
                mMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    private Messenger mGetMessenger = new Messenger(new MessengerHandler());

    /**
     * 接收服务端的回复消息
     */
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyConstants.MSG_FROM_SERVICE:
                    Log.d(TAG, "接收服务端的回复消息");
                    Log.d(TAG, "receive msg from Service:"+msg.getData().get("reply"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,MessengerService.class);
        /**
         * 1.绑定远程进程Service服务端，需要三个参数
         *  参数1：intent对象
         *  参数2：绑定（连接）服务端成功后，获取服务端返回的binder对象
         *  参数3：绑定标志
         */
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "绑定（连接）服务端成功");
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }

}
