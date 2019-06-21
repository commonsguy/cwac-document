package com.commonsware.cwac.document.demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.commonsware.cwac.document.DocumentFileCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class RowState(
  val doc: DocumentFileCompat,
  val displayName: String,
  val mimeType: String?,
  val isDirectory: Boolean
)

data class MainViewState(
  val contents: List<RowState>
)

class MainMotor(app: Application) : AndroidViewModel(app) {
  private val _states = MutableLiveData<MainViewState>()
  val states: LiveData<MainViewState> = _states

  fun load(tree: DocumentFileCompat) {
    viewModelScope.launch(Dispatchers.Main) {
      val contents = withContext(Dispatchers.IO) {
        tree.listFiles()
          .toList()
          .map { RowState(it, it.name, it.type, it.isDirectory) }
          .sortedBy { it.displayName }
      }

      _states.value = MainViewState(contents)
    }
  }
}