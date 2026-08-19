package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccent

@Composable
fun AboutAppDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "عن تطبيق أذكار المسلم",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "الإصدار 1.0.0 (نقاء واستقرار)",
                    style = MaterialTheme.typography.labelMedium.copy(color = GoldAccent)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "تطبيق «أذكار المسلم» هو تطبيق إسلامي شامل ومجاني لوجه الله تعالى، صُمم بأعلى معايير الجودة والتقنية الحديثة ليكون رفيقك الدائم في ذكر الله عز وجل.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "📖 المصادر والتحقيق الشرعي:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• القرآن الكريم.\n• صحيح الإمام البخاري.\n• صحيح الإمام مسلم.\n• سنن الترمذي، أبي داود، النسائي، وابن ماجه.\n• كتاب «حصن المسلم من أذكار الكتاب والسنة» للشيخ سعيد بن علي بن وهف القحطاني رحمه الله.\n• تم ضبط النصوص بالتشكيل الكامل وعزو كل ذكر لمصدره الأصلي وفضله الثابت.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "✨ المزايا التقنية:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• يعمل دون إنترنت 100%.\n• سبحة إلكترونية رقمية تفاعلية.\n• تنبيهات وتذكير بأوقات الأذكار.\n• دعم كامل للوضع الداكن و4 ألوان إسلامية راقية.\n• تحكم بحجم الخط والاهتزاز والصوت.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("حفظك الله")
            }
        }
    )
}

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "سياسة الخصوصية والأمان",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = "خصوصيتك أمانة مقدسة لدينا:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. لا نجمع أي بيانات شخصية: التطبيق لا يطلب ولا يسجل اسمك أو بريدك أو أي معلومة شخصية.\n\n2. قاعدة بيانات محلية 100%: جميع إعداداتك ومفضلاتك وعداداتك تُحفظ على هاتفك فقط عبر تقنية Room Database المشفرة محلياً.\n\n3. لا توجد إعلانات مزعجة أو متتبعات تجارية.\n\n4. أذونات التطبيق مقتصرة تماماً على التنبيهات والاهتزاز لتشغيل السبحة والتذكيرات.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("إغلاق")
            }
        }
    )
}
