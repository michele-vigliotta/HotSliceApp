package com.example.hotsliceapp.activities

import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hotsliceapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RegisterTest {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var scenario: ActivityScenario<Register>


    @get:Rule
    val activityRule = ActivityScenarioRule(Register::class.java)

    @Before
    fun setUp() {
        firebaseAuth = FirebaseAuth.getInstance()
        scenario = ActivityScenario.launch(Register::class.java)
    }

    @After
    fun tearDown() {

        scenario.close()
        firebaseAuth.signOut()
    }

    @Test
    fun testRegistrazioneConSuccesso() {
        val testEmail = "test@example.com"
        val testPassword = "password123"

        firebaseAuth.signOut()

        // Compila le edit text
        onView(withId(R.id.etEmail)).perform(typeText(testEmail), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText(testPassword), closeSoftKeyboard())
        onView(withId(R.id.etConfermaPassword)).perform(typeText(testPassword), closeSoftKeyboard())


        // Clicca sul pulsante di registrazione
        onView(withId(R.id.btnRegister)).perform(click())


        Thread.sleep(3000)

        // Verifica che l'Activity di login sia visibile
        onView(withId(R.id.loginActivityRoot))
            .check(matches(isDisplayed()))

        // Elimina l'utente appena creato
        firebaseAuth.signInWithEmailAndPassword(testEmail, testPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseAuth.currentUser?.delete()?.addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        // Verifica che l'utente sia stato eliminato
                        firebaseAuth.signOut()
                        Log.d("RegisterActivityTest", "Utente eliminato con successo")
                    } else {
                        Log.e("RegisterActivityTest", "Errore durante l'eliminazione dell'utente: ${deleteTask.exception?.message}")
                    }
                }
            } else {
                Log.e("RegisterActivityTest", "Errore durante l'accesso per l'eliminazione: ${task.exception?.message}")
            }
        }
    }
        

    @Test
    fun testEmailGiaInUso(){
        val testEmail = "user@gmail.com"
        val testPassword = "prova123"
        val testConfermaPassword = "prova123"

        firebaseAuth.signOut()

        val initialLatch = CountDownLatch(1)
        firebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword)
            .addOnCompleteListener { initialTask ->
                initialLatch.countDown()
            }
        initialLatch.await(10, TimeUnit.SECONDS)


        // Compila le edit text
        onView(withId(R.id.etEmail)).perform(typeText(testEmail), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).perform(typeText(testPassword), closeSoftKeyboard())
        onView(withId(R.id.etConfermaPassword)).perform(typeText(testConfermaPassword), closeSoftKeyboard())

        onView(withId(R.id.btnRegister)).perform(click())

        // Listener per attendere la risposta di Firebase
        val latch = CountDownLatch(1)
        var exception: Exception? = null

        firebaseAuth.createUserWithEmailAndPassword(testEmail, testPassword)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    exception = task.exception
                }
                latch.countDown()
            }

        // Attendi fino a 10 secondi che il latch raggiunga zero
        latch.await(10, TimeUnit.SECONDS)

        // Verifica l'eccezione lanciata da Firebase
        if (exception is FirebaseAuthUserCollisionException) {
            // L'eccezione corretta è stata lanciata
            assert(true)
        } else {
            // Un'altra eccezione è stata lanciata
            assert(false) { "Expected FirebaseAuthUserCollisionException but got ${exception?.javaClass?.name}" }
        }
    }
}