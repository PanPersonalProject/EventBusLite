package pan.lib.eventbuslite

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pan.lib.eventbus.EventBusLite
import pan.lib.eventbus.Subscribe
import pan.lib.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBusLite.getInstance().register(this);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    fun testMethod(event: TestEvent) {
        Toast.makeText(this, event.str, Toast.LENGTH_SHORT).show();
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusLite.getInstance().unregister(this);
    }
}
