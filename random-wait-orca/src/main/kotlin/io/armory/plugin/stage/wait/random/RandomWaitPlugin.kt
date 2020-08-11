package io.armory.plugin.stage.wait.random

import com.netflix.spinnaker.orca.api.simplestage.SimpleStage
import com.netflix.spinnaker.orca.api.simplestage.SimpleStageInput
import com.netflix.spinnaker.orca.api.simplestage.SimpleStageOutput
import com.netflix.spinnaker.orca.api.simplestage.SimpleStageStatus
import org.slf4j.LoggerFactory
import org.pf4j.Extension
import org.pf4j.Plugin
import org.pf4j.PluginWrapper
//import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration
//import org.springframework.boot.json.GsonJsonParser
//import org.springframework.http.converter.json.GsonFactoryBean
//import org.springframework.http.converter.json.GsonHttpMessageConverter
import java.util.concurrent.TimeUnit
import java.util.*
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties
//import org.springframework.web.client.RestTemplate
//import retrofit2.Call
//import retrofit2.Retrofit
//import retrofit2.converter.moshi.MoshiConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Query
import khttp.*



class RandomWaitPlugin(wrapper: PluginWrapper) : Plugin(wrapper) {
    private val logger = LoggerFactory.getLogger(RandomWaitPlugin::class.java)

    override fun start() {
        logger.info("RandomWaitPlugin.start()")
    }

    override fun stop() {
        logger.info("RandomWaitPlugin.stop()")
    }
}

/**
 * By implementing SimpleStage, your stage is available for use in Spinnaker.
 * @see com.netflix.spinnaker.orca.api.SimpleStage
 */
@Extension
class RandomWaitStage(val configuration: RandomWaitConfig) : SimpleStage<RandomWaitInput> {

    private val log = LoggerFactory.getLogger(SimpleStage::class.java)

    /**
     * This sets the name of the stage
     * @return the name of the stage
     */
    override fun getName(): String {
        return "randomWait"
    }

    /**
     * This is called when the stage is executed. It takes in an object that you create. That
     * object contains fields that you want to pull out of the pipeline context. This gives you a
     * strongly typed object that you have full control over.
     * The SimpleStageOutput class contains the status of the stage and any stage outputs that should be
     * put back into the pipeline context.
     * @param stageInput<RandomWaitInput>
     * @return SimpleStageOutput; the status of the stage and any context that should be passed to the pipeline context
     */
    override fun execute(stageInput: SimpleStageInput<RandomWaitInput>): SimpleStageOutput<*, *> {
        val rand = Random()
        val maxWaitTime = stageInput.value.maxWaitTime
        var outputMessage = stageInput.value.outputMessage
        val timeToWait = rand.nextInt(maxWaitTime)

        try {
            TimeUnit.SECONDS.sleep(timeToWait.toLong())
        } catch (e: Exception) {
            log.error("{}", e)
        }

//        val quote = RestTemplate().getForObject("http://gturnquist-quoters.cfapps.io/api/random", Quote::class.java)
//        val service = Retrofit.Builder().baseUrl("http://gturnquist-quoters.cfapps.io/api/random").build()
        val quote = get("http://gturnquist-quoters.cfapps.io/api/random")
        val stageOutput = SimpleStageOutput<Output, Context>()
        val output = Output(timeToWait, outputMessage.toString() + ": " + quote.text)
//        val output = Output(timeToWait, outputMessage)
        val context = Context(maxWaitTime)

        stageOutput.setOutput(output)
        stageOutput.setContext(context)
        stageOutput.setStatus(SimpleStageStatus.SUCCEEDED)

        return stageOutput
    }
}

//@JsonIgnoreProperties(ignoreUnknown = true)
//data class Value(var id: Long = 0, var quote: String = "")
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//data class Quote(var type : String = "", var value : Value? = null)

