package romasku.moneylog.lib

private data class State<BS, V>(val viewCache: Map<BS, V> = mapOf(), val view: V)

class ViewNavigator<VS, V>(
    initial: List<VS>,
    subscribeForStack: ((List<VS>) -> Unit) -> Unit,
    makeView: (VS) -> V
) {

    private val store = Store<State<VS, V>, List<VS>, Unit>(
        init = defInit {
            val viewStore = initial.last()
            val view = makeView(viewStore)
            Pair(State(mapOf(viewStore to view), view), null)
        },
        update = defUpdate { state, stack ->
            val filteredCache = state.viewCache.filterKeys { it in stack }
            val viewStore = stack.last()
            val view = filteredCache[viewStore] ?: makeView(viewStore)
            Pair(
                State(
                    filteredCache + mapOf(viewStore to view),
                    view,
                ),
                null
            )
        },
        doCommand = defDoCommand { }
    )

    val view get() = store.current.view
    val viewCache get() = store.current.viewCache

    init {
        subscribeForStack { store.dispatch(it) }
    }

    fun subscribe(callback: (V) -> Unit) = store.subscribe { callback(it.view) }
}

fun <VS, V> Navigator<VS, *>.makeUINavigator(
    makeView: (VS) -> V
) = ViewNavigator(current, ::subscribe, makeView)
