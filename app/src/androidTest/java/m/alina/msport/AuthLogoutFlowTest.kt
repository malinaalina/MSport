package m.alina.msport

import android.content.Context
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import m.alina.msport.smscode.SMS_CODE_LENGTH
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthLogoutFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun userCanLogInAndThenLogOut() {
        // phone entering
        composeTestRule.onNodeWithText(PHONE_ENTRY_HEADING).assertExists()
        composeTestRule.onNode(hasSetTextAction()).performTextInput(TEST_PHONE_DIGITS)
        composeTestRule.onNodeWithText(SEND_CODE_ACTION).performClick()
        // SMS code
        waitUntilExists(SMS_CODE_HEADING)
        composeTestRule.onNode(hasSetTextAction()).performTextInput("1".repeat(SMS_CODE_LENGTH))
        composeTestRule.onNodeWithText(CONFIRM_ACTION).performClick()
        // PIN setup
        waitUntilExists(PIN_SETUP_HEADING)
        composeTestRule.onNode(hasSetTextAction()).performTextInput(TEST_PIN)
        // login
        waitUntilExists(MY_CLASSES_TAB_LABEL)
        // profile
        composeTestRule.onNodeWithText(PROFILE_TAB_LABEL).performClick()
        waitUntilExists(LOGOUT_ACTION)
        composeTestRule.onNodeWithText(LOGOUT_ACTION).performClick()
        waitUntilExists(PHONE_ENTRY_HEADING)
    }

    private fun waitUntilExists(text: String, timeoutMillis: Long = 5_000) {
        composeTestRule.waitUntil(timeoutMillis = timeoutMillis) {
            composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    companion object {
        private const val TEST_PHONE_DIGITS = "9991234567"
        private const val TEST_PIN = "1234"

        private const val PHONE_ENTRY_HEADING = "Введите номер телефона"
        private const val SEND_CODE_ACTION = "Отправить код"
        private const val SMS_CODE_HEADING = "Введите код"
        private const val CONFIRM_ACTION = "Подтвердить"
        private const val PIN_SETUP_HEADING = "Задайте код для входа"
        private const val MY_CLASSES_TAB_LABEL = "Мои"
        private const val PROFILE_TAB_LABEL = "Профиль"
        private const val LOGOUT_ACTION = "Выйти"

        private const val SESSION_PREFS_NAME = "msport_prefs"

        @BeforeClass
        @JvmStatic
        fun clearSession() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            context.getSharedPreferences(SESSION_PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        }
    }
}
