package romasku.moneylog.lib

class Store<S, E> (initialState: S, val reducer: (S, E) -> S) {

    interface Subscription {
        fun unsubscribe()
    }

    var state: S = initialState
        private set
    private val subscriptions: MutableList<(S) -> Unit> = mutableListOf()

    fun dispatch(event: E) {
        state = reducer(state, event)
        subscriptions.forEach {
            it(state)
        }
    }

    fun subscribe(listener: (S) -> Unit): Subscription {
        subscriptions.add(listener)
        return object : Subscription {
            override fun unsubscribe() {
                subscriptions.remove(listener)
            }
        }
    }
}
