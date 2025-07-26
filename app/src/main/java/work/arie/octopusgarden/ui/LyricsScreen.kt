package work.arie.octopusgarden.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import work.arie.octopusgarden.model.UiState

@Composable
fun LyricsScreen() {
    val viewModel = hiltViewModel<LyricsViewModel>()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val uiModel = uiState.value as? UiState ?: UiState.DEFAULT

    LyricsComponent(
        uiModel = uiModel,
        onTitleChange = viewModel::updateTitle,
        onBodyChange = viewModel::updateBody,
        onBuildClick = viewModel::runInference
    )
}

@Composable
private fun LyricsComponent(
    uiModel: UiState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onBuildClick: () -> Unit
) {
    val backgroundColor = Color(0xFFF5E6D3)
    val textColor = Color(0xFF2C2C2C)
    val lightBulbColor = Color(0xFFE6FF4D)
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logo",
                tint = textColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Sketches",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(scrollState)
        ) {
            BasicTextField(
                value = uiModel.title,
                onValueChange = onTitleChange,
                textStyle = TextStyle(
                    fontSize = 24.sp,
                    color = textColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                decorationBox = { innerTextField ->
                    Box {
                        if (uiModel.title.isEmpty()) {
                            Text(
                                text = "Your song title...",
                                color = textColor.copy(alpha = 0.5f),
                                fontSize = 24.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )

            BasicTextField(
                value = uiModel.body,
                onValueChange = onBodyChange,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = textColor
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (uiModel.body.isEmpty()) {
                            Text(
                                text = "Write your lyrics prompt here...\ne.g. 'here comes the sun'",
                                color = textColor.copy(alpha = 0.5f),
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBuildClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = lightBulbColor,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Build,
                    contentDescription = "Ideas",
                    tint = textColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
