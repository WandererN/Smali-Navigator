package com.jh.views

import javafx.scene.control.TextArea
import tornadofx.*

class ExternalToolLoggerView : View("Running...") {
    var myTextArea: TextArea = textarea {
        isEditable = false
    }
    override val root = borderpane {
        center = myTextArea
        setMinSize(300.0, 300.0)
        bottom = button {
            text = "Close"
            this.setOnMouseClicked { close() }
        }
    }
}
