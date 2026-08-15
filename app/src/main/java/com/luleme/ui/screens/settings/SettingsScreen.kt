package com.luleme.ui.screens.settings

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.luleme.ui.components.CuteSwitch
import com.luleme.ui.components.SettingGroup
import com.luleme.ui.components.SettingItem
import com.luleme.ui.auth.SystemAuth
import com.luleme.ui.theme.CutePink
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    fun toggleSystemLock(enabled: Boolean) {
        if (enabled) {
            if (!SystemAuth.canAuthenticate(context)) {
                Toast.makeText(context, "请先在系统设置中启用锁屏密码或生物识别", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                return
            }
        }
        viewModel.toggleLock(enabled)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 24.dp)
    ) {
        item {
            Text(
                text = "猪猪文本",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }

        item {
            SettingGroup(title = "傻猪[粉丝猪]年龄") {
                var ageSliderValue by remember(uiState.age) { mutableStateOf(uiState.age.toFloat()) }
                val pendingAge = ageSliderValue.roundToInt().coerceIn(18, 100)

                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Face,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        Text(
                            text = "年龄: $pendingAge 岁",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = ageSliderValue,
                        onValueChange = { ageSliderValue = it },
                        onValueChangeFinished = {
                            val savedAge = ageSliderValue.roundToInt().coerceIn(18, 100)
                            if (savedAge != uiState.age) {
                                viewModel.updateAge(savedAge)
                            }
                        },
                        valueRange = 18f..100f,
                        steps = 81,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }

        item {
            SettingGroup(title = "安全与隐私") {
                SettingItem(
                    icon = Icons.Rounded.Lock,
                    title = "应用锁",
                    subtitle = "使用系统锁屏密码或生物识别",
                    iconTint = CutePink,
                    trailingContent = {
                        CuteSwitch(
                            checked = uiState.lockEnabled,
                            onCheckedChange = { toggleSystemLock(it) }
                        )
                    },
                    onClick = { toggleSystemLock(!uiState.lockEnabled) }
                )
            }
        }

        item {
            Text(
                text = "猪猪配置",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
