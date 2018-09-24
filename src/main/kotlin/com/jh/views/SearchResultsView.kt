package com.jh.views

import javafx.collections.FXCollections
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.io.File


class SearchTableDataClass(var file: File, var lineNumber: Int)

class SearchResultsView : View("My View") {
    override val root: BorderPane by fxml("/views/SearchResultsView.fxml")
    private val usersData = FXCollections.observableArrayList<SearchTableDataClass>()
    private val resultsTable: TableView<SearchTableDataClass> by fxid("results_table")
    private val pathTableColumn: TableColumn<SearchTableDataClass, File> by fxid("path")
    private val lineNumberColumn: TableColumn<SearchTableDataClass, Int> by fxid("line")

    init {
        pathTableColumn.cellValueFactory = PropertyValueFactory<SearchTableDataClass, File>("file")
        pathTableColumn.cellFormat { text = it.name }
        lineNumberColumn.cellValueFactory = PropertyValueFactory<SearchTableDataClass, Int>("lineNumber")
        resultsTable.items = usersData
    }

    fun addLineToResultTable(SearchTableDataClass: SearchTableDataClass) {
        usersData.add(SearchTableDataClass)
    }

}
