package romasku.moneylog.lib

private data class State<BS, V>(val viewCache: Map<BS, V> = mapOf(), val view: V)

private data class AttachView<V>(val view: V)

class ViewNavigator<VS, V>(
    private val navigator: Navigator<VS, *>,
    makeView: (VS) -> V,
    attachView: (V) -> Unit
) {

    private val store = Store<State<VS, V>, List<VS>, AttachView<V>>(
        init = defInit {
            val viewStore = navigator.current.last()
            val view = makeView(viewStore)
            Pair(State(mapOf(viewStore to view), view), null)
        },
        update = defUpdate { state, stack ->
            val filteredCache = state.viewCache.filterKeys { it in stack }
            val viewStore = stack.last()
            val view = filteredCache.get(viewStore) ?: makeView(viewStore)
            Pair(
                State(
                    filteredCache + mapOf(viewStore to view),
                    view,
                ),
                AttachView(view)
            )
        },
        doCommand = defDoCommand {
            attachView(it.view)
        }
    )

    val view
        get() = store.current.view

    val viewCache
        get() = store.current.viewCache

    init {
        navigator.subscribe { store.dispatch(it) }
    }
}
