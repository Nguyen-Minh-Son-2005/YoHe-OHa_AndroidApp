package com.nguyenminhson.yoheoha.ui.util

import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object ValidationUtils {

    // RFC 5322 simplified email regex
    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    // Time format HH:mm (00:00 - 23:59)
    private val TIME_PATTERN = Pattern.compile(
        "^([01]\\d|2[0-3]):[0-5]\\d$"
    )

    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> "Vui lòng nhập địa chỉ Email"
            !EMAIL_PATTERN.matcher(trimmed).matches() -> "Email không đúng định dạng (ví dụ: name@gmail.com)"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Vui lòng nhập mật khẩu"
            password.length < 6 -> "Mật khẩu phải có ít nhất 6 ký tự"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        return when {
            confirm.isEmpty() -> "Vui lòng nhập lại mật khẩu xác nhận"
            password != confirm -> "Mật khẩu xác nhận không khớp"
            else -> null
        }
    }

    fun validateNickname(nickname: String): String? {
        val trimmed = nickname.trim()
        return when {
            trimmed.isEmpty() -> "Vui lòng nhập biệt danh / tên hiển thị"
            trimmed.length < 2 -> "Tên hiển thị phải từ 2 ký tự trở lên"
            trimmed.length > 30 -> "Tên hiển thị không được vượt quá 30 ký tự"
            else -> null
        }
    }

    // --- Onboarding / Health Metrics Validation ---

    fun calculateAge(birthDateStr: String): Int? {
        val trimmed = birthDateStr.trim()
        if (trimmed.isEmpty()) return null
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val birth = sdf.parse(trimmed) ?: return null
            val birthCal = Calendar.getInstance().apply { time = birth }
            val nowCal = Calendar.getInstance()

            var age = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
            if (nowCal.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            null
        }
    }

    fun validateBirthDate(birthDateStr: String): String? {
        val trimmed = birthDateStr.trim()
        if (trimmed.isEmpty()) return "Vui lòng chọn ngày tháng năm sinh"
        val age = calculateAge(trimmed) ?: return "Ngày sinh không đúng định dạng"
        return when {
            age < 12 -> "Độ tuổi theo dõi phù hợp là từ 12 tuổi trở lên (Hiện tại: $age tuổi)"
            age > 100 -> "Vui lòng chọn ngày sinh thực tế (tối đa 100 tuổi)"
            else -> null
        }
    }

    fun formatDisplayDate(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return ""
        return try {
            val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inFormat.parse(dateStr)
            if (date != null) outFormat.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun validateHeight(heightStr: String): String? {
        val trimmed = heightStr.trim()
        if (trimmed.isEmpty()) return "Vui lòng nhập chiều cao"
        val h = trimmed.toFloatOrNull() ?: return "Chiều cao không hợp lệ"
        return when {
            h < 100f -> "Chiều cao thực tế tối thiểu từ 100 cm"
            h > 230f -> "Chiều cao thực tế tối đa đến 230 cm"
            else -> null
        }
    }

    fun validateWeight(weightStr: String): String? {
        val trimmed = weightStr.trim()
        if (trimmed.isEmpty()) return "Vui lòng nhập cân nặng"
        val w = trimmed.toFloatOrNull() ?: return "Cân nặng không hợp lệ"
        return when {
            w < 30f -> "Cân nặng thực tế tối thiểu từ 30 kg"
            w > 250f -> "Cân nặng thực tế tối đa đến 250 kg"
            else -> null
        }
    }

    fun validateTime(timeStr: String, fieldName: String = "Thời gian"): String? {
        val trimmed = timeStr.trim()
        if (trimmed.isEmpty()) return "Vui lòng chọn $fieldName"
        return if (!TIME_PATTERN.matcher(trimmed).matches()) {
            "Định dạng giờ phải là HH:mm (ví dụ: 06:30)"
        } else null
    }

    // Circadian Rhythm Healthy Check:
    // Dậy trước 07:30 (từ 04:00 đến 07:30)
    // Ngủ trước 01:00 (từ 21:00 đến 01:00 đêm)
    fun validateSchedule(wakeTime: String, sleepTime: String): String? {
        val wakeErr = validateTime(wakeTime, "Giờ thức dậy")
        if (wakeErr != null) return wakeErr
        val sleepErr = validateTime(sleepTime, "Giờ đi ngủ")
        if (sleepErr != null) return sleepErr

        val wakeParts = wakeTime.split(":").map { it.toInt() }
        val wakeMinutes = wakeParts[0] * 60 + wakeParts[1]

        val sleepParts = sleepTime.split(":").map { it.toInt() }
        val sleepMinutes = sleepParts[0] * 60 + sleepParts[1]

        // 1. Wake time check: Must be <= 07:30 (450 mins) and >= 04:00 (240 mins)
        if (wakeMinutes > 7 * 60 + 30 && wakeMinutes <= 12 * 60) {
            return "🌿 Để cơ thể đón ánh nắng sớm và trao đổi chất tốt nhất, bạn hãy thức dậy trước 07:30 sáng nhé!"
        }

        // 2. Sleep time check: Must NOT be between 01:01 AM and 04:00 AM (61 to 240 mins)
        if (sleepMinutes in 61..240) {
            return "🌿 Giấc ngủ sâu từ 23:00 đến 01:00 đêm giúp gan thận giải độc và phục hồi tốt nhất. Bạn hãy đi ngủ trước 01:00 đêm nhé!"
        }

        if (wakeTime.trim() == sleepTime.trim()) {
            return "Giờ thức dậy và giờ đi ngủ không được trùng nhau"
        }
        return null
    }

    // Asian BMI 5 levels classification (WHO WPRO & Bộ Y Tế VN)
    fun getAsianBmiClassification(bmi: Float): Pair<String, String> {
        return when {
            bmi < 18.5f -> Pair("Thiếu cân (Gầy)", "UNDERWEIGHT")
            bmi < 23.0f -> Pair("Bình thường (Cân đối)", "NORMAL")
            bmi < 25.0f -> Pair("Tiền béo phì (Thừa cân)", "OVERWEIGHT")
            bmi < 30.0f -> Pair("Béo phì Độ I", "OBESE_I")
            else -> Pair("Béo phì Độ II (Cần lưu ý)", "OBESE_II")
        }
    }
}
