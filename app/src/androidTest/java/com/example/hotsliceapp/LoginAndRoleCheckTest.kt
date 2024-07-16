import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.hotsliceapp.R
import com.example.hotsliceapp.activities.Login
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginAndRoleCheckTest {

    private lateinit var firebaseAuth: FirebaseAuth

    @get:Rule
    val activityRule = ActivityScenarioRule(Login::class.java)

    @Before
    fun setUp() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
    }

    @After
    fun tearDown() {
        firebaseAuth.signOut()
    }

    //Login utente ruolo staff
    @Test
    fun testLoginStaff() {
        val testEmail = "staff@gmail.com"
        val testPassword = "staffstaff"

            onView(withId(R.id.etEmail)).perform(typeText(testEmail),closeSoftKeyboard())
            onView(withId(R.id.etPassword)).perform(typeText(testPassword), closeSoftKeyboard())

            //Clicca su login
            onView(withId(R.id.btnLogin)).perform(click())

            Thread.sleep(3000)

            onView(withId(R.id.bottomNavigationView)).check(ViewAssertions.matches(isDisplayed()))
            onView(withId(R.id.bottom_offerte)).check(ViewAssertions.matches(isDisplayed()))
            onView(withId(R.id.bottom_home)).check(ViewAssertions.matches(isDisplayed()))
            onView(withId(R.id.bottom_ordini)).check(ViewAssertions.matches(isDisplayed()))
        }

    //Login utente ruolo admin
    @Test
    fun testLoginAdmin() {
        val testEmail = "admin@gmail.com"
        val testPassword = "adminadmin"

        onView(withId(R.id.etEmail)).perform(typeText(testEmail),closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText(testPassword), closeSoftKeyboard())

        //Clicca su login
        onView(withId(R.id.btnLogin)).perform(click())

        Thread.sleep(3000)

        onView(withId(R.id.bottomNavigationView)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.bottom_home)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.bottom_statistiche)).check(ViewAssertions.matches(isDisplayed()))
    }
}
