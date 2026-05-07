package com.abtasty.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.abtasty.app.databinding.ActivityMainBinding
import com.abtasty.flagship.hits.Event
import com.abtasty.flagship.hits.Hit
import com.abtasty.flagship.hits.Item
import com.abtasty.flagship.hits.Screen
import com.abtasty.flagship.hits.Transaction
import com.abtasty.flagship.main.Flagship
import com.abtasty.flagship.main.FlagshipConfig
//import com.abtasty.qa_assistant_android.QAAssistant
import com.abtasty.qa_assistant_android.QAAssistant2
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("#QA ASSISTANT COLLECTER APP ON CREATE")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()

            val hits = arrayListOf<Hit<*>?>(
                null,
                Screen("Screen B " + System.currentTimeMillis()),
                Item("Item B ", "Product name", "System.currentTimeMillis()"),
                Transaction("Transaction B", "${System.currentTimeMillis()}")
            )
            hits.random()?.let { Flagship.getVisitor()?.sendHit(it) }
            val event = Event(Event.EventCategory.ACTION_TRACKING, "Click")
            Flagship.getVisitor()?.sendHit(event)
        }

        Flagship.start(
            application,
            "bkk4s7gcmjcg07fke9dg",
            "Q6FDmj6F188nh75lhEato2MwoyXDS7y34VrAL4Aa",
            FlagshipConfig.Bucketing()
        )
//        Flagship.start(application, "bkk4s7gcmjcg07fke9dg", "Q6FDmj6F188nh75lhEato2MwoyXDS7y34VrAL4Aa", FlagshipConfig.DecisionApi())
            .invokeOnCompletion {
                QAAssistant2.open(this, "bkk4s7gcmjcg07fke9dg")
                println("[APP] FLAGSHIP STARTED")
                println("#QA CORE COLLECTER FLAGSHIP STARTED")
                println("#QA CORE COLLECTER VISITOR CREATED")
                val visitor = Flagship.newVisitor("visitorId8937", true)
                    .context(
                        hashMapOf(
                            "fs_is_vip" to "vip",
                            "Persona1" to "deux",
                            "case" to 3
                        )
                    )
                    .build()
                visitor.fetchFlags().invokeOnCompletion {
                    println("#QA CORE COLLECTER FLAGSHIP FETCHED")
                    println("[APP] FETCHED FLAGS")
                }
                runBlocking {
                    println("#QA COLLECTER SEND HIT")
                    delay(2000)
                    visitor.sendHit(Screen("AAAAAAAAAAAA " + System.currentTimeMillis()))
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()

    }
}