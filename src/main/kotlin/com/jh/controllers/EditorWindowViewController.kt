package com.jh.controllers

import com.jh.HighlightsLoader
import javafx.concurrent.Task
import org.fxmisc.richtext.CodeArea
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import org.reactfx.Subscription
import java.time.Duration
import java.util.*
import java.util.concurrent.Executor
import java.util.regex.Pattern

class EditorWindowViewController(private val codeArea: CodeArea, var executor: Executor) {
    lateinit var highlightsPattern: Pattern
    private var cleanupWhenNoLongerNeedIt: Subscription

    init {
        cleanupWhenNoLongerNeedIt =
                codeArea
                        .multiPlainChanges()
                        .successionEnds(Duration.ofMillis(500))
                        .supplyTask { computeHighlightingAsync() }
                        .awaitLatest(codeArea.multiPlainChanges())
                        .filterMap {
                            if (it.isSuccess) {
                                return@filterMap Optional.of(it.get())
                            } else {
                                it.failure.printStackTrace()
                                return@filterMap null
                            }
                        }.subscribe(this::applyHighlighting)
        with(HighlightsLoader)
        {
            highlightsPattern = generateGlobalPattern(
                    generateOpcodesString(),
                    generateKeywordsString(),
                    generateCommentString(),
                    generateClassAndTypesString(),
                    generateLabelsString(),
                    generateLineNamString(),
                    generateStringString(),
                    generateRegistersString()
            )

        }
    }

    private fun computeHighlightingAsync(): Task<StyleSpans<Collection<String>>> {
        val task = object : Task<StyleSpans<Collection<String>>>() {
            override fun call(): StyleSpans<Collection<String>> {
                return computeHighlighting(codeArea.text)
            }
        }
        executor.execute(task)
        return task
    }

    private val highlightsToCSSClassesMap = hashMapOf(
            "CLASSORTYPE" to "classortype",
            "KEYWORD" to "keyword",
            "OPCODE" to "opcode",
            "COMMENT" to "comment",
            "LABEL" to "label",
            "LINENUMBER" to "linenumber",
            "STRING" to "string",
            "REGISTER" to "register"
    )

    fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
        var lastKwPos = 0
        val matcher = highlightsPattern.matcher(text)
        val spansBuilder = StyleSpansBuilder<Collection<String>>()
        while (matcher.find()) {
            var styleClass: String? = null
            var matchGroupName: String? = null
            for (elem in highlightsToCSSClassesMap) {
                matcher.group(elem.key)?.let {
                    styleClass = elem.value
                    matchGroupName = elem.key
                }
            }
            spansBuilder.add(Collections.emptyList(), matcher.start(matchGroupName) - lastKwPos)
            spansBuilder.add(Collections.singleton(styleClass!!), matcher.end(matchGroupName) - matcher.start(matchGroupName))
            lastKwPos = matcher.end(matchGroupName)
        }
        spansBuilder.add(Collections.emptyList(), text.length - lastKwPos)
        return spansBuilder.create()
    }

    private fun applyHighlighting(highlighting: StyleSpans<Collection<String>>) {
        codeArea.setStyleSpans(0, highlighting)
    }

    fun close() {
        cleanupWhenNoLongerNeedIt.unsubscribe()
    }
}