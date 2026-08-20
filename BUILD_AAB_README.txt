أذكار المسلم - نسخة البناء المصححة

التعديلات:
- توحيد AGP/Kotlin/KSP/Gradle إلى مجموعة متوافقة.
- AGP 9.0.0
- Kotlin 2.3.20
- KSP 2.3.10
- Gradle 9.1.0
- إصلاح إعداد توقيع Release ليقرأ KEYSTORE_PATH / STORE_PASSWORD / KEY_PASSWORD / KEY_ALIAS.
- تعطيل configuration cache لتجنب مشاكل إضافية مع KSP أثناء إصدار Release.
- تحديث GitHub Actions لبناء app-release.aab مباشرة.

GitHub Secrets المطلوبة:
KEYSTORE_BASE64
STORE_PASSWORD
KEY_PASSWORD
KEY_ALIAS

قيمة KEY_ALIAS يجب أن تطابق الـalias الموجود داخل ملف JKS (مثلاً upload).

لا تضع ملف JKS داخل GitHub repository.
