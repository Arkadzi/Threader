package me.arkadzi.threader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import me.arkadzi.threader.adapter.Adapter
import me.arkadzi.threader.model.Page
import me.arkadzi.threader.model.PageProcessor
import me.arkadzi.threader.model.QueueElement
import me.arkadzi.threader.model.Status
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var totalsuccess = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val url = "https://www.bbc.com"

        rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            adapter = Adapter(layoutInflater)
        }

        val pageProcessor = PageProcessor()
        val page = Page(url, pageProcessor, "ukraine")

        Thread {
            try {
                bfs(page)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.run()
    }

    @Throws(InterruptedException::class)
    fun bfs(start: QueueElement): List<QueueElement>? {
        val service = Executors.newFixedThreadPool(100)
        val path = ArrayList<QueueElement>()
        val used = LinkedList<QueueElement>()
        val queue = LinkedList<QueueElement>()
        queue.add(start)
        used.add(start)
        val startTime = System.currentTimeMillis()
        service.invokeAll(listOf(generateCallable(start)))
        while (!queue.isEmpty()) {
            Log.e("asdasd", "1")
            val node = queue.pop()
            path.add(node)

            Log.e("asdasd", "2")
            val uniqueElements = node.getStates()
                    .filter { !used.contains(it) }
                    .onEach { it.parent = node }
            Log.e("asdasd", "4")

            val list = uniqueElements
                    .map { generateCallable(it) }

            service.invokeAll(list)
            totalsuccess += uniqueElements.count { queueElement -> (queueElement as Page).status === Status.SUCCESS }
            if (finalState(node)) {
                Log.e("asdasd", "final " + (System.currentTimeMillis() - startTime) + " " + totalsuccess)
                return path
            }
            uniqueElements.forEach { queueElement ->
                queue.add(queueElement)
                used.add(queueElement)
            }
        }
        return null
    }

    private fun generateCallable(queueElement: QueueElement): Callable<QueueElement> {
        return Callable {
            queueElement.process()
            queueElement as Page
            Log.e("asdasd", "${queueElement.status} ${queueElement.error} ${queueElement.url} ${queueElement.layer} ${queueElement.urls} ${queueElement.analyzeTime}")
            queueElement
        }
    }


    private fun finalState(queueElement: QueueElement): Boolean {
        return totalsuccess > 10
    }

}
