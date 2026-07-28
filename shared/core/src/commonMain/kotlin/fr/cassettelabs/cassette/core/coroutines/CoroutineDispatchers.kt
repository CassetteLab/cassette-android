package fr.cassettelabs.cassette.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
    val mainImmediate: CoroutineDispatcher
    val io: CoroutineDispatcher
}
