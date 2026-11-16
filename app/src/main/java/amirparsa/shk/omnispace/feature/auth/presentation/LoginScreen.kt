package amirparsa.shk.omnispace.feature.auth.presentation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import amirparsa.shk.omnispace.core.designsystem.OmniTheme

private val ColorCanvas = Color(0xFF0B0C0E)
private val ColorCard = Color(0xFF13161C)
private val ColorInputBg = Color(0xFF101319)
private val ColorBorderSubtle = Color(0xFF1F242E)
private val ColorPrimaryViolet = Color(0xFF9D7BF6)
private val ColorTextMuted = Color(0xFF64748B)
private val ColorTextSecondary = Color(0xFF94A3B8)
private val ColorDanger = Color(0xFFEF4444)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.LoginSuccess -> {
                    Toast.makeText(context, "ورود با موفقیت انجام شد", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }
                is LoginEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LoginScreenContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

@Composable
fun LoginScreenContent(
    state: LoginUiState,
    onIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorCanvas)
    ) {
        // Top ambient violet line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            ColorPrimaryViolet.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Radial glow aura in upper section
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(360.dp)
                .blur(80.dp)
                .background(ColorPrimaryViolet.copy(alpha = 0.08f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top minimal bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OmniSpace",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "راهنما",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onIntent(LoginIntent.OpenHelpDialog) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Brand Logo & Header
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .blur(20.dp)
                            .background(ColorPrimaryViolet.copy(alpha = 0.25f), CircleShape)
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(ColorCard)
                            .border(1.dp, ColorBorderSubtle, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RocketLaunch,
                            contentDescription = "OmniSpace Logo",
                            tint = ColorPrimaryViolet,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "OmniSpace",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                Text(
                    text = "فضای کاری هوشمند شما",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "DOTONE SYSTEMS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorTextMuted,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 2. Form Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ColorCard)
                        .border(1.dp, ColorBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ورود به حساب",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Error Banner
                        AnimatedVisibility(
                            visible = state.errorMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            if (state.errorMessage != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(ColorDanger.copy(alpha = 0.12f))
                                        .border(1.dp, ColorDanger.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.WarningAmber,
                                        contentDescription = null,
                                        tint = ColorDanger,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = state.errorMessage,
                                        fontSize = 12.sp,
                                        color = Color(0xFFFCA5A5),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Email Field
                        Text(
                            text = "ایمیل سازمانی",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorTextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ColorInputBg)
                                .border(1.dp, ColorBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = ColorTextMuted,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                BasicTextField(
                                    value = state.email,
                                    onValueChange = { onIntent(LoginIntent.EmailChanged(it)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 14.sp
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(ColorPrimaryViolet),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (state.email.isEmpty()) {
                                            Text(
                                                text = "name@company.com",
                                                color = ColorTextMuted,
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Field
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "رمز عبور",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorTextSecondary
                            )

                            Text(
                                text = "فراموشی رمز عبور؟",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorPrimaryViolet,
                                modifier = Modifier.clickable {
                                    onIntent(LoginIntent.ForgotPasswordClicked)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ColorInputBg)
                                .border(1.dp, ColorBorderSubtle, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = ColorTextMuted,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                BasicTextField(
                                    value = state.password,
                                    onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
                                    modifier = Modifier.weight(1f),
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 14.sp
                                    ),
                                    singleLine = true,
                                    cursorBrush = SolidColor(ColorPrimaryViolet),
                                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            onIntent(LoginIntent.SubmitLogin)
                                        }
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (state.password.isEmpty()) {
                                            Text(
                                                text = "••••••••",
                                                color = ColorTextMuted,
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }

                            IconButton(
                                onClick = { onIntent(LoginIntent.TogglePasswordVisibility) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (state.isPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = "Toggle Password",
                                    tint = ColorTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Primary Login CTA (52dp)
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                onIntent(LoginIntent.SubmitLogin)
                            },
                            enabled = !state.isLoading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorPrimaryViolet,
                                contentColor = Color(0xFF0B0C0E),
                                disabledContainerColor = ColorPrimaryViolet.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color(0xFF0B0C0E),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "ورود",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.Login,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Divider "یا"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = ColorBorderSubtle
                            )
                            Text(
                                text = "یا",
                                fontSize = 12.sp,
                                color = ColorTextMuted,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = ColorBorderSubtle
                            )
                        }

                        // Secondary CTA: "ورود با توکن"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ColorPrimaryViolet.copy(alpha = 0.08f))
                                .border(1.dp, ColorPrimaryViolet.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                .clickable { onIntent(LoginIntent.OpenTokenDialog) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.VpnKey,
                                    contentDescription = null,
                                    tint = ColorPrimaryViolet,
                                    modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    text = "ورود با توکن توسعه‌دهنده",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ColorPrimaryViolet
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 3. Language Selector Pill & Version
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ColorCard)
                        .border(1.dp, ColorBorderSubtle, RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (state.selectedLanguage == "fa") ColorPrimaryViolet.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .border(
                                width = 1.dp,
                                color = if (state.selectedLanguage == "fa") ColorPrimaryViolet.copy(alpha = 0.4f) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onIntent(LoginIntent.SelectLanguage("fa")) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (state.selectedLanguage == "fa") {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ColorPrimaryViolet)
                                )
                            }
                            Text(
                                text = "فارسی",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.selectedLanguage == "fa") ColorPrimaryViolet else ColorTextMuted
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (state.selectedLanguage == "en") ColorPrimaryViolet.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .border(
                                width = 1.dp,
                                color = if (state.selectedLanguage == "en") ColorPrimaryViolet.copy(alpha = 0.4f) else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onIntent(LoginIntent.SelectLanguage("en")) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "English",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (state.selectedLanguage == "en") ColorPrimaryViolet else ColorTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "نسخه ۱.۰.۰",
                    fontSize = 11.sp,
                    color = ColorTextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Token Input Dialog
    if (state.showTokenDialog) {
        AlertDialog(
            onDismissRequest = { onIntent(LoginIntent.CloseTokenDialog) },
            containerColor = ColorCard,
            titleContentColor = Color.White,
            textContentColor = ColorTextSecondary,
            title = {
                Text(
                    text = "ورود با توکن امنیتی / سشن",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "توکن سشن یا کلید دسترسی CSRF دریافت شده از وب‌سایت omnispace.ir را وارد کنید:",
                        fontSize = 13.sp,
                        color = ColorTextSecondary
                    )

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        BasicTextField(
                            value = state.tokenInput,
                            onValueChange = { onIntent(LoginIntent.TokenInputChanged(it)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ColorInputBg)
                                .border(1.dp, ColorBorderSubtle, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                            singleLine = true,
                            cursorBrush = SolidColor(ColorPrimaryViolet),
                            decorationBox = { inner ->
                                if (state.tokenInput.isEmpty()) {
                                    Text("eyJh...", color = ColorTextMuted, fontSize = 13.sp)
                                }
                                inner()
                            }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onIntent(LoginIntent.SubmitToken) },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimaryViolet)
                ) {
                    Text("ورود به سامانه", color = Color(0xFF0B0C0E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(LoginIntent.CloseTokenDialog) }) {
                    Text("انصراف", color = ColorTextMuted)
                }
            }
        )
    }

    // Help Dialog
    if (state.showHelpDialog) {
        AlertDialog(
            onDismissRequest = { onIntent(LoginIntent.CloseHelpDialog) },
            containerColor = ColorCard,
            titleContentColor = Color.White,
            textContentColor = ColorTextSecondary,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = ColorPrimaryViolet
                    )
                    Text("راهنمای ورود به سامانه", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "سامانه OMNISpace یک فضای کاری یکپارچه سازمانی است.",
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Text(
                        text = "برای ورود از ایمیل و کلمه عبور سازمانی خود استفاده نمایید.\nدر صورت بروز مشکل با پشتیبانی فنی یا ادمین سیستم تماس حاصل فرمایید.",
                        fontSize = 12.sp,
                        color = ColorTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onIntent(LoginIntent.CloseHelpDialog) },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimaryViolet)
                ) {
                    Text("متوجه شدم", color = Color(0xFF0B0C0E), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

