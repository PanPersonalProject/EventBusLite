package pan.lib.eventbuslite

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pan.lib.eventbus.EventBusLite
import pan.lib.eventbus.Subscribe
import pan.lib.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBusLite.getInstance().register(this);
        button.setOnClickListener {
            EventBusLite.getInstance().post(TestEvent())
        }
        button2.setOnClickListener {
            EventBusLite.getInstance().post(TestEvent2())
        }
        bt_secondActivity.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun testMethod(event: TestEvent) {
        tv_content.text = event.str
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun test2(event: TestEvent) {
        Toast.makeText(this, event.str, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun event2(event: TestEvent2) {
        Looper.prepare()
        Toast.makeText(this, """子线程 ${Thread.currentThread().name}""", Toast.LENGTH_SHORT).show();
        Looper.loop()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusLite.getInstance().unregister(this);
    }
}
