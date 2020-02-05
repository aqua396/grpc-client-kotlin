package tutorial.grpc.client.kotlin

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.lang.Exception
import io.grpc.ManagedChannel
import android.app.Activity
import io.grpc.ManagedChannelBuilder
import java.lang.ref.WeakReference
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    // TODO: replace view model
    private var send: Button? = null
    private var message: EditText? = null
    private var host: EditText? = null
    private var port: EditText? = null
    private var response: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        send = findViewById(R.id.send_button)
        message = findViewById(R.id.message_edit_text)
        host = findViewById(R.id.host_edit_text)
        port = findViewById(R.id.port_edit_text)
        response = findViewById(R.id.grpc_response_text)
    }

    fun sendMessage(view: View) {
        val inputMethodManager
                = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(host?.windowToken, 0)
        send?.isEnabled = false
        response?.text = ""
        GrppcTask(activity = this).execute(
            host?.text.toString(),
            port?.text.toString(),
            message?.text.toString()
        )
    }

    // TODO: replace coroutine
    inner class GrppcTask(activity: Activity) : AsyncTask<String, Int, String>() {
        private var activityReference: WeakReference<Activity>? = null
        private var channel: ManagedChannel? = null

        init {
            activityReference = WeakReference(activity)
        }

        override fun doInBackground(vararg params: String?): String {
            val host = params[0]
            val portStr = params[1]
            val message = params[2]

            var port = 0
            portStr?.let {
                port = if (TextUtils.isEmpty(it)) 0 else Integer.valueOf(portStr)
            }

            try {
                channel =
                    ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext(true).build()
                val stub: GreetGrpc.GreetBlockingStub = GreetGrpc.newBlockingStub(channel)
                val request: GreetService.Question
                        = GreetService.Question.newBuilder().setMessage(message).build()
                val replay: GreetService.Reply = stub.say(request)
                return replay.message
            } catch (e: Exception) {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                pw.flush()
                return String.format("Failed... : %n%s", sw)
            }
        }

        override fun onPostExecute(result: String?) {
            try {
                channel?.shutdown()?.awaitTermination(1, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            val activity = activityReference?.get() ?: return
            val response: TextView = activity.findViewById(R.id.grpc_response_text)
            val send: Button = activity.findViewById(R.id.send_button)
            response.text = result
            send.isEnabled = true
        }
    }
}
