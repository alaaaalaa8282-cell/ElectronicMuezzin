package com.yousefalaa.electronicmuezzin.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefalaa.electronicmuezzin.data.models.QuranKhatma
import com.yousefalaa.electronicmuezzin.data.repositories.QuranKhatmaDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuranKhatmaViewModel @Inject constructor(
    private val dao: QuranKhatmaDao
) : ViewModel() {

    val allKhatmat: Flow<List<QuranKhatma>> = dao.getAll()
    val activeKhatma: Flow<QuranKhatma?> = dao.getActive()

    fun createKhatma(name: String, targetDays: Int) {
        viewModelScope.launch {
            dao.insert(
                QuranKhatma(
                    name = name,
                    targetDays = targetDays,
                    startDate = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateProgress(khatma: QuranKhatma, juz: Int, page: Int) {
        viewModelScope.launch {
            dao.update(
                khatma.copy(
                    currentJuz = juz.coerceIn(1, 30),
                    currentPage = page.coerceIn(1, 604)
                )
            )
        }
    }

    fun completeKhatma(khatma: QuranKhatma) {
        viewModelScope.launch {
            dao.update(
                khatma.copy(
                    isCompleted = true,
                    completedDate = System.currentTimeMillis(),
                    currentJuz = 30,
                    currentPage = 604
                )
            )
        }
    }

    fun deleteKhatma(khatma: QuranKhatma) {
        viewModelScope.launch {
            dao.delete(khatma)
        }
    }
}
