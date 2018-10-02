package com.jh.editor

import com.jh.util.loadPicture
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import org.fxmisc.richtext.GenericStyledArea
import org.reactfx.collection.LiveList
import org.reactfx.value.Val
import tornadofx.*
import java.util.function.IntFunction

class AdvancedLineNumberFactory : IntFunction<Node> {
    private val DEFAULT_INSETS = Insets(0.0, 5.0, 0.0, 5.0)
    private val DEFAULT_TEXT_FILL = Color.web("#666")
    private val DEFAULT_FONT = Font.font("monospace", FontPosture.REGULAR, 13.0)
    private val DEFAULT_BACKGROUND = Background(BackgroundFill(Color.web("#ddd"), null, null))
    // private val img = loadPicture()
    override fun apply(value: Int): Node {
        return HBox().apply {
            background = DEFAULT_BACKGROUND
            padding = DEFAULT_INSETS
            alignment = Pos.TOP_RIGHT
            styleClass += "lineno"
            label {
                textFill = DEFAULT_TEXT_FILL
                font = DEFAULT_FONT
                text = "%3d".format(value + 1)
            }

        }
    }
}