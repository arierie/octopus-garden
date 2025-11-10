package work.arie.octopusgarden.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import work.arie.octopusgarden.R
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
    onBuildClick: () -> Unit,
) {
    val backgroundColor = Color(0xFFF5E6D3)
    val textColor = Color(0xFF2C2C2C)
    val greyTextColor = Color(0xFF8A8A8A)
    val lightBulbColor = Color(0xFFE6FF4D)
    val scrollState = rememberScrollState()
    val isScrolledUp = scrollState.value > 100
    val fabVisible = !isScrolledUp

    val bodyTextFieldValue = remember(uiModel.body) {
        TextFieldValue(
            text = uiModel.body,
            selection = TextRange(uiModel.body.length)
        )
    }

    val icon = if (uiModel.isLoading) {
        painterResource(id = R.drawable.ic_hourglass)
    } else {
        painterResource(id = R.drawable.ic_magic_wand)
    }
    val rotation = if (uiModel.isLoading) {
        val transition = rememberInfiniteTransition(label = "rotate")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotate"
        ).value
    } else {
        0f
    }
    val context = LocalContext.current

    LaunchedEffect(uiModel.errorMessage) {
        if (uiModel.errorMessage.isNotEmpty()) {
            Toast.makeText(context, uiModel.errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(uiModel.body, uiModel.isLoading) {
        if (uiModel.body.isNotEmpty() && !uiModel.isLoading) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Sketches",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState)
            ) {
                BasicTextField(
                    value = uiModel.title,
                    onValueChange = onTitleChange,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    ),
                    decorationBox = { innerTextField ->
                        if (uiModel.title.isEmpty()) {
                            Text(
                                text = "Song title...",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = greyTextColor
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                BasicTextField(
                    value = bodyTextFieldValue,
                    onValueChange = { newValue ->
                        onBodyChange(newValue.text)
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        color = textColor,
                        lineHeight = 24.sp
                    ),
                    decorationBox = { innerTextField ->
                        if (uiModel.body.isEmpty()) {
                            Text(
                                text = "Write your lyrics prompt here...\ne.g. 'here comes the sun'",
                                color = greyTextColor,
                                fontSize = 16.sp,
                                lineHeight = 24.sp
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 120.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = fabVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            IconButton(
                enabled = !uiModel.isLoading,
                onClick = onBuildClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = lightBulbColor,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = icon,
                    contentDescription = "AI assistant: generate lyrics",
                    tint = textColor,
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer { rotationZ = rotation }
                )
            }
        }
    }
}
