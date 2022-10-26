package org.jellyfin.androidtv.ui.shared;

import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import org.jellyfin.androidtv.constant.CustomMessage;

//@Deprecated(message = "Use FragmentActivity instead", replaceWith = @ReplaceWith(expression = "FragmentActivity", imports = {}))
public abstract class BaseActivity extends AppCompatActivity {
    private KeyListener keyListener;
    private MessageListener messageListener;

    public BaseActivity() {
        super();
    }

    public BaseActivity(int fragmentContentView) {
        super(fragmentContentView);
    }

    public void registerKeyListener(KeyListener listener) {
        keyListener = listener;
    }

    public void registerMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public void removeMessageListener() {
        messageListener = null;
    }

    public void sendMessage(CustomMessage message) {
        if (messageListener != null) {
            messageListener.onMessageReceived(message);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return keyListener != null ? keyListener.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event) : super.onKeyUp(keyCode, event);
    }
}
