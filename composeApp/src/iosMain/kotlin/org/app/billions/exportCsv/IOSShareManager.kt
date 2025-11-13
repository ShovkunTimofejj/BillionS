package org.app.billions.exportCsv

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.app.billions.exportCsv.ShareManager
import platform.Foundation.*
import platform.UIKit.*

class IOSShareManager : ShareManager {
    override fun shareTextFile(fileName: String, mimeType: String, content: String) {
        val tempDir = NSTemporaryDirectory()
        val filePath = tempDir + fileName
        val fileUrl = NSURL.fileURLWithPath(filePath)

        val data = content.encodeToByteArray().toNSData()
        data.writeToFile(filePath, atomically = true)

        val activityVC = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null
        )

        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootVC?.presentViewController(activityVC, animated = true, completion = null)
    }
}

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData =
    usePinned {
        NSData.create(bytes = it.addressOf(0), length = this.size.toULong())
    }

actual val share: ShareManager = IOSShareManager()