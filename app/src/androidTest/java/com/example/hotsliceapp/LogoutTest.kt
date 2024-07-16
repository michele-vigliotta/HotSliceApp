import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.MainActivity
import org.junit.Test

class LogoutTest {


    @Test
    fun testLogoutButtonClick() {

        // Avvia MainActivity utilizzando ActivityScenario
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->


            Thread.sleep(4000)

            // Verifica che il bottone di logout sia visibile
            onView(withId(R.id.logout_button))
                .check(matches(isDisplayed()))

            // Clicca sul bottone di logout
            onView(withId(R.id.logout_button))
                .perform(click())

            // Verifica che Login sia visibile dopo il logout
            onView(withId(R.id.loginActivityRoot))
                .check(matches(isDisplayed()))
        }
    }
}