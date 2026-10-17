package com.example.composelab6

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composelab6.data.DbWorker
import com.example.composelab6.data.User
import com.example.composelab6.data.UserRepository
import com.example.composelab6.ui.theme.ComposeLab6Theme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val prefs = getSharedPreferences("lab6_settings", Context.MODE_PRIVATE)
        val repository = UserRepository.getInstance(applicationContext)
        setContent {
            var darkTheme by remember { mutableStateOf(prefs.getBoolean("dark_theme", false)) }
            ComposeLab6Theme(darkTheme = darkTheme) {
                ComposeLab6App(
                    darkTheme = darkTheme,
                    onToggleTheme = {
                        darkTheme = !darkTheme
                        prefs.edit().putBoolean("dark_theme", darkTheme).apply()
                    },
                    repository = repository,
                    initialDbDelayMs = prefs.getLong("db_delay_ms", 1500L),
                    onSetDbDelay = { prefs.edit().putLong("db_delay_ms", it).apply() }
                )
            }
        }
    }
}

sealed class Screen {
    object Hello : Screen()
    object Login : Screen()
    object Register : Screen()
    object Menu : Screen()
    object Profile : Screen()
    object Settings : Screen()
    object Users : Screen()
    object Admin : Screen()
    object Log : Screen()
    class UserProfile(val user: User) : Screen()
}

@Composable
fun ComposeLab6App(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    repository: UserRepository,
    initialDbDelayMs: Long,
    onSetDbDelay: (Long) -> Unit
) {
    val screenState = remember { mutableStateOf<Screen>(Screen.Hello) }
    val currentUserState = remember { mutableStateOf<User?>(null) }
    val busyState = remember { mutableStateOf(false) }
    val usersState = remember { mutableStateOf<List<User>>(emptyList()) }
    val loginErrorState = remember { mutableStateOf(false) }
    val registerErrorState = remember { mutableStateOf(false) }
    val logState = remember { mutableStateOf("") }
    val dbDelayState = remember { mutableStateOf(initialDbDelayMs) }

    // Handler, привязанный к UI-потоку (Looper.getMainLooper()).
    // Сюда приходят сообщения из рабочих потоков DbWorker.
    var dbWorker: DbWorker? = null
    val handler = remember {
        Handler(Looper.getMainLooper()) { message ->
            when (message.what) {
                DbWorker.MSG_USERS -> {
                    @Suppress("UNCHECKED_CAST")
                    usersState.value = (message.obj as? List<User>) ?: emptyList()
                    busyState.value = false
                }
                DbWorker.MSG_SIGN_IN -> {
                    val user = message.obj as? User
                    if (user == null) {
                        loginErrorState.value = true
                    } else {
                        loginErrorState.value = false
                        currentUserState.value = user
                        screenState.value = Screen.Menu
                    }
                    busyState.value = false
                }
                DbWorker.MSG_SIGN_UP -> {
                    val user = message.obj as? User
                    if (user == null) {
                        registerErrorState.value = true
                    } else {
                        registerErrorState.value = false
                        screenState.value = Screen.Login
                    }
                    busyState.value = false
                }
                DbWorker.MSG_SET_ADMIN -> {
                    // После изменения роли заново загружаем список из БД.
                    dbWorker?.loadUsers()
                }
                DbWorker.MSG_LOG -> {
                    logState.value = (message.obj as? String) ?: ""
                    busyState.value = false
                }
            }
            true
        }
    }
    val worker = remember { DbWorker(repository, handler) }
    dbWorker = worker
    worker.delayMs = dbDelayState.value

    val usersDemo = remember { mutableStateListOf<String>() }
    var addedCount by remember { mutableStateOf(0) }

    val screen = screenState.value
    val busy = busyState.value

    BackHandler(enabled = screen != Screen.Hello) {
        screenState.value = when (val s = screen) {
            Screen.Hello -> Screen.Hello
            Screen.Login -> Screen.Hello
            Screen.Register -> Screen.Login
            Screen.Menu -> Screen.Login
            Screen.Profile, Screen.Settings, Screen.Users -> Screen.Menu
            Screen.Admin -> Screen.Menu
            Screen.Log -> Screen.Admin
            is Screen.UserProfile -> Screen.Admin
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val s = screen) {
            Screen.Hello -> HelloScreen(onContinue = { screenState.value = Screen.Login })

            Screen.Login -> LoginScreen(
                loginError = loginErrorState.value,
                busy = busy,
                dbDelayMs = dbDelayState.value,
                onLogin = { login, password ->
                    busyState.value = true
                    worker.signIn(login, password)
                },
                onRegister = {
                    loginErrorState.value = false
                    screenState.value = Screen.Register
                }
            )

            Screen.Register -> RegisterScreen(
                loginTaken = registerErrorState.value,
                busy = busy,
                dbDelayMs = dbDelayState.value,
                onRegister = { login, password, fio, birth, gender ->
                    busyState.value = true
                    worker.signUp(login, password, fio, birth, gender)
                },
                onCancel = { screenState.value = Screen.Login }
            )

            Screen.Menu -> MenuScreen(
                isAdmin = currentUserState.value?.isAdmin == true,
                onProfile = { screenState.value = Screen.Profile },
                onSettings = { screenState.value = Screen.Settings },
                onUsers = { screenState.value = Screen.Users },
                onAdmin = { screenState.value = Screen.Admin },
                onExit = {
                    currentUserState.value = null
                    screenState.value = Screen.Hello
                }
            )

            Screen.Profile -> ProfileScreen(user = currentUserState.value)

            Screen.Settings -> SettingsScreen(
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
                dbDelayMs = dbDelayState.value,
                onSetDbDelay = {
                    dbDelayState.value = it
                    onSetDbDelay(it)
                    worker.delayMs = it
                }
            )

            Screen.Users -> UsersScreen(
                users = usersDemo,
                onAdd = {
                    addedCount++
                    usersDemo.add("Пользователь " + addedCount)
                }
            )

            Screen.Admin -> {
                LaunchedEffect(Unit) {
                    busyState.value = true
                    worker.loadUsers()
                }
                AdminScreen(
                    users = usersState.value,
                    busy = busy,
                    dbDelayMs = dbDelayState.value,
                    onUserClick = { screenState.value = Screen.UserProfile(it) },
                    onShowLog = { screenState.value = Screen.Log },
                    onBack = { screenState.value = Screen.Menu }
                )
            }

            is Screen.UserProfile -> {
                val fresh = usersState.value.firstOrNull { it.id == s.user.id } ?: s.user
                UserProfileScreen(
                    user = fresh,
                    canManage = currentUserState.value?.isAdmin == true &&
                        currentUserState.value?.id != fresh.id,
                    busy = busy,
                    onSetAdmin = { grant ->
                        busyState.value = true
                        worker.setAdmin(fresh, grant)
                    },
                    onBack = { screenState.value = Screen.Admin }
                )
            }

            Screen.Log -> {
                LaunchedEffect(Unit) {
                    busyState.value = true
                    worker.loadLog()
                }
                LogScreen(
                    logText = logState.value,
                    busy = busy,
                    onBack = { screenState.value = Screen.Admin }
                )
            }
        }
    }
}

@Composable
fun BusyIndicator(busy: Boolean) {
    if (busy) {
        CircularProgressIndicator(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun DbDelayInfo(delayMs: Long) {
    Text(
        text = stringResource(R.string.db_delay_current, delayMs),
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
fun HelloScreen(onContinue: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.hello_text),
            fontSize = 40.sp
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { pressed = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (pressed) stringResource(R.string.btn_pressed) else stringResource(R.string.btn_press))
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_continue))
        }
    }
}

@Composable
fun LoginScreen(
    loginError: Boolean,
    busy: Boolean,
    dbDelayMs: Long,
    onLogin: (String, String) -> Unit,
    onRegister: () -> Unit
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_login),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text(stringResource(R.string.hint_login)) },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.hint_password)) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            isError = loginError
        )
        if (loginError) {
            Text(
                text = stringResource(R.string.error_login),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
        Button(
            onClick = { if (login.isNotBlank() && password.isNotBlank()) onLogin(login, password) },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text(stringResource(R.string.btn_login))
        }
        Button(
            onClick = onRegister,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.btn_register))
        }
        BusyIndicator(busy)
        DbDelayInfo(dbDelayMs)
    }
}

@Composable
fun RegisterScreen(
    loginTaken: Boolean,
    busy: Boolean,
    dbDelayMs: Long,
    onRegister: (String, String, String, String, String) -> Unit,
    onCancel: () -> Unit
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fio by remember { mutableStateOf("") }
    var birthValue by remember { mutableStateOf(TextFieldValue("")) }
    var gender by remember { mutableStateOf("М") }
    var avatarIndex by remember { mutableStateOf(0) }
    val avatars = intArrayOf(
        R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3
    )

    val birth = birthValue.text
    val birthValid = isValidDate(birth)

    fun formatDate(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(8)
        return buildString {
            digits.forEachIndexed { i, ch ->
                if (i == 2 || i == 4) append('.')
                append(ch)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.title_register),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text(stringResource(R.string.hint_login)) },
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                singleLine = true,
                isError = loginTaken
            )
            if (loginTaken) {
                Text(
                    text = stringResource(R.string.error_login_exists),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.hint_password)) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation()
            )
            OutlinedTextField(
                value = fio,
                onValueChange = { fio = it },
                label = { Text(stringResource(R.string.hint_fio)) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = birthValue,
                onValueChange = { tfv ->
                    val formatted = formatDate(tfv.text)
                    birthValue = if (formatted != tfv.text) {
                        TextFieldValue(formatted, selection = TextRange(formatted.length))
                    } else {
                        tfv
                    }
                },
                label = { Text(stringResource(R.string.hint_birth)) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = birth.isNotEmpty() && !birthValid,
                supportingText = {
                    if (birth.isNotEmpty() && !birthValid) {
                        Text(stringResource(R.string.toast_invalid_date), color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            Text(
                text = stringResource(R.string.label_gender),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { gender = "М" }
                ) {
                    RadioButton(selected = gender == "М", onClick = { gender = "М" })
                    Text(stringResource(R.string.gender_m))
                }
                Spacer(Modifier.width(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { gender = "Ж" }
                ) {
                    RadioButton(selected = gender == "Ж", onClick = { gender = "Ж" })
                    Text(stringResource(R.string.gender_f))
                }
            }
            Text(
                text = stringResource(R.string.label_avatar),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                fontSize = 16.sp
            )
            Image(
                painter = painterResource(avatars[avatarIndex]),
                contentDescription = stringResource(R.string.label_avatar),
                modifier = Modifier
                    .size(120.dp)
                    .clickable { avatarIndex = (avatarIndex + 1) % avatars.size }
            )
        }
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Button(
                onClick = {
                    if (login.isNotBlank() && password.isNotBlank() && fio.isNotBlank() &&
                        birth.isNotBlank() && birthValid
                    ) {
                        onRegister(login.trim(), password, fio.trim(), birth, gender)
                    }
                },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_register_submit))
            }
            Button(
                onClick = onCancel,
                enabled = !busy,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.btn_cancel))
            }
            BusyIndicator(busy)
            DbDelayInfo(dbDelayMs)
        }
    }
}

@Composable
fun MenuScreen(
    isAdmin: Boolean,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onUsers: () -> Unit,
    onAdmin: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_menu),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(24.dp))
        MenuButton(stringResource(R.string.btn_profile), onProfile)
        MenuButton(stringResource(R.string.btn_settings), onSettings)
        MenuButton(stringResource(R.string.btn_users), onUsers)
        if (isAdmin) {
            MenuButton(stringResource(R.string.btn_admin), onAdmin)
        }
        MenuButton(stringResource(R.string.btn_exit), onExit)
    }
}

@Composable
fun MenuButton(label: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(label)
    }
}

@Composable
fun ProfileScreen(user: User?) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_profile),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        DetailRow(stringResource(R.string.label_name), user?.fio ?: "-")
        DetailRow(stringResource(R.string.label_login), user?.login ?: "-")
        DetailRow(stringResource(R.string.label_birth), user?.birth ?: "-")
        DetailRow(stringResource(R.string.label_gender), user?.gender ?: "-")
        DetailRow(
            stringResource(R.string.label_role),
            stringResource(
                if (user?.isAdmin == true) R.string.role_admin else R.string.role_user
            )
        )
    }
}

@Composable
fun UserProfileScreen(
    user: User,
    canManage: Boolean,
    busy: Boolean,
    onSetAdmin: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_profile),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        DetailRow(stringResource(R.string.label_name), user.fio)
        DetailRow(stringResource(R.string.label_login), user.login)
        DetailRow(stringResource(R.string.label_birth), user.birth)
        DetailRow(stringResource(R.string.label_gender), user.gender)
        DetailRow(
            stringResource(R.string.label_role),
            stringResource(if (user.isAdmin) R.string.role_admin else R.string.role_user)
        )
        if (canManage) {
            Button(
                onClick = { onSetAdmin(!user.isAdmin) },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) {
                Text(
                    stringResource(
                        if (user.isAdmin) R.string.btn_remove_admin else R.string.btn_make_admin
                    )
                )
            }
        }
        Button(
            onClick = onBack,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.btn_back))
        }
        BusyIndicator(busy)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Text(text = label + ": ", fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}

@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    dbDelayMs: Long,
    onSetDbDelay: (Long) -> Unit
) {
    val delayPresets = longArrayOf(0L, 500L, 1500L, 3000L)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_settings),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            Text(text = stringResource(R.string.night_mode), modifier = Modifier.weight(1f))
            Switch(checked = darkTheme, onCheckedChange = { onToggleTheme() })
        }
        Text(
            text = stringResource(R.string.label_db_delay),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
        )
        Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            delayPresets.forEach { value ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSetDbDelay(value) }
                ) {
                    RadioButton(
                        selected = dbDelayMs == value,
                        onClick = { onSetDbDelay(value) }
                    )
                    Text(stringResource(R.string.db_delay_value, value))
                }
            }
        }
    }
}

@Composable
fun UsersScreen(users: List<String>, onAdd: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_users),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)
        ) {
            items(users) { item ->
                Text(
                    text = item,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                )
            }
        }
        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text(stringResource(R.string.btn_add))
        }
    }
}

@Composable
fun AdminScreen(
    users: List<User>,
    busy: Boolean,
    dbDelayMs: Long,
    onUserClick: (User) -> Unit,
    onShowLog: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_admin),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        DbDelayInfo(dbDelayMs)
        if (busy) {
            Text(text = stringResource(R.string.busy_db), fontSize = 14.sp)
            BusyIndicator(true)
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)
        ) {
            items(users, key = { it.id }) { u ->
                Text(
                    text = u.login + " (" + u.fio + ")" +
                        if (u.isAdmin) " " + stringResource(R.string.admin_badge) else "",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !busy) { onUserClick(u) }
                        .padding(12.dp)
                )
            }
        }
        Button(
            onClick = onShowLog,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.btn_show_log))
        }
        Button(
            onClick = onBack,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
        ) {
            Text(stringResource(R.string.btn_back))
        }
    }
}

@Composable
fun LogScreen(logText: String, busy: Boolean, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_log),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        if (busy) {
            BusyIndicator(true)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp)
        ) {
            Text(text = logText, fontSize = 14.sp)
        }
        Button(
            onClick = onBack,
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
        ) {
            Text(stringResource(R.string.btn_back))
        }
    }
}

fun isValidDate(text: String): Boolean {
    val match = Regex("""(\d{2})\.(\d{2})\.(\d{4})""").matchEntire(text) ?: return false
    val (dayStr, monthStr, yearStr) = match.destructured
    val year = yearStr.toInt()
    if (year < 1900 || year > 2099) return false
    val calendar = Calendar.getInstance()
    calendar.isLenient = false
    calendar.clear()
    calendar.set(year, monthStr.toInt() - 1, dayStr.toInt())
    return try {
        calendar.time
        true
    } catch (e: Exception) {
        false
    }
}
