package views

import javafx.scene.control.DialogPane
import javafx.scene.layout.BorderPane
import tornadofx.*

class SearchDialog : View("My View") {
    override val root: DialogPane by fxml("/views/SearchDialog.fxml")
}
