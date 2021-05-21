
import java.net.HttpURLConnection
import java.net.URL

// TODO: 2021/5/21  
class UrlTools {

}
suspend fun sendGetHttpRequest(url:String, params: HashMap<String,String> = hashMapOf(), back: Back){
    val strBuffer:StringBuilder = StringBuilder()
    if (params.isNotEmpty()){
        strBuffer.append(url).append("?")
        for (i in params){
            strBuffer.append(i.key).append("=").append(i.value).append("&")
        }
        strBuffer.deleteCharAt(strBuffer.length-1)
    }

    val httpUrlConnection = URL(url).openConnection() as HttpURLConnection

    httpUrlConnection.apply {
        doInput = true
        doOutput = true
        requestMethod = "GET"
        readTimeout = 5000
        connectTimeout = 5000
        defaultUseCaches = false
    }

//    val outPutStream = httpUrlConnection.outputStream
    getResult(httpUrlConnection,back)

}

private fun getResult(httpUrlConnection: HttpURLConnection, back: Back): String {
    val stringBuilder = StringBuilder()
    //连接成功
    return if (httpUrlConnection.responseCode == HttpURLConnection.HTTP_OK) {
        val inputStream = httpUrlConnection.inputStream
        inputStream.bufferedReader().forEachLine {
            stringBuilder.append(it)
        }
        back.onSuccess(stringBuilder.toString())
        stringBuilder.toString()
    }else{
        back.onFail()
        "Http Connect Wrong"
    }
}

suspend fun sendPostHttpRequest(url:String, params: HashMap<String,String> = hashMapOf(), back: Back){
    val strBuffer:StringBuilder = StringBuilder()
    if (params.isNotEmpty()){
        strBuffer.append(url).append("?")
        for (i in params){
            strBuffer.append(i.key).append("=").append(i.value).append("&")
        }
        strBuffer.deleteCharAt(strBuffer.length-1)
    }

    val httpUrlConnection = URL(url).openConnection() as HttpURLConnection

    httpUrlConnection.apply {
        doInput = true
        doOutput = true
        requestMethod = "POST"
        readTimeout = 5000
        connectTimeout = 5000
        defaultUseCaches = false
    }

//    val outPutStream = httpUrlConnection.outputStream
    getResult(httpUrlConnection,back)

}

interface Back{
    fun onSuccess(body:String)
    fun onFail()
}