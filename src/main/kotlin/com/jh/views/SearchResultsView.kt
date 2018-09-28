package com.jh.views

import com.jh.smaliStructs.SmaliClass
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import javafx.stage.StageStyle
import tornadofx.*


class SearchResultsView(private val parentView: ModernView) : View("Search results...") {
    override val root: BorderPane by fxml("/views/SearchResultsView.fxml")
    private val resultsTable: TableView<SearchTableDataClass> by fxid("results_table")
    private val nameTableColumn: TableColumn<SearchTableDataClass, SmaliClass> by fxid("name")
    private val packageTableColumn: TableColumn<SearchTableDataClass, SmaliClass> by fxid("package")
    private val lineNumberColumn: TableColumn<SearchTableDataClass, Int> by fxid("line")
    private val gotoButton: Button by fxid("goto_button")
    private val cancelButton: Button by fxid("close_button")

    init {
        nameTableColumn.cellValueFactory = PropertyValueFactory<SearchTableDataClass, SmaliClass>("smaliClass")
        nameTableColumn.cellFormat { text = it.name }

        packageTableColumn.cellValueFactory = PropertyValueFactory<SearchTableDataClass, SmaliClass>("smaliClass")
        packageTableColumn.cellFormat { text = it.packageName }

        lineNumberColumn.cellValueFactory = PropertyValueFactory<SearchTableDataClass, Int>("lineNumber")
        resultsTable.selectionModel.selectionMode = SelectionMode.SINGLE
        resultsTable.setOnMouseClicked { if (it.clickCount == 2) gotoSearchResult() }
        gotoButton.setOnMouseClicked { gotoSearchResult() }
        cancelButton.setOnMouseClicked { close() }
    }

    override fun onBeforeShow() {
        super.onBeforeShow()
        currentStage?.initStyle(StageStyle.UTILITY)
    }

    private fun gotoSearchResult() {
        val (smaliClass, line) = resultsTable.selectionModel.selectedItem ?: return
        smaliClass.file?.let { parentView.openFileOrFocusTab(it, line) }
    }

    fun addLineToResultTable(searchTableDataClass: SearchTableDataClass) {
        resultsTable.items.add(searchTableDataClass)
    }
}
