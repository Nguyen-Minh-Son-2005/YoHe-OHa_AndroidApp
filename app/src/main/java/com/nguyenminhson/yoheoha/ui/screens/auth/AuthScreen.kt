package com.nguyenminhson.yoheoha.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nguyenminhson.yoheoha.ui.theme.*
import com.nguyenminhson.yoheoha.ui.util.ValidationUtils
import com.nguyenminhson.yoheoha.ui.viewmodel.MainViewModel

@Composable
fun AuthScreen(
    viewModel: MainViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    val authState by viewModel.authUiState.collectAsState()

    // No hardcoded mock values - pristine empty states
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }

    // Password visibility toggles
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Dialog state for "Quên mật khẩu?"
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Touch / blur tracking to only show errors after user interacts
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }
    var nicknameTouched by remember { mutableStateOf(false) }

    // Validation computations
    val emailError = if (emailTouched && email.isNotEmpty()) ValidationUtils.validateEmail(email) else null
    val passwordError = if (passwordTouched && password.isNotEmpty()) ValidationUtils.validatePassword(password) else null
    val confirmPasswordError = if (confirmPasswordTouched && confirmPassword.isNotEmpty()) {
        ValidationUtils.validateConfirmPassword(password, confirmPassword)
    } else null
    val nicknameError = if (nicknameTouched && nickname.isNotEmpty()) ValidationUtils.validateNickname(nickname) else null

    val isLoginValid = email.isNotBlank() && password.isNotBlank() &&
            ValidationUtils.validateEmail(email) == null &&
            ValidationUtils.validatePassword(password) == null

    val isRegisterValid = isLoginValid && nickname.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            ValidationUtils.validateNickname(nickname) == null &&
            ValidationUtils.validateConfirmPassword(password, confirmPassword) == null

    Scaffold(
        containerColor = ScreenBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "YoHe - OHa",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = HealthyGreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lối sống khỏe mỗi ngày",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tab selector
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = HealthyGreenPrimary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Đăng nhập", fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Đăng ký", fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- NICKNAME (Only for Register) ---
                    if (selectedTab == 1) {
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = {
                                nickname = it
                                nicknameTouched = true
                            },
                            label = { Text("Biệt danh / Tên hiển thị") },
                            placeholder = { Text("Ví dụ: Minh Sơn") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = HealthyGreenPrimary) },
                            isError = nicknameError != null,
                            supportingText = {
                                if (nicknameError != null) {
                                    Text(nicknameError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // --- EMAIL ---
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailTouched = true
                        },
                        label = { Text("Email") },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = HealthyGreenPrimary) },
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(emailError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // --- PASSWORD ---
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordTouched = true
                        },
                        label = { Text("Mật khẩu") },
                        placeholder = { Text("Tối thiểu 6 ký tự") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = HealthyGreenPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (isPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = TextSecondary
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = {
                            if (passwordError != null) {
                                Text(passwordError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // --- CONFIRM PASSWORD (Only for Register) ---
                    if (selectedTab == 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPasswordTouched = true
                            },
                            label = { Text("Xác nhận mật khẩu") },
                            placeholder = { Text("Nhập lại mật khẩu ở trên") },
                            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = HealthyGreenPrimary) },
                            trailingIcon = {
                                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (isConfirmPasswordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = confirmPasswordError != null,
                            supportingText = {
                                if (confirmPasswordError != null) {
                                    Text(confirmPasswordError, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // --- FORGOT PASSWORD (Only for Login) ---
                    if (selectedTab == 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Quên mật khẩu?",
                                color = HealthyGreenPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { showForgotPasswordDialog = true }
                                    .padding(vertical = 4.dp)
                            )
                        }
                    }

                    // Backend Error Message
                    authState.errorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = err,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // SUBMIT BUTTON (Anti-spam: disabled while loading or invalid)
                    val isButtonEnabled = if (selectedTab == 0) isLoginValid else isRegisterValid
                    Button(
                        onClick = {
                            if (selectedTab == 0) {
                                viewModel.login(email, password) { destination ->
                                    if (destination == "main") onNavigateToMain() else onNavigateToOnboarding()
                                }
                            } else {
                                viewModel.register(nickname, email, password, confirmPassword) {
                                    onNavigateToOnboarding()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !authState.isLoading && isButtonEnabled,
                        colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (authState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (selectedTab == 0) "Đăng nhập" else "Đăng ký",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog "Quên mật khẩu?" placeholder
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = HealthyGreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Khôi phục Mật khẩu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Tính năng đặt lại mật khẩu qua Email xác thực và mã OTP đang được lên phương án phát triển và sẽ ra mắt ở bản cập nhật tiếp theo.\n\nNếu bạn quên mật khẩu thử nghiệm, vui lòng liên hệ admin hoặc đăng ký tài khoản mới.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showForgotPasswordDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HealthyGreenPrimary)
                ) {
                    Text("Đã hiểu")
                }
            }
        )
    }
}
