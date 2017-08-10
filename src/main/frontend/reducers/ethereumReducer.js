import {
    GET_INFO_REQUEST, GET_INFO_SUCCESS, GET_INFO_FAILURE
    } from '../actions/EthereumActions';

export const initialState = {
    isFetching: false,
    info: {}
};

const reducer = (state = initialState, action) => {
    switch (action.type) {
        case GET_INFO_REQUEST:
            return Object.assign({}, state, {
                isFetching: true,
                info: {}
            });
        case GET_INFO_SUCCESS:
            return Object.assign({}, state, {
                isFetching: false,
                errorMessage: '',
                info: action.payload
            });
        case GET_INFO_FAILURE:
            return Object.assign({}, state, {
                isFetching: false,
                errorMessage: action.error,
                info: {}
            });
        default:
            return state
    }
};

export default reducer;
