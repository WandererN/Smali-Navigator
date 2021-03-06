package com.jh.logs

import javafx.application.Platform
import javafx.scene.control.TextArea
import org.apache.logging.log4j.core.*
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory
import java.io.Serializable

@Plugin(name = "TextViewLoggerAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
class TextViewLoggerAppender(name: String, filter: Filter?, layout: Layout<out Serializable>?) : AbstractAppender(name, filter, layout) {
    override fun append(event: LogEvent?) {
        val logMessage = String(layout.toByteArray(event))
        Platform.runLater {
            if (loggingTextArea.text.isEmpty()) {
                loggingTextArea.text = logMessage
            } else {
                loggingTextArea.insertText(loggingTextArea.length, logMessage)
            }
            loggingTextArea.positionCaret(loggingTextArea.length)
        }
    }


    companion object {
        lateinit var loggingTextArea: TextArea
        @PluginFactory
        @JvmStatic
        fun createAppender(
                @PluginAttribute("name") name: String?,
                @PluginElement("PatternLayout") layout: Layout<out Serializable>?,
                @PluginElement("Filter") filter: Filter?
        ): TextViewLoggerAppender? {
            if (name == null) {
                LOGGER.error("No name provided for TextViewLoggerAppender")
                return null
            }
//            var resultLayout = layout
//            if (resultLayout == null) {
//                resultLayout=createDefaultLayout().
//            }
            return TextViewLoggerAppender(name, filter, layout)
        }
    }


}