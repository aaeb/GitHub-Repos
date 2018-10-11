package com.github.repos.utils

open class AppNetworkState {
    enum class State {
        LOADING,
        LOADED,
        FAILED,
        NAVIGATE
    }

    data class TooliStringState(var state: State, var value: String?) : AppNetworkState()

    data class TooliIntState(var state: State, var value: Int?) : AppNetworkState()

}
