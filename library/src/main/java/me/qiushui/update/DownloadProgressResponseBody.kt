package me.qiushui.update

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * Rxbus 消息载体
 *
 * Created by Qiushui on 2017/10/21.
 */
internal class DownloadProgressResponseBody(private val responseBody: ResponseBody) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null
    private var lastPublishTime: Long = 0

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun contentLength(): Long = responseBody.contentLength()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()))
        }
        return bufferedSource!!
    }


    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            internal var currentLength = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                currentLength += if (bytesRead != -1L) bytesRead else 0
                if (System.currentTimeMillis() - lastPublishTime > 200) {
                    RxBus.publish(DownloadProgressMsg(currentLength, responseBody.contentLength()))
                }
                return bytesRead
            }
        }

    }
}