package com.example.hotsliceapp.activities

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hotsliceapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class RegisterTest {

    @get:Rule
    var activityRule = ActivityScenarioRule(Register::class.java)

    private lateinit var decorView: View
    private lateinit var firebaseAuth: FirebaseAuth

    @Before
    fun setUp() {
        activityRule.scenario.onActivity { activity ->
            decorView = activity.window.decorView
            firebaseAuth = mock(FirebaseAuth::class.java)
            activity.firebaseAuth = firebaseAuth
        }
    }

    @Test
    fun testRegisterActivity_successfulRegistration() {
        // Inizializza Espresso Intents
        Intents.init()

        // Simula l'inserimento di dati validi
        onView(withId(R.id.etEmail)).perform(typeText("test@example.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.etConfermaPassword)).perform(typeText("password123"), closeSoftKeyboard())

        // Simula la registrazione riuscita
        val mockAuthResult = mock(AuthResult::class.java)
        val mockTask = mock(Task::class.java) as Task<AuthResult>
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockTask.result).thenReturn(mockAuthResult)
        `when`(firebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask)

        // Clicca sul pulsante di registrazione
        onView(withId(R.id.btnRegister)).perform(click())

        // Verifica che l'Intent per la Login Activity sia stato lanciato
        Intents.intended(hasComponent(Login::class.java.name))

        // Libera Espresso Intents
        Intents.release()
    }
}