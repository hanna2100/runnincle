package com.example.runnincle.framework.presentation.composable


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.vanpra.composematerialdialogs.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

internal class CustomMaterialDialogScopeImpl(
    override val dialogState: MaterialDialogState,
    override val autoDismiss: Boolean = true
) : MaterialDialogScope {
    override val dialogButtons = MaterialDialogButtons(this)

    override val callbacks = mutableStateMapOf<Int, () -> Unit>()
    private val callbackCounter = AtomicInteger(0)

    override val positiveButtonEnabled = mutableStateMapOf<Int, Boolean>()
    private val positiveEnabledCounter = AtomicInteger(0)

    /**
     * Hides the dialog and calls any callbacks from components in the dialog
     */
    override fun submit() {
        dialogState.hide()
        callbacks.values.forEach {
            it()
        }
    }

    /**
     * Clears the dialog callbacks and positive button enables values along with their
     * respective counters
     */
    override fun reset() {
        positiveButtonEnabled.clear()
        callbacks.clear()

        positiveEnabledCounter.set(0)
        callbackCounter.set(0)
    }

    /**
     * Adds a value to the [positiveButtonEnabled] map and updates the value in the map when
     * [valid] changes
     *
     * @param valid boolean value to initialise the index in the list
     * @param onDispose cleanup callback when component calling this gets destroyed
     */
    @Composable
    override fun PositiveButtonEnabled(valid: Boolean, onDispose: () -> Unit) {
        val positiveEnabledIndex = remember { positiveEnabledCounter.getAndIncrement() }

        DisposableEffect(valid) {
            positiveButtonEnabled[positiveEnabledIndex] = valid
            onDispose {
                positiveButtonEnabled[positiveEnabledIndex] = true
                onDispose()
            }
        }
    }

    /**
     * Adds a callback to the dialog which is called on positive button press
     *
     * @param callback called when positive button is pressed
     */
    @Composable
    override fun DialogCallback(callback: () -> Unit) {
        val callbackIndex = rememberSaveable { callbackCounter.getAndIncrement() }

        DisposableEffect(Unit) {
            callbacks[callbackIndex] = callback
            onDispose { callbacks[callbackIndex] = {} }
        }
    }
}

/**
 *  Builds a dialog with the given content
 * @param dialogState state of the dialog
 * @param properties properties of the compose dialog
 * @param backgroundColor background color of the dialog
 * @param shape shape of the dialog and components used in the dialog
 * @param border border stoke of the dialog
 * @param elevation elevation of the dialog
 * @param autoDismiss when true the dialog is hidden on any button press
 * @param onCloseRequest function to be executed when user clicks outside dialog
 * @param buttons the buttons layout of the dialog
 * @param content the body content of the dialog
 */
@Composable
fun CustomMaterialDialog(
    dialogState: MaterialDialogState = rememberMaterialDialogState(),
    properties: DialogProperties = DialogProperties(),
    backgroundColor: Color = MaterialTheme.colors.surface,
    shape: Shape = MaterialTheme.shapes.medium,
    border: BorderStroke? = null,
    elevation: Dp = 24.dp,
    autoDismiss: Boolean = true,
    onCloseRequest: (MaterialDialogState) -> Unit = { it.hide() },
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val dialogScope = remember { CustomMaterialDialogScopeImpl(dialogState, autoDismiss) }
    DisposableEffect(dialogState.showing) {
        if (!dialogState.showing) dialogScope.reset()
        onDispose { }
    }

    BoxWithConstraints {
        val maxHeight = if (isLargeDevice()) {
            LocalConfiguration.current.screenHeightDp.dp - 96.dp
        } else {
            560.dp
        }

        val maxHeightPx = with(LocalDensity.current) { maxHeight.toPx().toInt() }
        val isDialogFullWidth = LocalConfiguration.current.screenWidthDp.dp == maxWidth
        val padding = if (isDialogFullWidth) 16.dp else 0.dp

        if (dialogState.showing) {
            dialogState.dialogBackgroundColor = LocalElevationOverlay.current?.apply(
                color = backgroundColor,
                elevation = elevation
            ) ?: MaterialTheme.colors.surface

            Dialog(
                properties = properties,
                onDismissRequest = { onCloseRequest(dialogState) }
            ) {
                Surface(
                    modifier = Modifier
                        .width(LocalConfiguration.current.screenWidthDp.times(0.7f).dp)
                        .sizeIn(maxHeight = maxHeight, maxWidth = 560.dp)
                        .padding(horizontal = padding)
                        .clipToBounds()
                        .wrapContentHeight()
                        .testTag("dialog"),
                    shape = shape,
                    color = backgroundColor,
                    border = border,
                    elevation = elevation
                ) {
                    Layout(
                        content = {
                            Column(Modifier.layoutId("content")) { content(dialogScope) }
                        }
                    ) { measurables, constraints ->

                        val contentPlaceable = measurables[0].measure(
                            constraints.copy(
                                maxHeight = maxHeightPx,
                                minHeight = 0,
                            )
                        )

                        val height =
                            min(maxHeightPx, contentPlaceable.height)

                        return@Layout layout(constraints.maxWidth, height) {
                            contentPlaceable.place(0, 0)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun isLargeDevice(): Boolean {
    return LocalConfiguration.current.screenWidthDp <= 600
}

