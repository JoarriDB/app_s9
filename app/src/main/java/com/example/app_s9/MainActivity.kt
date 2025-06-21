package com.example.app_s9

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    // Contador de visitas
    private lateinit var textViewVisitCount: TextView
    private lateinit var buttonResetCounter: Button

    // Perfil de usuario
    private lateinit var editTextName: EditText
    private lateinit var editTextAge: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonSaveProfile: Button
    private lateinit var buttonLoadProfile: Button

    // Modo oscuro/claro
    private lateinit var switchDarkMode: Switch
    private lateinit var textViewModeStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar SharedPreferencesHelper
        sharedPreferencesHelper = SharedPreferencesHelper(this)

        // Inicializar vistas
        initViews()

        // Configurar listeners
        setupListeners()

        // Configuraciones iniciales
        setupVisitCounter()
        loadUserProfile()
        setupDarkMode()
    }

    private fun initViews() {
        // Contador de visitas
        textViewVisitCount = findViewById(R.id.textViewVisitCount)
        buttonResetCounter = findViewById(R.id.buttonResetCounter)

        // Perfil de usuario
        editTextName = findViewById(R.id.editTextName)
        editTextAge = findViewById(R.id.editTextAge)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile)
        buttonLoadProfile = findViewById(R.id.buttonLoadProfile)

        // Modo oscuro/claro
        switchDarkMode = findViewById(R.id.switchDarkMode)
        textViewModeStatus = findViewById(R.id.textViewModeStatus)
    }

    private fun setupListeners() {
        // Contador de visitas
        buttonResetCounter.setOnClickListener {
            resetVisitCounter()
        }

        // Perfil de usuario
        buttonSaveProfile.setOnClickListener {
            saveUserProfile()
        }

        buttonLoadProfile.setOnClickListener {
            loadUserProfile()
        }

        // Modo oscuro/claro
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            toggleDarkMode(isChecked)
        }
    }

    // FUNCIONALIDAD 1: CONTADOR DE VISITAS
    private fun setupVisitCounter() {
        // Incrementar el contador cada vez que se abre la app
        val currentCount = sharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_VISIT_COUNT, 0)
        val newCount = currentCount + 1
        sharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_VISIT_COUNT, newCount)

        // Mostrar el contador
        updateVisitCountDisplay()
    }

    private fun updateVisitCountDisplay() {
        val visitCount = sharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_VISIT_COUNT, 0)
        textViewVisitCount.text = "🔢 La app se ha abierto $visitCount veces"
    }

    private fun resetVisitCounter() {
        sharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_VISIT_COUNT, 0)
        updateVisitCountDisplay()
        Toast.makeText(this, "Contador reiniciado", Toast.LENGTH_SHORT).show()
    }

    // FUNCIONALIDAD 2: PERFIL DE USUARIO
    private fun saveUserProfile() {
        val name = editTextName.text.toString().trim()
        val ageText = editTextAge.text.toString().trim()
        val email = editTextEmail.text.toString().trim()

        // Validaciones básicas
        if (name.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show()
            return
        }

        if (ageText.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu edad", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu email", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val age = ageText.toInt()
            if (age <= 0 || age > 150) {
                Toast.makeText(this, "Por favor ingresa una edad válida", Toast.LENGTH_SHORT).show()
                return
            }

            // Guardar en SharedPreferences
            sharedPreferencesHelper.saveString(SharedPreferencesHelper.KEY_USER_NAME, name)
            sharedPreferencesHelper.saveInt(SharedPreferencesHelper.KEY_USER_AGE, age)
            sharedPreferencesHelper.saveString(SharedPreferencesHelper.KEY_USER_EMAIL, email)

            Toast.makeText(this, "✅ Perfil guardado exitosamente", Toast.LENGTH_SHORT).show()

            // Limpiar campos
            editTextName.setText("")
            editTextAge.setText("")
            editTextEmail.setText("")

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Por favor ingresa una edad válida", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserProfile() {
        val name = sharedPreferencesHelper.getString(SharedPreferencesHelper.KEY_USER_NAME, "")
        val age = sharedPreferencesHelper.getInt(SharedPreferencesHelper.KEY_USER_AGE, 0)
        val email = sharedPreferencesHelper.getString(SharedPreferencesHelper.KEY_USER_EMAIL, "")

        if (name.isNotEmpty()) {
            editTextName.setText(name)
            editTextAge.setText(age.toString())
            editTextEmail.setText(email)
            Toast.makeText(this, "📂 Perfil cargado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "❌ No hay perfil guardado", Toast.LENGTH_SHORT).show()
        }
    }

    // FUNCIONALIDAD 3: MODO OSCURO/CLARO
    private fun setupDarkMode() {
        // Cargar preferencia guardada
        val isDarkMode = sharedPreferencesHelper.getBoolean(SharedPreferencesHelper.KEY_DARK_MODE, false)
        switchDarkMode.isChecked = isDarkMode
        applyTheme(isDarkMode)
    }

    private fun toggleDarkMode(isEnabled: Boolean) {
        // Guardar preferencia
        sharedPreferencesHelper.saveBoolean(SharedPreferencesHelper.KEY_DARK_MODE, isEnabled)

        // Aplicar tema
        applyTheme(isEnabled)

        Toast.makeText(
            this,
            if (isEnabled) "🌙 Modo oscuro activado" else "☀️ Modo claro activado",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun applyTheme(isDarkMode: Boolean) {
        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)

        if (isDarkMode) {
            // MODO OSCURO - Colores mejorados
            val darkBg = Color.parseColor("#1A1A1A")
            val darkCardBg = Color.parseColor("#2D2D2D")
            val darkText = Color.parseColor("#E0E0E0")
            val whiteText = Color.parseColor("#FFFFFF")

            // Fondo principal
            mainLayout.setBackgroundColor(darkBg)
            window.statusBarColor = darkBg

            // Actualizar texto de estado con fondo más visible
            textViewModeStatus.text = "🌙 Modo Oscuro: Fondo gris con texto blanco"
            textViewModeStatus.setTextColor(whiteText)

            // Aplicar fondo especial para el texto de estado en modo oscuro
            val statusDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#4CAF50"))
                cornerRadius = 24f
            }
            textViewModeStatus.background = statusDrawable

            // Mejorar el contador de visitas
            textViewVisitCount.setTextColor(whiteText)
            val countDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#424242"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#4CAF50"))
            }
            textViewVisitCount.background = countDrawable

            // Aplicar estilo a las tarjetas (secciones)
            applyCardStyle(findViewById(R.id.cardSection1), darkCardBg, darkText)
            applyCardStyle(findViewById(R.id.cardSection2), darkCardBg, darkText)
            applyCardStyle(findViewById(R.id.cardSection3), darkCardBg, darkText)

            // Actualizar el contenedor del switch en modo oscuro
            updateSwitchContainer(true)

            // Aplicar estilo a los EditTexts con mejor contraste
            applyEditTextStyleDark(editTextName)
            applyEditTextStyleDark(editTextAge)
            applyEditTextStyleDark(editTextEmail)

            // Aplicar estilo a los botones con colores más vibrantes
            applyButtonStyle(buttonResetCounter, Color.parseColor("#FF6B6B"))
            applyButtonStyle(buttonSaveProfile, Color.parseColor("#51CF66"))
            applyButtonStyle(buttonLoadProfile, Color.parseColor("#FFD43B"))

        } else {
            // MODO CLARO - Colores frescos
            val lightBg = Color.parseColor("#F5F5F5")
            val lightCardBg = Color.parseColor("#FFFFFF")
            val lightText = Color.parseColor("#212121")

            // Fondo principal
            mainLayout.setBackgroundColor(lightBg)
            window.statusBarColor = Color.parseColor("#FFFFFF")

            // Actualizar texto de estado
            textViewModeStatus.text = "☀️ Modo Claro: Fondo blanco, texto negro"
            textViewModeStatus.setTextColor(Color.parseColor("#4CAF50"))

            // Aplicar fondo original para modo claro
            val statusDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#E8F5E8"))
                cornerRadius = 24f
            }
            textViewModeStatus.background = statusDrawable

            // Restaurar estilo del contador
            textViewVisitCount.setTextColor(Color.parseColor("#2196F3"))
            val countDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#E3F2FD"))
                cornerRadius = 24f
            }
            textViewVisitCount.background = countDrawable

            // Aplicar estilo a las tarjetas (secciones)
            applyCardStyle(findViewById(R.id.cardSection1), lightCardBg, lightText)
            applyCardStyle(findViewById(R.id.cardSection2), lightCardBg, lightText)
            applyCardStyle(findViewById(R.id.cardSection3), lightCardBg, lightText)

            // Actualizar el contenedor del switch en modo claro
            updateSwitchContainer(false)

            // Aplicar estilo a los EditTexts
            applyEditTextStyleLight(editTextName)
            applyEditTextStyleLight(editTextAge)
            applyEditTextStyleLight(editTextEmail)

            // Aplicar estilo a los botones
            applyButtonStyle(buttonResetCounter, Color.parseColor("#2196F3"))
            applyButtonStyle(buttonSaveProfile, Color.parseColor("#4CAF50"))
            applyButtonStyle(buttonLoadProfile, Color.parseColor("#FF9800"))
        }
    }

    // Función específica para actualizar el contenedor del switch
    private fun updateSwitchContainer(isDarkMode: Boolean) {
        val switchContainer = findViewById<LinearLayout>(R.id.switchContainer)
        val switchLabel = findViewById<TextView>(R.id.switchLabel)

        if (isDarkMode) {
            // Modo oscuro - fondo más visible y texto blanco
            val drawable = GradientDrawable().apply {
                setColor(Color.parseColor("#424242"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#9C27B0"))
            }
            switchContainer.background = drawable
            switchLabel.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            // Modo claro - fondo original
            val drawable = GradientDrawable().apply {
                setColor(Color.parseColor("#F3E5F5"))
                cornerRadius = 24f
                setStroke(1, Color.parseColor("#9C27B0"))
            }
            switchContainer.background = drawable
            switchLabel.setTextColor(Color.parseColor("#9C27B0"))
        }
    }

    // Función específica para EditTexts en modo oscuro
    private fun applyEditTextStyleDark(editText: EditText) {
        val drawable = GradientDrawable().apply {
            setColor(Color.parseColor("#424242"))
            cornerRadius = 24f
            setStroke(2, Color.parseColor("#616161"))
        }
        editText.background = drawable
        editText.setTextColor(Color.parseColor("#FFFFFF"))
        editText.setHintTextColor(Color.parseColor("#BDBDBD"))
    }

    // Función específica para EditTexts en modo claro
    private fun applyEditTextStyleLight(editText: EditText) {
        val drawable = GradientDrawable().apply {
            setColor(Color.parseColor("#FAFAFA"))
            cornerRadius = 24f
            setStroke(2, Color.parseColor("#E0E0E0"))
        }
        editText.background = drawable
        editText.setTextColor(Color.parseColor("#212121"))
        editText.setHintTextColor(Color.parseColor("#757575"))
    }

    // Función para aplicar estilo a las tarjetas
    private fun applyCardStyle(view: androidx.cardview.widget.CardView?, backgroundColor: Int, textColor: Int) {
        view?.let {
            it.setCardBackgroundColor(backgroundColor)
            // Actualizar todos los TextView dentro de la tarjeta
            updateTextViewsInContainer(it, textColor)
        }
    }

    // Función para aplicar estilo a EditTexts - removida porque ya tenemos las específicas

    // Función para aplicar estilo a botones
    private fun applyButtonStyle(button: Button, backgroundColor: Int) {
        val drawable = GradientDrawable().apply {
            setColor(backgroundColor)
            cornerRadius = 24f
        }
        button.background = drawable
        button.setTextColor(Color.WHITE)
    }

    // Función para actualizar TextViews dentro de un contenedor
    private fun updateTextViewsInContainer(container: android.view.ViewGroup, textColor: Int) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            when (child) {
                is TextView -> child.setTextColor(textColor)
                is android.view.ViewGroup -> updateTextViewsInContainer(child, textColor)
            }
        }
    }
}