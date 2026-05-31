package com.yousefalaa.electronicmuezzin.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefalaa.electronicmuezzin.data.models.Dhikr
import com.yousefalaa.electronicmuezzin.data.models.DhikrCategory
import com.yousefalaa.electronicmuezzin.data.repositories.DhikrDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdhkarViewModel @Inject constructor(
    private val dhikrDao: DhikrDao
) : ViewModel() {

    init {
        viewModelScope.launch {
            val count = dhikrDao.count()
            if (count == 0) {
                dhikrDao.insertAll(getDefaultAdhkar())
            }
        }
    }

    fun getAdhkarByCategory(category: DhikrCategory): Flow<List<Dhikr>> =
        dhikrDao.getByCategory(category)

    private fun getDefaultAdhkar(): List<Dhikr> = listOf(
        // أذكار الصباح
        Dhikr(text = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", count = 1, category = DhikrCategory.MORNING, reference = "رواه مسلم"),
        Dhikr(text = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ", count = 1, category = DhikrCategory.MORNING, reference = "رواه الترمذي"),
        Dhikr(text = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ", count = 1, category = DhikrCategory.MORNING, reference = "رواه البخاري - سيد الاستغفار"),
        Dhikr(text = "اللَّهُمَّ عَافِنِي فِي بَدَنِي، اللَّهُمَّ عَافِنِي فِي سَمْعِي، اللَّهُمَّ عَافِنِي فِي بَصَرِي، لَا إِلَهَ إِلَّا أَنْتَ", count = 3, category = DhikrCategory.MORNING, reference = "رواه أبو داود"),
        Dhikr(text = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ", count = 1, category = DhikrCategory.MORNING, reference = "رواه أبو داود"),
        Dhikr(text = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ", count = 3, category = DhikrCategory.MORNING, reference = "رواه أبو داود والترمذي"),
        Dhikr(text = "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالْإِسْلَامِ دِينًا، وَبِمُحَمَّدٍ صَلَّى اللهُ عَلَيْهِ وَسَلَّمَ نَبِيًّا", count = 3, category = DhikrCategory.MORNING, reference = "رواه أبو داود"),
        Dhikr(text = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", count = 100, category = DhikrCategory.MORNING, reference = "رواه مسلم"),
        Dhikr(text = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ", count = 3, category = DhikrCategory.MORNING, reference = "رواه مسلم"),
        Dhikr(text = "قُلْ هُوَ اللَّهُ أَحَدٌ - قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ - قُلْ أَعُوذُ بِرَبِّ النَّاسِ", count = 3, category = DhikrCategory.MORNING, reference = "رواه أبو داود والترمذي"),
        Dhikr(text = "آيَةُ الْكُرْسِيِّ: اللَّهُ لَا إِلَهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ...", count = 1, category = DhikrCategory.MORNING, reference = "رواه الطبراني"),
        Dhikr(text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", count = 10, category = DhikrCategory.MORNING, reference = "رواه أحمد"),

        // أذكار المساء
        Dhikr(text = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ", count = 1, category = DhikrCategory.EVENING, reference = "رواه مسلم"),
        Dhikr(text = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ", count = 1, category = DhikrCategory.EVENING, reference = "رواه الترمذي"),
        Dhikr(text = "اللَّهُمَّ إِنِّي أَمْسَيْتُ أُشْهِدُكَ وَأُشْهِدُ حَمَلَةَ عَرْشِكَ وَمَلَائِكَتَكَ وَجَمِيعَ خَلْقِكَ أَنَّكَ أَنْتَ اللَّهُ لَا إِلَهَ إِلَّا أَنْتَ وَحْدَكَ لَا شَرِيكَ لَكَ وَأَنَّ مُحَمَّدًا عَبْدُكَ وَرَسُولُكَ", count = 4, category = DhikrCategory.EVENING, reference = "رواه أبو داود"),
        Dhikr(text = "اللَّهُمَّ مَا أَمْسَى بِي مِنْ نِعْمَةٍ أَوْ بِأَحَدٍ مِنْ خَلْقِكَ فَمِنْكَ وَحْدَكَ لَا شَرِيكَ لَكَ فَلَكَ الْحَمْدُ وَلَكَ الشُّكْرُ", count = 1, category = DhikrCategory.EVENING, reference = "رواه أبو داود"),

        // أذكار بعد الصلاة
        Dhikr(text = "أَسْتَغْفِرُ اللَّهَ", count = 3, category = DhikrCategory.AFTER_PRAYER, reference = "رواه مسلم"),
        Dhikr(text = "اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ ذَا الْجَلَالِ وَالْإِكْرَامِ", count = 1, category = DhikrCategory.AFTER_PRAYER, reference = "رواه مسلم"),
        Dhikr(text = "سُبْحَانَ اللَّهِ", count = 33, category = DhikrCategory.AFTER_PRAYER, reference = "رواه مسلم"),
        Dhikr(text = "الْحَمْدُ لِلَّهِ", count = 33, category = DhikrCategory.AFTER_PRAYER, reference = "رواه مسلم"),
        Dhikr(text = "اللَّهُ أَكْبَرُ", count = 33, category = DhikrCategory.AFTER_PRAYER, reference = "رواه مسلم"),
        Dhikr(text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ", count = 1, category = DhikrCategory.AFTER_PRAYER, reference = "رواه مسلم"),
        Dhikr(text = "آيَةُ الْكُرْسِيِّ", count = 1, category = DhikrCategory.AFTER_PRAYER, reference = "من قرأها دبر كل صلاة - رواه الطبراني"),
        Dhikr(text = "اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ وَشُكْرِكَ وَحُسْنِ عِبَادَتِكَ", count = 1, category = DhikrCategory.AFTER_PRAYER, reference = "رواه أبو داود"),

        // أذكار النوم
        Dhikr(text = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا", count = 1, category = DhikrCategory.SLEEP, reference = "رواه البخاري"),
        Dhikr(text = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ", count = 3, category = DhikrCategory.SLEEP, reference = "رواه أبو داود"),
        Dhikr(text = "سُبْحَانَ اللَّهِ (33) الْحَمْدُ لِلَّهِ (33) اللَّهُ أَكْبَرُ (34)", count = 1, category = DhikrCategory.SLEEP, reference = "رواه البخاري ومسلم"),
        Dhikr(text = "الَّذِي بِيَدِهِ مَلَكُوتُ كُلِّ شَيْءٍ وَإِلَيْهِ تُرْجَعُونَ - سورة يس (83)", count = 1, category = DhikrCategory.SLEEP),

        // أذكار الاستيقاظ
        Dhikr(text = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ", count = 1, category = DhikrCategory.WAKE, reference = "رواه البخاري"),
        Dhikr(text = "لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ سُبْحَانَ اللَّهِ وَالْحَمْدُ لِلَّهِ وَلَا إِلَهَ إِلَّا اللَّهُ وَاللَّهُ أَكْبَرُ", count = 1, category = DhikrCategory.WAKE, reference = "رواه البخاري"),

        // أذكار الطعام
        Dhikr(text = "بِسْمِ اللَّهِ", count = 1, category = DhikrCategory.EATING, reference = "رواه أبو داود"),
        Dhikr(text = "بِسْمِ اللَّهِ فِي أَوَّلِهِ وَآخِرِهِ (إذا نسي في البداية)", count = 1, category = DhikrCategory.EATING, reference = "رواه أبو داود"),
        Dhikr(text = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَذَا وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ (بعد الأكل)", count = 1, category = DhikrCategory.EATING, reference = "رواه الترمذي"),

        // أذكار قرآنية
        Dhikr(text = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ", count = 7, category = DhikrCategory.QURAN, reference = "آل عمران: 173"),
        Dhikr(text = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ", count = 1, category = DhikrCategory.QURAN, reference = "الأنبياء: 87 - دعاء يونس"),
        Dhikr(text = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ", count = 1, category = DhikrCategory.QURAN, reference = "البقرة: 201"),
        Dhikr(text = "رَبِّ اغْفِرْ لِي وَتُبْ عَلَيَّ إِنَّكَ أَنْتَ التَّوَّابُ الرَّحِيمُ", count = 100, category = DhikrCategory.QURAN, reference = "رواه الترمذي")
    )
}
