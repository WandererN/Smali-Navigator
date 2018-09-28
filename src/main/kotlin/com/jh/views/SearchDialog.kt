package com.jh.views

import javafx.scene.layout.BorderPane
import javafx.stage.StageStyle
import tornadofx.*

class SearchDialog : View("Find...") {
    override val root: BorderPane by fxml("/views/SearchDialog.fxml")

    init {
    }

    override fun onBeforeShow() {
        super.onBeforeShow()
        currentStage?.initStyle(StageStyle.UTILITY)
    }
}
