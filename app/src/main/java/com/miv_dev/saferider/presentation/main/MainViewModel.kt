package com.miv_dev.saferider.presentation.main


sealed class UiState
object Loading : UiState()
object Initial : UiState()
class Error(val msg: String) : UiState()


//@HiltViewModel
//class MainViewModel @Inject constructor(private val bleRepository: RepositoryImpl) : ViewModel() {
//
//}



