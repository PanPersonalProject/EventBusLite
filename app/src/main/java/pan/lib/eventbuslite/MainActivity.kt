package pan.lib.eventbuslite

import android.content.Intent
import android.os.Bundle
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
        bt_secondActivity.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    fun testMethod(event: TestEvent) {
        tv_content.text = event.str
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusLite.getInstance().unregister(this);
    }
}
