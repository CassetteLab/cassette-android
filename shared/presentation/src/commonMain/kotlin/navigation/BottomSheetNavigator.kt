@file:OptIn(ExperimentalMaterial3Api::class)

package navigation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.util.fastForEach
import androidx.navigation.FloatingWindow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorState
import androidx.navigation.compose.LocalOwnersProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

public class BottomSheetNavigatorSheetState(private val sheetState: SheetState) {
    public val isVisible: Boolean
        get() = sheetState.isVisible

    public val currentValue: SheetValue
        get() = sheetState.currentValue

    public val targetValue: SheetValue
        get() = sheetState.targetValue
}

@Composable
public fun rememberBottomSheetNavigator(
    skipPartiallyExpanded: Boolean = false,
): BottomSheetNavigator {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    return remember(sheetState) { BottomSheetNavigator(sheetState) }
}

@NavigatorName("bottomSheet")
public open class BottomSheetNavigator(
    internal open val sheetState: SheetState
) : Navigator<BottomSheetNavigator.Destination>() {

    internal var sheetEnabled by mutableStateOf(false)
        private set

    private var attached by mutableStateOf(false)

    private val backStack: StateFlow<List<NavBackStackEntry>>
        get() = if (attached) {
            state.backStack
        } else {
            MutableStateFlow(emptyList())
        }

    private val transitionsInProgress: StateFlow<Set<NavBackStackEntry>>
        get() = if (attached) {
            state.transitionsInProgress
        } else {
            MutableStateFlow(emptySet())
        }

    public val navigatorSheetState: BottomSheetNavigatorSheetState =
        BottomSheetNavigatorSheetState(sheetState)

    internal var sheetContent: @Composable ColumnScope.() -> Unit = {}
    internal var onDismissRequest: () -> Unit = {}

    private var animateToDismiss: () -> Unit = {}

    internal val sheetInitializer: @Composable () -> Unit = {
        val saveableStateHolder = rememberSaveableStateHolder()
        val transitionsInProgressEntries by transitionsInProgress.collectAsState()

        val retainedEntry by produceState<NavBackStackEntry?>(
            initialValue = null,
            key1 = backStack
        ) {
            backStack
                .transform { backStackEntries ->
                    try {
                        sheetEnabled = false
                    } catch (_: CancellationException) {
                    } finally {
                        emit(backStackEntries.lastOrNull())
                    }
                }
                .collect {
                    value = it
                }
        }

        val entry = retainedEntry
        if (entry != null) {
            val currentOnSheetShown by rememberUpdatedState {
                transitionsInProgressEntries.forEach(state::markTransitionComplete)
            }
            LaunchedEffect(sheetState, entry) {
                snapshotFlow { sheetState.isVisible }
                    .distinctUntilChanged()
                    .drop(1)
                    .collect { visible ->
                        if (visible) {
                            currentOnSheetShown()
                        }
                    }
            }
            val scope = rememberCoroutineScope()
            LaunchedEffect(key1 = entry) {
                sheetEnabled = true

                sheetContent = {
                    entry.LocalOwnersProvider(saveableStateHolder) {
                        val content =
                            (entry.destination as Destination).content
                        content(entry)
                    }
                }
                val dismissRequest = {
                    sheetEnabled = false

                    if (state.transitionsInProgress.value.contains(entry)) {
                        state.markTransitionComplete(entry)
                    } else if (state.backStack.value.contains(entry)) {
                        state.pop(popUpTo = entry, saveState = false)
                    }
                }
                onDismissRequest = dismissRequest

                animateToDismiss = {
                    scope.launch {
                        sheetState.hide()
                        dismissRequest()
                    }
                }

            }
        } else {
            LaunchedEffect(key1 = Unit) {
                sheetContent = {}
                onDismissRequest = {}
            }
        }
    }

    override fun onAttach(state: NavigatorState) {
        super.onAttach(state)
        attached = true
    }

    override fun createDestination(): Destination = Destination(
        navigator = this as BottomSheetNavigator,
        content = {}
    )

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.fastForEach { entry ->
            state.push(entry)
        }
    }

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        animateToDismiss()
    }

    public class Destination(
        navigator: BottomSheetNavigator,
        internal val content: @Composable ColumnScope.(NavBackStackEntry) -> Unit
    ) : NavDestination(navigator), FloatingWindow
}
