package views

import com.jh.views.EditorWindowView
import javafx.scene.layout.VBox
import tornadofx.*

class ModernTabEditorView : View("My View") {
    override val root: VBox by fxml("/views/ModernTabEditorView.fxml")
    val editorWindowView: EditorWindowView by fxid("codeArea")

}

