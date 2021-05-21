package com.example.firsttask.level2
import android.util.Log
import com.example.firsttask.level2.NetRequest.TAG
import com.example.firsttask.level2.NetRequest.innerSendGetRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//顶层
var netJob= Job()
var netThread= CoroutineScope(netJob)

/**
 * 简化接口
 */
// TODO: 2021/2/21
suspend fun sendPostRequest(url: String, tail: String):String{
    return suspendCoroutine{
        NetRequest.innerSendPostRequest(url, tail,object : Back{
            override fun onSucceed(resultBody: String) {
                it.resume(resultBody)
            }

            override fun onFailed(respondCode: Int, e: Exception) {
                Log.d(TAG, "onFailed: ")
                Log.d(TAG, "Exception: $e")
                Log.d(TAG, "respondCode: $respondCode")
                it.resumeWithException(e)
            }

        })
    }
}

suspend fun sendGetRequest(url: String):String{
    return suspendCoroutine {
        innerSendGetRequest(url, object : Back{
            override fun onSucceed(resultBody: String) {
                //恢复传入一个值
                it.resume(resultBody)
            }

            override fun onFailed(respondCode: Int, e: Exception) {
                Log.d(TAG, "onFailed: ")
                Log.d(TAG, "Exception: $e")
                Log.d(TAG, "respondCode: $respondCode")
                //恢复（通过异常）
                it.resumeWithException(e)
            }

        })
    }
}
private object NetRequest {
    const val TAG = "NetRequest"

    /**
     * 发送Post请求
     */

    fun innerSendPostRequest(url: String, tail: String, back: Back) {

        //获取HttpURLConnection
        val httpURLConnection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection

        //配置连接属性
        httpURLConnection.apply {
            doInput = true
            doOutput = true
            requestMethod = "POST"
            readTimeout = 5000
            connectTimeout = 5000
            defaultUseCaches = false

        }

        //获取output流
        val outputStream = httpURLConnection.outputStream

        //包装成buffer流
        val writer = BufferedWriter(OutputStreamWriter(outputStream))
        //写入后部分的参数
        writer.write(tail)
        //刷新
        writer.flush()
        getResult(httpURLConnection, back)

        //关闭
        httpURLConnection.disconnect()
        outputStream.close()
        writer.close()
    }

    /**中间代码（通过reader读取返回的内容）(减少重复代码)
     * connection -> 传入的网络连接
     * back -> 回调接口
     */
    private fun getResult(connection: HttpURLConnection, back: Back) {
        val stringBuilder = StringBuilder()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            //代表连接成功

            val inputStream = connection.inputStream
            inputStream.bufferedReader().forEachLine {
                stringBuilder.append(it)
                Log.d(TAG, "getResult: $it")
            }
            back.onSucceed(stringBuilder.toString())
            Log.d(TAG, "网络请求成功")

            //关闭流
            inputStream.close()

        } else {
            //初始化Exception抓异常
            var mye: Exception = Exception()
            //
            try {
                val x = connection.responseMessage
                Log.d(TAG, ":responseMessage  $x")
                var y = connection.errorStream
            } catch (e: Exception) {
                mye = e
            }
            back.onFailed(connection.responseCode, mye)
            Log.d(TAG, "网络请求失败")
        }
    }

    /**发送Get请求
     * url -> 对应的网站地址
     * back -> 网络请求结束的回调接口
     */

    fun innerSendGetRequest(url: String, back: Back) {
        val connection = URL(url).openConnection() as HttpURLConnection
        //配置连接
        try {
            connection.apply {
                connectTimeout = 5000
                readTimeout = 5000
                defaultUseCaches = false
                doInput = true
                requestMethod = "GET"
            }
        }catch (e:java.lang.Exception){
            Log.e(TAG, "innerSendGetRequest: 已经被连接了",e)
        }
        //获取网络请求结果
        getResult(connection, back)

        //关闭连接
        connection.disconnect()
    }
}

/**
 * 回调接口
 */
interface Back {
    fun onSucceed(resultBody: String)
    fun onFailed(respondCode: Int, e: Exception)
}

